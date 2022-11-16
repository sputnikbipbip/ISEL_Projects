'use strict'

const crypto = require('crypto')
const fetch = require('node-fetch')
let elasticUrl = 'http://localhost:9200/borga'

function getUrl() {
    return elasticUrl
}

function setIndex(index) {
    elasticUrl = 'http://localhost:9200/' + index
}

/**
 * @param {string} username 
 * @returns {Promise.<doc._id>}
 */
async function getDocumentID(username) {
    return fetch(getUrl() + '/_search?q=name:' + username)
        .then(resp => checkStatus(200, resp, 'No user with username ' + username))
        .then(resp => resp.json())
        .then(doc => doc.hits.hits[0]._id)
}

/**
 * @param {string} nome 
 * @param {string} email 
 * @returns {Promise.<user>}
 */
async function insertUser(nome, email, password) {
    let userInElastic = await fetch(getUrl() + '/_search?q=name:' + nome)
        .then(resp => resp.json())
        .then(doc => {
            if(doc.hits.hits.length == 0)
                return undefined
            return doc.hits.hits[0]._source
        })
    if (userInElastic != undefined)
        throw borgaError(409, 'There is already a User with your username: ' + nome)
    let user = {name: nome, mail: email, password: password, UUID: crypto.randomUUID(), grupos: []}
    return fetch(getUrl() + '/_doc?refresh=true', {
        method: 'post',
        body: JSON.stringify(user),
        headers: { 'Content-Type': 'application/json' }
    })
        .then(resp => checkStatus(201, resp))
        .then(resp => resp.json())
        .then(doc => {
            if (doc == undefined)
                throw borgaError(404, 'User does not exist')
            return user
        })
}

/**
 * 
 * @param {string} username 
 * @returns {Promise<user>} or {Promise.<undefined>} if username doesn't exist
 */
async function getUser(username){
    return fetch(getUrl() + '/_search?q=name:' + username)
        .then(resp => checkStatus(200, resp, 'No user with username ' + username))
        .then(resp => resp.json())
        .then(doc => {
            if(doc.hits.hits.length == 0)
                throw borgaError(404, 'User does not exist')
            return doc.hits.hits[0]._source
        })
}

/**
 * 
 * @param {string} username 
 * @returns {Promise.<UUID>}
 */
async function getUserID(username) {
    return fetch(getUrl() + '/_search?q=name:' + username)
        .then(resp => checkStatus(200, resp, 'No user with username ' + username))
        .then(resp => resp.json()) 
        .then(doc => {
            if(doc == undefined)
                throw borgaError(404, 'User does not exist')
            return doc.hits.hits[0]._source.UUID
        })
}

/**
 * @param {object} user 
 * @returns {Promise.<response>}
 */
async function deleteUser(user) {
    let docID = await getDocumentID(user.name)
    return fetch(getUrl() + '/_doc/' + docID + '?refresh=true', {
        method: 'delete'
    })
        .then(resp => {checkStatus(200, resp, 'No user with id ' + user.id)})
}

/**
 * @param {string} nome 
 * @param {string} descriptionn 
 * @param {string} username 
 * @returns {Promise.<group>}
 */
async function insertGroup(name, description, username) {
    let docID
    let userInElastic = await fetch(getUrl() + '/_search?q=name:' + username)
        .then(resp => resp.json())
        .then(doc => { 
            if(doc.hits.hits.length == 0)
                return undefined
            let user =  doc.hits.hits[0]._source
            docID = doc.hits.hits[0]._id
            return user
        })
    if (userInElastic == undefined)
        throw borgaError(404, 'There is no user with username : ' + username)
    let group = {
        id: userInElastic.grupos.length,
        name,
        owner: username,
        description,
        games: []
    }
    userInElastic.grupos.push(group)
    return fetch(getUrl() + '/_doc/' + docID + '?refresh=true', {
        method: 'put',
        body: JSON.stringify(userInElastic),
        headers: { 'Content-Type': 'application/json' }
    })
        .then(resp => checkStatus(200, resp, 'User not updated ' + userInElastic.name))
        .then(() => fetch(getUrl() + '/_doc/' + docID))
        .then(resp => resp.json())
        .then(doc => {
            return doc._source.grupos[doc._source.grupos.length - 1]
        })
}

/**
 * @param {string} username 
 * @param {Number} GID 
 * @returns {Promise.<group>} or {Promise.<undefined>} if GID doesn't exist
 */
async function getgroup(username, GID){
    let docID = await getDocumentID(username)
    return fetch(getUrl() + '/_doc/' + docID)
        .then(resp => checkStatus(200, resp, 'No user with username ' + username))
        .then(resp => resp.json())
        .then(doc => {
            return doc._source.grupos[GID]
        })
}

/**
 * @param {string} username 
 * @param {Number} GID 
 * @returns {Promise.<grupo>}
 */
function selectGroup(username, GID){
    return getgroup(username, GID)
}

/**
 * 
 * @param {string} username 
 * @param {Object} group
 * @returns {Promise.<grupo>}
 */
async function updateGroup(username, group){
    let user = await getUser(username)
    let docID = await getDocumentID(username)
    user.grupos[group.id] = group
    let result = await fetch(getUrl() + '/_doc/' + docID + '?refresh=true', {
        method: 'put',
        body: JSON.stringify(user),
        headers: { 'Content-Type': 'application/json' }
    })
        .then(resp => checkStatus(200, resp, 'No user with username ' + username))
        .then(resp => resp.json())
        .then(doc => doc.result)
    if (result != 'updated')
        throw borgaError(500, 'Error updating elasticSearch')
    return fetch(getUrl() + '/_doc/' + docID)
        .then(resp => checkStatus(200, resp, 'No user with username ' + username))
        .then(resp => resp.json())
        .then(doc => doc._source.grupos[group.id])
}

/**
 * @param {string} username 
 * @returns {Promise.<grupos>}
 */
async function selectAllGroups(username){
    let docID = await getDocumentID(username)
    return fetch(getUrl() + '/_doc/' + docID)
        .then(resp => resp.json())
        .then(doc => {
            return doc._source.grupos.filter(grupo => {
                if (grupo != undefined) return grupo
            })
        })
}

/**
 * 
 * @param {string} username 
 * @param {Number} GID 
 * @returns {Promise.<grupo>}
 */
async function deleteGroup(username, GID){
    let grupo = await selectGroup(username, GID)
    if (grupo == undefined)
        throw borgaError(204, 'The specified group is not in the server')

    let user = await getUser(username)
    let docID = await getDocumentID(username)
    user.grupos[GID] = undefined
    await fetch(getUrl() + '/_doc/' + docID + '?refresh=true', {
        method: 'put',
        body: JSON.stringify(user),
        headers: { 'Content-Type': 'application/json' }
    })
        .then(resp => checkStatus(200, resp, 'No user with username ' + username))
        .then(resp => resp.json())
        .then(doc =>  {
            if (doc.result != 'updated') throw borgaError(500, 'Error deleting group from elasticSearch')})
    return Promise.resolve(grupo)
}

/**
 * @param {Number} status 
 * @param {string} resp 
 * @param {string} msg 
 * @returns {string} resp
 */
function checkStatus(status, resp, msg) {
    if(resp.status === status) return resp
    const err = msg 
        ? Error(msg)
        : Error(resp.statusText)
    err.status = resp.status
    throw err
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

module.exports = {
    insertUser,
    deleteUser,
    setIndex,
    getUrl,
    getUser, 
    insertGroup,
    selectGroup,
    selectAllGroups,
    getUserID,
    updateGroup,
    deleteGroup
}