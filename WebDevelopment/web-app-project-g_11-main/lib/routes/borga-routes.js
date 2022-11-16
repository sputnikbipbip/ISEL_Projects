'use strict'

const express = require('express')
const passport = require('passport')
const router1 = require('./borga-web-api')
const router2 = require('./borga-web-api-auth')
const router3 = require('./borga-web-site')
const borgaServer = require('../services/borga-services').setGamesData
const swaggerUi = require('swagger-ui-express')
const YAML = require('yamljs')
const openapi = YAML.load('openapi.yaml')
const db = require('../data/borga-data-elastic')
//const db = require('../data/borga-data-mem')

/**
 * @param {Express} app 
 */
module.exports = function(app, isGameMock) {
    borgaServer(isGameMock)
    app.set('view engine', 'hbs')
    app.use(express.json()) // => Parses HTTP request body and populates req.body
    app.use(express.urlencoded({ extended: true })) // for parsing application/x-www-form-urlencoded
    app.use(express.static('public'))
    app.use(require('cookie-parser')())
    app.use(require('express-session')({ secret: 'keyboard cat', resave: true, saveUninitialized: true }))
    app.use(passport.initialize())
    app.use(passport.session())

    passport.serializeUser((user, done) => {
        done(null, user.name)
    })
    passport.deserializeUser((username, done) => {
        db
            .getUser(username)
            .then(user => done(null, user))
            .catch(err => done(err))
    })


    app.use('/api', router1)
    app.use('/api', router2)
    app.use('/', router3)
    app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(openapi))

    // eslint-disable-next-line no-unused-vars
    app.use((err, req, resp, _next) => {
        resp
            .status(err.status || 500)
            .json({
                message: err.message
            }) 
    })
}