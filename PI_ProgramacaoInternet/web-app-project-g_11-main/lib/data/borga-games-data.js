'use strict'

const fetch = require('node-fetch')
const BOARDGAMEATLAS_HOST = 'https://api.boardgameatlas.com/api/'
const BOARDGAMEATLAS_CLIENT_ID = process.env.ATLAS_CLIENT_ID
const BOARDGAMEATLAS_GET_GAME_ID = `search?client_id=${BOARDGAMEATLAS_CLIENT_ID}&ids=`
const BOARDGAMEATLAS_GET_GAME_NAME = `search?client_id=${BOARDGAMEATLAS_CLIENT_ID}&name=`
const BOARDGAMEATLAS_POPULAR_GAMES = `search?client_id=${BOARDGAMEATLAS_CLIENT_ID}&order_by=rank&limit=`
const BOARDGAMEATLAS_CATEGORIES = `game/categories?client_id=${BOARDGAMEATLAS_CLIENT_ID}`
const BOARDGAMEATLAS_MECHANICS = `game/mechanics?client_id=${BOARDGAMEATLAS_CLIENT_ID}`

if(!BOARDGAMEATLAS_CLIENT_ID) throw Error('ATLAS_CLIENT_ID not set.')

let mechanics = []
let categories = []

async function getpopulargames(number){//number between 1 and 100
    let path = BOARDGAMEATLAS_HOST + BOARDGAMEATLAS_POPULAR_GAMES + number
    //const res = await fetch(path)
    const obj = await fetch(path).then(res => res.json())//res.json()
    let cont = obj.games
    let games = []
    for(let i = 0; i<cont.length; i++){
        games.push(filtergame(cont[i]))
    }
    return games
}
/**
 * @typedef Game
 * @type {Object}
 * @property {String} name Game name
 * @property {String} id Unique id
 * @property {String} url url
 */
async function getgame(gameid){
    let path = BOARDGAMEATLAS_HOST + BOARDGAMEATLAS_GET_GAME_ID + gameid
    const res = await fetch(path)
    if (res.status == 404) return Promise.reject(erro('Game with id ' + gameid + ' does not exit', 404))
    const obj = await res.json()
    return filtergame(obj.games[0])
}

async function getgamename(gamename){
    let path = BOARDGAMEATLAS_HOST + BOARDGAMEATLAS_GET_GAME_NAME + gamename
    const res = await fetch(path)
    if (res.status == 404) return Promise.reject(erro('Game with name ' + gamename + ' does not exit', 404))
    const obj = await res.json()
    let ret = []
    for(let i = 0; i<obj.games.length; i++){
        ret.push(filtergame(obj.games[i]))
    }
    return ret
}

async function getGameInfo(gameid) {
    let prm = undefined
    if(mechanics.length == 0 || categories.length == 0) prm = localInfo()
    let path = BOARDGAMEATLAS_HOST + BOARDGAMEATLAS_GET_GAME_ID + gameid
    console.log(path)
    const res = await fetch(path)
    if (res.status == 404) return Promise.reject(erro('Game with id ' + gameid + ' does not exit', 404))
    let obj = await res.json()
    obj = obj.games[0]
    await prm
    obj = fillCatMech(obj)
    return {
        id : obj.id, 
        name : obj.name, 
        description : obj.description,
        url : obj.url,
        image_url : obj.image_url,
        mechanics : obj.mechanics,
        categories : obj.categories
    }
}

function erro(msg, code){
    return {message: msg, status: code}
}

function filtergame(obj){
    /*
    let game = {}
    game.name = obj.name
    game.url = obj.url
    game.id = obj.id
    */
    return {name: obj.name, url: obj.url, id: obj.id, rating: obj.rating}
}

function fillCatMech(obj){
    let mechs = []
    for(let i = 0; i<obj.mechanics.length; i++){
        mechs.push(mechanics.find(mechanic => mechanic.id == obj.mechanics[i].id))
    }
    obj.mechanics = mechs
    let cats = []
    for(let i = 0; i<obj.categories.length; i++){
        cats.push(categories.find(category => category.id == obj.categories[i].id))
    }
    obj.categories = cats
    return obj
}

async function localInfo(){
    let catpath = BOARDGAMEATLAS_HOST + BOARDGAMEATLAS_CATEGORIES
    let cat = fetch(catpath)
    let mechpath = BOARDGAMEATLAS_HOST + BOARDGAMEATLAS_MECHANICS
    let mech = fetch(mechpath)
    let catres = await cat
    categories = (await catres.json()).categories
    let mechres = await mech
    mechanics = (await mechres.json()).mechanics
}

module.exports = {
    getpopulargames,
    getgame,
    getGameInfo,
    getgamename
}