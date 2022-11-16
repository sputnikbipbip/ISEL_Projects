'use strict'

const request = require('supertest')
const express = require('express')
const bga = require('../lib/data/borga-data-elastic')
const jestOpenAPI = require('jest-openapi').default
const fetch = require('node-fetch')

// Load an OpenAPI file (YAML or JSON) into this plugin
jestOpenAPI(process.cwd() +  '/openapi.yaml')

/**
 * Setup express instance and routes
 */
const app = express()
require('../lib/routes/borga-routes')(app, true)
let authArray = []

function insertDummies() {
    const prms = [
        bga.insertUser('John', 'John@hotmail.com')
            .then(user => {bga.insertGroup("name1", "descrip 1", user.name),
                authArray.push({'bearer' : user.UUID, 'username' : user.name})}),
        bga.insertUser('Maria', 'Maria@hotmail.com')
            .then(user => {bga.insertGroup("name2", "descrip 2", user.name),
                authArray.push({'bearer' : user.UUID, 'username' : user.name})}),
        bga.insertUser('Gamer', 'Gamer@gmail.com')
            .then(user => {bga.insertGroup("name3", "descrip 3", user.name), 
                authArray.push({'bearer' : user.UUID, 'username' : user.name})}),
        bga.insertUser('Beatriz', 'Beatriz@hotmail.com')
            .then(user => {bga.insertGroup("name4", "descrip 4", user.name),
                authArray.push({'bearer' : user.UUID, 'username' : user.name})}),
        bga.insertUser('Naenia', 'Naenia@hotmail.com')
            .then(user => {bga.insertGroup("name5", "descrip 5", user.name), 
                authArray.push({'bearer' : user.UUID, 'username' : user.name})}),
        bga.insertUser('Gojko', 'Gojko@hotmail.com')
            .then(user => {bga.insertGroup("name6", "descrip 6", user.name), 
                authArray.push({'bearer' : user.UUID, 'username' : user.name})}),
        bga.insertUser('Flamur', 'Flamur@hotmail.com')
            .then(user => {bga.insertGroup("name7", "descrip 7", user.name), 
                authArray.push({'bearer' : user.UUID, 'username' : user.name})}),
    ]
    return Promise.all(prms)
}

beforeAll(async () => { 
    /*
    First drop and recreate test Index
    */
    bga.setIndex('api-test')
    return fetch(bga.getUrl(), { method: 'delete'})
    .then(() => fetch(bga.getUrl(), { method: 'put' }))
    .then(data => insertDummies())
})

test('GET most popular games', async () => {
    return request(app)
        .get('/api/populargames?limit=2')
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(resp => {
            expect(resp.body.length).toBe(2)
        })
})

test('CREATE group', async () => {
    return request(app)
        .post('/api/users/John/groups')
        .set('Accept', 'application/json')
        .set('Authorization', `bearer ${authArray.find(elem => elem.username == 'John').bearer}`)
        .send({
            "name": "name1",
            "description": "descrip 1"
        })
        .expect('Content-Type', /json/)
        .expect(201)
        .then(resp => {
            expect(resp.body.name).toBe("name1")
            expect(resp.body.description).toBe("descrip 1")
            expect(resp.body.owner).toBe("John")
            expect(resp.body.games.length).toBe(0)
        })
})

test('EDIT group', async () => {
    return request(app)
        .post('/api/users/Maria/groups/0')
        .set('Accept', 'application/json')
        .set('Authorization', `bearer ${authArray.find(elem => elem.username == 'Maria').bearer}`)
        .send({
            "name": "New name",
            "description": "New description"
        })
        .expect('Content-Type', /json/)
        .expect(201)
        .then(resp => {
            expect(resp.body.name).toBe("New name")
            expect(resp.body.description).toBe("New description")
            expect(resp.body.owner).toBe("Maria")
        })
})

test('GET list all groups of user', async () => {
    return request(app)
        .get('/api/users/Gamer/groups')
        .set('Accept', 'application/json')
        .set('Authorization', `bearer ${authArray.find(elem => elem.username == 'Gamer').bearer}`)
        .expect('Content-Type', /json/)
        .expect(200)
        .then(resp => {
            expect(resp.body.length).toBe(1)
            expect(resp.body[0].id).toBe(0)
            expect(resp.body[0].name).toBe('name3')
            expect(resp.body[0].owner).toBe('Gamer')
            expect(resp.body[0].description).toBe('descrip 3')
            expect(resp.body[0].games.length).toBe(0)
        })
})

test('DELETE a group from the user', async () => {
    return request(app)
        .delete('/api/users/Beatriz/groups/0')
        .set('Accept', 'application/json')
        .set('Authorization', `bearer ${authArray.find(elem => elem.username == 'Beatriz').bearer}`)
        .expect('Content-Type', /json/)
        .expect(200)
        .then(resp => {
            expect(resp.body).toMatchObject({"description": "descrip 4", "games": [], "id": 0, "name": "name4", "owner": "Beatriz"})
        })
})

test('GET group from user', async () => {
    return request(app)
        .get('/api/users/Naenia/groups/0')
        .set('Accept', 'application/json')
        .set('Authorization', `bearer ${authArray.find(elem => elem.username == 'Naenia').bearer}`)
        .expect('Content-Type', /json/)
        .expect(200)
        .then(resp => {
            expect(resp.body.id).toBe(0)
            expect(resp.body.name).toBe('name5')
            expect(resp.body.owner).toBe('Naenia')
            expect(resp.body.description).toBe('descrip 5')
            expect(resp.body.games.length).toBe(0)
        })
})

test('ADD game to group', async () => {
    return request(app)
        .post('/api/users/Gojko/groups/0/games')
        .set('Accept', 'application/json')
        .set('Authorization', `bearer ${authArray.find(elem => elem.username == 'Gojko').bearer}`)
        .send({
            id: 'yqR4PtpO8X'
        })
        .expect('Content-Type', /json/)
        .expect(201)
        .then(resp => {
            let game = resp.body.games[0]
            expect(game.id).toBe("yqR4PtpO8X")
            expect(game.name).toBe("Scythe")
            expect(game.url).toBe("https://www.boardgameatlas.com/game/yqR4PtpO8X/scythe")
        })
})

test('CREATE new user', async () => {
    return request(app)
        .post('/api/users')
        .set('Accept', 'application/json')
        .send({
            "name": "Professor Chibanga",
            "email": "chib@hotmail.com"
        })
        .expect('Content-Type', /json/)
        .expect(200)
        .then(resp => {
            expect(resp.body.name).toBe("Professor Chibanga")
            expect(resp.header.authorization).toBeDefined
        })
})

test('GET game with id', async () => {
    return request(app)
        .get('/api/games/5H5JS0KLzK')
        .set('Accept', 'application/json')
        .expect('Content-Type', /json/)
        .expect(200)
        .then(resp => {
            let game = resp.body
            expect(game.id).toBe("5H5JS0KLzK")
            expect(game.name).toBe("Wingspan")
            expect(game.url).toBe("https://www.boardgameatlas.com/game/5H5JS0KLzK/wingspan")
        })
})