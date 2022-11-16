'use strict'

const router = require('express').Router()
// eslint-disable-next-line no-unused-vars
const { getgame } = require('../data/borga-games-data')
const services = require('../services/borga-services')
const crypto = require('crypto')
//const servicesMock = require('../services/mock.js')

//Get the list of the most popular games
router.get('/populargames?:limit', getPopularGames)

//router.get("games/:game/details")
//Search games by id games/:id
router.get('/games/:id', getGameByID)

router.get('/games?:name', getGameByName)

//Get game information games/:id
router.get('/gameInfo/:id', getGameInfo)

//Create new user
//aqui tem de retornar o UUID no authorization header
router.post('/users', createUser)

function getPopularGames(req, res, next) {
    let number = req.query.limit
    if(isNaN(number) || number < 0 || number > 100) number = 50
    services
        .getpopulargames(number)
        .then((stf) => {res.status(200); res.json(stf)})
        .catch(next)
}

function getGameByID(req, res, next) {
    services
        .getgame(req.params.id)
        .then((stf) => res.json(stf))
        .catch(next)
}

function getGameByName(req, res, next) {
    services
        .getgamename(req.query.name)
        .then((stf) => res.json(stf))
        .catch(next)
}

function getGameInfo(req, res, next) {
    services
        .getGameInfo(req.params.id)
        .then((stf) => res.json(stf))
        .catch(next)
}

function createUser(req, res, next) {
    let password = req.body.password
    if (password == undefined) password = 'password'
    password = digest(password)
    services
        .postuser(req.body.name, req.body.email, password)
        .then((user) => {
            res.set('Authorization', user.UUID)
            res.json({name: user.name, mail: user.mail})
        })
        .catch(next)
}

function digest(message) {
    return crypto
        .createHash('sha256')
        .update(message)
        .digest('hex')
}

module.exports = router