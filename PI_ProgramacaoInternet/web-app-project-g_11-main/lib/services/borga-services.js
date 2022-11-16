'use strict'
const localdb = require('../data/borga-data-elastic')
//const localdb = require('../data/borga-data-mem')

let bga

function setGamesData(jest = false){
    if (!jest) 
        bga = require('../data/borga-games-data')
    else 
        bga = require('../data/games-data')
} 

/**
 * @typedef User    
 * @type {Object}
 * @property {String} name Unique Name
 * @property {String} mail email
 * @property {String} UUID unique id criado com crypto.randomUUID()
 */
/**
 * @typedef Game
 * @type {Object}
 * @property {String} name Game name
 * @property {String} id Unique id
 * @property {String} url url
 */
/**
 * @typedef Group
 * @type {Object}
 * @property {Number} id UUID of group
 * @property {String} owner UUID of group owner
 * @property {String} name Name
 * @property {String} description description
 * @property {Array.<Game>} games array de games
 */


//web-api

function getpopulargames(number){
    return bga.getpopulargames(number)
}

function getgame(game){
    return bga.getgame(game)
}

function getgamename(game){
    return bga.getgamename(game)
}

function getGameInfo(game){
    return bga.getGameInfo(game)
}

function postuser(name, mail, password){
    return localdb.insertUser(name, mail, password)
}

//web-api-auth

function getUserInfo(name){
    return localdb.getUser(name)
}

function createGroup(name, description, user){
    return localdb.insertGroup(name, description, user)
}

async function editGroup(newname, description, GID, user){
    let group = await localdb.selectGroup(user, GID)
    group.name = newname
    group.description = description
    return localdb.updateGroup(user, group)
}

function getAllGroups(user){
    return localdb.selectAllGroups(user)
}

function deleteGroup(GID, user){
    return localdb.deleteGroup(user, GID)
}

function getInfoGroup(GID, user){
    return localdb.selectGroup(user, GID)
}

async function addGameToGroup(GID, newgame, user){
    let group = await localdb.selectGroup(user, GID)
    let game = group.games.find( ({ id }) => id == newgame)
    if(game == undefined) {
        game = await bga.getgame(newgame)
        group.games.push(game)
        return localdb.updateGroup(user, group)
    }
    return Promise.resolve(group)
}

async function removeGameFromGroup(GID, gameid, user){
    let group = await localdb.selectGroup(user, GID)
    group.games = group.games.filter(game => game.id != gameid)
    return localdb.updateGroup(user, group)
}

/**
 * @param {Express.Request} req 
 * @param {Express.Response} res 
 */
async function checkAuth(req, res, next){
    let AuthorizationBearer = req.get('Authorization')
    if (AuthorizationBearer == undefined && req.user != undefined){
        req.headers.authorization = 'Bearer ' + req.user.UUID
        AuthorizationBearer = req.get('Authorization')
    }
    let userID = await localdb.getUserID(req.params.username)
    if (AuthorizationBearer != undefined 
        && AuthorizationBearer.slice(7, this.length) == userID) {
        next()
    } else{
        res.status(403)//Forbidden -> need authorization
        res.send('You need to provide an header "Authorization" with the UUID')
    }
}

module.exports = {
    getUserInfo,
    getpopulargames,
    getgame,
    getgamename,
    getGameInfo,
    postuser,
    createGroup,
    editGroup,
    getAllGroups,
    deleteGroup,
    getInfoGroup,
    addGameToGroup,
    removeGameFromGroup,
    checkAuth,
    setGamesData,
}