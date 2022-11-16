'use strict'

const router = require('express').Router()
const services = require('../services/borga-services')

router.use(checkLocals)
router.get('/', getHome)
router.get('/about', getAbout)
router.get('/signup', getSignup)
router.put('/signup/:username', putSignup)
router.get('/login', getLogin)
router.post('/login', postLogin)
router.post('/logout', postLogout)

//Get the list of the most popular games
router.get('/populargames', getMostPopularGames)

//router.get("games/:game/details")
//Search games by id games/:id
router.get('/games/:id', getGameDetails)

router.get('/games?:name', getGamesByName)

//Get game information games/:id
router.get('/gameInfo/:game', getGameInfo)

router.use('/users/:username', checkCredentials)
//Show user info, has link to username/groups
router.get('/users/:username', getUserInfo)

//List all groups do user
//each group links to /users/:username/groups/:GID
router.get('/users/:username/groups', getUserGroups)

router.post('/users/:username/groups', insertGroup)

//Get the details of a group, with its name, description and names of the included games.
//each game links to game/:id
router.get('/users/:username/groups/:GID', getGroupDetails) 

router.get('/users/:username/groups/:GID/addgame', getGameFromGroup)

router.post('/users/:username/groups/:GID/addgame', insertGameInGroup)

//each game links to game/:id
router.get('/users/:username/groups/:GID/edit', getEditGroupPage)

router.post('/users/:username/groups/:GID/edit', editGroup)

function sessionAlert(session, kind, title, message) {
    session.alert = {
        title,
        message,
        kind
    }
}

function checkCredentials(req, res, next){
    if(!req.user) {
        sessionAlert(req.session, 'danger', 'Access Forbidden!', 'You should login/signup first to access user & groups features.')
        return res.redirect('/signup')
    }
    if(req.user.name != req.params.username) {
        sessionAlert(req.session, 'warning', 'Access Forbidden!', 'You can only access your user info. You cannot access other users info!')
        return res.redirect(req.headers.referer)
    }
    next()
}

function getSignup(req, res){
    res.render('signup')
}

function putSignup(req, res, next){
    services
        .postuser(req.params.username, req.body.mail, req.body.password)
        .then((user) => req.logIn(user, err => {
            if(err) return next(err)
            sessionAlert(req.session, 'success', 'Signup Successfully!', req.params.username + ' has signned up with success!')
            res.status(201).end()
        }))
        .catch(next)
}

function getLogin(req, res){
    if(req.user != undefined) 
        res.redirect('/')
    else
        res.render('login')
}

/**
 * @param {Express.Request} req 
 * @param {Express.Response} res 
 */
function postLogin(req, res, next) {
    services
        .getUserInfo(req.body.username)
        .then(user => {
            if(user.password != req.body.password) {
                sessionAlert(req.session, 'danger', 'Bad Credentials!', 'Username or password incorrect!')
                res.status(409)
                res.statusMessage = 'Bad Credentials'
                res.end()
            } else {
                req.logIn(user, err => {
                    if(err) return next(err)
                    sessionAlert(req.session, 'success', 'Logged in!', req.body.username + ' has logged in with success!')
                    res.status(201).end()
                })
            }
        })
        .catch(next)
}

/**
 * @param {Express.Request} req 
 * @param {Express.Response} res 
 */
function postLogout(req, res){
    req.logOut()
    sessionAlert(req.session, 'success', 'Logged out', 'User logged out with success!')
    res.redirect(req.headers.referer)
}

function getMostPopularGames(req, res, next) { 
    let number = req.query.limit
    if(isNaN(number) || number < 0 || number > 100) number = 50
    services
        .getpopulargames(number)
        .then(populargames => {
            populargames = populargames.map(game => { return {
                'name': game.name,
                'id': game.id,
                'url': game.url
            }}) 
            return res.render('populargames', {'populargames' : populargames})
        })
        .catch(next)
}

function getGameDetails(req, res) {
    res.redirect('/gameinfo/'+req.params.id)
}

function getGamesByName(req, res, next) {
    let game = req.query.name
    if (game == undefined){
        game = ''
    }
    services
        .getgamename(game)
        .then(games => {
            const model = {
                'search': game,
                'games' : games
            }
            return res.render('gamelist', model)
        })
        .catch(next)
}

function getGameInfo(req, res, next) {
    services
        .getGameInfo(req.params.game)
        .then(gameinfo => res.render('game', gameinfo))
        .catch(next)
}

function getUserInfo(req, res, next) {
    services
        .getUserInfo(req.params.username)
        .then(user => res.render('user', user))
        .catch(next)
}

function getUserGroups(req, res, next) {
    services
        .getAllGroups(req.params.username)
        .then(groups => res.render('userallgroups', {'user' : req.params.username, 'groups':groups}))
        .catch(next)
}

function getGroupDetails(req, res, next) {
    services
        .getInfoGroup(req.params.GID, req.params.username)
        .then(group => res.render('usergroup', group))
        .catch(next)
}

function getGameFromGroup(req, res, next) {
    res.render('addgame')
}

function insertGameInGroup(req, res, next) {
    const username = req.params.username
    const gid = req.params.GID
    services.getgamename(req.body.Title)
        .then(game => services.addGameToGroup(gid, game[0].id, username))
        .then(group => res.redirect(`/users/${username}/groups/${group.id}`))
        .catch(next)
}

function getHome(req, res, next) {
    res.render('home')
}

function getAbout(req, res, next) {
    res.render('about')
}

function insertGroup(req, res, next) {
    const user = req.params.username
    services
        .createGroup(req.body.name, req.body.description, user)
        .then(group => res.redirect(`/users/${user}/groups/${group.id}`))
        .catch(next)
}

function getEditGroupPage(req, res, next) {
    services
    .getInfoGroup(req.params.GID, req.params.username)
    .then(group => res.render('usergroupedit', group))
    .catch(next)
}

function editGroup(req, res, next) {
    const username = req.params.username
    services.editGroup(req.body.name, req.body.description, req.params.GID, username)
            .then(group => res.redirect(`/users/${username}/groups/${group.id}`))
            .catch(next)
}

function checkLocals(req, res, next) {
    if(req.session.alert) {
        const alert = req.session.alert
        delete req.session.alert
        res.locals.alert = alert    
    }
    if(req.user) {
        res.locals.username = req.user.name
    }
    next()
}

module.exports = router