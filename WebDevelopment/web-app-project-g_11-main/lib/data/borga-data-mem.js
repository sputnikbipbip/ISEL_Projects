'use strict'
const crypto = require('crypto')
/**
 * @typedef User
 * @type {Object}
 * @property {String} name Unique Name
 * @property {String} mail email
 * @property {String} password hashed password
 * @property {String} UUID unique id criado com crypto.randomUUID()
 * @property {Array<Group>} grupos objeto com grupos
 */
/**
 * @typedef Game
 * @type {Object}
 * @property {String} name Game name
 * @property {String} id Unique id
 * @property {Number} rating rating of the game
 * @property {String} url url
 */
/**
 * @typedef Group
 * @type {Object}
 * @property {Number} id UUID of group
 * @property {String} name Name
 * @property {String} owner username of group owner
 * @property {String} description description
 * @property {Array.<Game>} games array de games
 */

//grupos e coisas guardadas no filesystem 
let data = [
    {   
        name : 'Alice',
        mail : 'Alice@hotmail.com',
        UUID : '12b9a1d9-d6aa-4f15-a50d-3081f3917b88',
        grupos : [
            {
                id: 0,
                name: 'Jogos de encriptação',
                owner: 'Alice',
                description: 'Melhor jogos para jogar com o Bob',
                games: []
            }
        ]
    }
]//Array<User>

function insertUser(nome, email, password){
    let user = {name: nome, mail: email, password: password, UUID: crypto.randomUUID(), grupos: []}
    let exist = getuser(nome)
    if(exist == undefined) {
        data.push(user)
        return Promise.resolve({name: nome, UUID: user.UUID, password: password, mail: email})
    } else 
        return Promise.reject(erro('User already exists', 400))
} 

function getUser(username){
    let user = data.find(usera => usera.name==username)
    return Promise.resolve(user)
}

function getUserID(username){
    let UUID = getuser(username).UUID
    if (UUID == undefined) return Promise.reject(erro('User does not exist', 404))
    else return Promise.resolve(UUID)
}

function deleteUser(username){
    const index = data.findIndex((users) => users.name == username)
    if (index > -1) {
        data.splice(index, 1)
        return Promise.resolve()
    }
    throw borgaError(404, 'user does not exist')
}

function insertGroup(nome, descriptionn, username){
    let user = getuser(username)
    let grupo = {
        id: user.grupos.length,
        name: nome,
        owner: username,
        description: descriptionn,
        games: []
    }
    user.grupos.push(grupo)
    return Promise.resolve(grupo)
}

function selectGroup(username, GID){
    //console.log(JSON.stringify(data))
    let grupo = getgroup(username, GID)
    if(grupo == undefined) return Promise.reject(erro('Invalid user/group', 404))
    return Promise.resolve(grupo)
}

function deleteGroup(username, GID){
    let user = getuser(username)
    let group = user.grupos[GID]
    if (user.grupos[GID] == undefined)
        return Promise.reject(erro('The specified group is not in the server', 204))
    user.grupos[GID] = undefined
    return Promise.resolve(group)
}

function selectAllGroups(username){
    //const user = getuser(username)
    /*
    if (user.grupos.length == 0)  
        return Promise.reject(erro('User doesn\'t have groups', 404))*/
    return Promise.resolve(getuser(username).grupos)
}

function updateGroup(username, group){
    let user = getuser(username)
    user.grupos[group.id] = group
    return Promise.resolve(group)
}

function getuser(username){
    return data.filter((elem) => elem.name == username)[0]
}

function getgroup(username, GID){
    let user = getuser(username)
    if(user == undefined) return undefined
    return user.grupos[GID]
}

function erro(msg, code){
    return {message: msg, status: code}
}

/**
 * @param {Number} status 
 * @param {string} msg 
 * @returns {Error} err
 */
function borgaError(status, msg) {
    const err = new Error(msg)
    err.status = status
    return err
}

//Function used in testing to get all the data
function getdata(){
    return Promise.resolve(data)
}

function setdata(newdata){
    data = newdata
    return Promise.resolve(data)
}

function setIndex(){
    return
}

function getUrl(){
    return
}

module.exports = {
    getUser,
    deleteUser,
    getdata,
    setdata,
    insertUser,
    getUserID,
    insertGroup,
    selectGroup,
    selectAllGroups,
    deleteGroup,
    updateGroup,
    setIndex,
    getUrl
}
