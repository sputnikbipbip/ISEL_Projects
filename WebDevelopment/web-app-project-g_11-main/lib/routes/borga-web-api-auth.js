'use strict'

const router = require('express').Router()
const services = require('../services/borga-services')

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
//Manage favorite game groups
//Create group providing its name and description
router.post('/users/:username/groups', checkAuth, createUser)

//Edit group by changing its name and description 
router.post('/users/:username/groups/:GID', checkAuth, editGroup)

//List all groups do user
router.get('/users/:username/groups', checkAuth, getUserGroups)

//Delete a group
router.delete('/users/:username/groups/:GID', checkAuth, deleteUserGroup)

//Get the details of a group, with its name, description and names of the included games.
router.get('/users/:username/groups/:GID', checkAuth, getUserGroupInfo)

//Add a game to a group
router.post('/users/:username/groups/:GID/games', checkAuth, addGameToGroup)

//Remove a game from a group
router.delete('/users/:username/groups/:GID/games/:id', checkAuth, deleteGameFromGroup)

function createUser(req, res, next) {
    services
        .createGroup(req.body.name, req.body.description, req.params.username)
        .then((grupo) => {
            res.status(201)
            res.json(grupo)
        })
        .catch(next)
}

function deleteGameFromGroup(req, res, next) {
    services
        .removeGameFromGroup(req.params.GID, req.params.id, req.params.username)
        .then((resp) => res.json(resp))
        .catch(next)
}

function getUserGroupInfo(req, res, next) {
    services
        .getInfoGroup(req.params.GID, req.params.username)
        .then((group) => res.json(group))
        .catch(next)
}

function getUserGroups(req, res, next) {
    services
        .getAllGroups(req.params.username)
        .then((groups) => res.json(groups))
        .catch(next)
}

function deleteUserGroup(req, res, next) {
    services
        .deleteGroup(req.params.GID, req.params.username)
        .then((resp) => {
            res.json(resp)
        })
        .catch(next)
}

function editGroup(req, res, next) {
    services
        .editGroup(req.body.name, req.body.description, req.params.GID, req.params.username)
        .then((grupo) => {
            res.status(201)
            res.json(grupo)
        })
        .catch(next)
}

function addGameToGroup(req, res, next) {
    services
        .addGameToGroup(req.params.GID, req.body.id, req.params.username)
        .then((resp) => {res.status(201); res.json(resp)})
        .catch(next)
}

function checkAuth(req, res, next) {
    services.checkAuth(req, res, next)
        .catch(next)
}


module.exports = router
