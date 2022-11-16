'use strict'

const mem = require('../lib/data/borga-data-mem')
const crypto = require('crypto')
const async = require('hbs/lib/async')
const exp = require('constants')

/**
 * @typedef User
 * @type {Object}
 * @property {String} name Unique Name
 * @property {String} mail email
 * @property {String} UUID unique id criado com crypto.randomUUID()
 * @property {Array<Group>} grupos objeto com grupos
 */
/**
 * @typedef Group
 * @type {Object}
 * @property {Number} id UUID of group
 * @property {String} name Name
 * @property {String} owner UUID of group owner
 * @property {String} description description
 * @property {Array.<Game>} games array de games
 */

let data = []

async function insertDummies(){
    data = await mem.getdata()
    data.push({
        name: 'Manuel',
        mail: 'manuelmail@gmail.com',
        UUID: crypto.randomUUID(),
        grupos: [
            {
                id: 0,
                name: 'grupoA',
                owner: 'Manuel',
                description: 'Este é o grupo A',
                games: []
            },
            {
                id: 1,
                name: 'grupoB',
                owner: 'Manuel',
                description: 'Este é o grupo B',
                games: []
            },
            {
                id: 2,
                name: 'grupoD',
                owner: 'Manuel',
                description: 'Este é o grupo D',
                games: []
            },
            {
                id: 3,
                name: 'grupoC',
                owner: 'Manuel',
                description: 'Este é o grupo C',
                games: []
            }
        ]
    })
    data.push({
        name: 'Daniel',
        mail: 'danielmail@gmail.com',
        UUID: crypto.randomUUID(),
        grupos: []
    })
    data.push({
        name: 'deletemepls',
        mail: "tobedeleted@bye.com",
        UUID: crypto.randomUUID(),
        grupos: []
    })
    return mem.setdata(data)
}

beforeAll(() => { 
    return insertDummies()
})

test('Select user UUID', async () => {
    let user = data.filter((elem) => elem.name == 'Manuel')[0]
    return mem.getUserID('Manuel')
        .then(UUID => expect(UUID).toBe(user.UUID))
})

test('Insert and delete user', async () => {
    let user = {nome: 'novo', mail: 'mai@gmail.com'}
    return mem.insertUser(user.nome, user.mail)
        .then(observed => {
            expect(observed).toBeDefined()
            expect(observed.name).toBe(user.nome)
            expect(observed.mail).toBe(user.mail)
        })
    let del = await mem.deleteUser(user.nome)
    return mem.getUser(user.nome).then(userdel => expect(userdel).toBe(undefined))
})

test('Select group', async () => {
    let grupo = data.filter((elem) => elem.name == 'Manuel')[0].grupos[0]
    return mem.selectGroup('Manuel', 0)
        .then(group => {
            expect(group).toBeDefined()
            expect(group.name).toBe(grupo.name)
            expect(group.owner).toBe(grupo.owner)
            expect(group.description).toBe(grupo.description)
            expect(group.games.length).toBe(grupo.games.length)
        })
})

test('Insert group', async () => {
    let expected = {
        id: 0,
        name: 'grupoA',
        owner: 'Daniel',
        description: 'Este é o grupo A',
        games: []
    }
    return mem.insertGroup(expected.name, expected.description, 'Daniel')
        .then(group => {
            expect(group).toBeDefined()
            expect(group.name).toBe(expected.name)
            expect(group.owner).toBe(expected.owner)
            expect(group.description).toBe(expected.description)
            expect(group.games.length).toBe(expected.games.length)
        })
})

test('Delete group', async () => {
    const a = await mem.deleteGroup('Manuel', 2)
    data = await mem.getdata()
    let user = data.filter((elem) => elem.name == 'Manuel')[0]
    expect(user.grupos[2]).toBe(undefined)
})

test('Select all groups', async () => {
    let grupos = data.filter((elem) => elem.name == 'Manuel')[0].grupos
    return mem.selectAllGroups('Manuel')
        .then(groups => {
            expect(groups.length).toBe(grupos.length)
        })
})

test('Update group', async () => {
    let group = data.filter((elem) => elem.name == 'Manuel')[0].grupos[1]
    group.description='updated description'
    group.name='update'
    let updated = await mem.updateGroup('Manuel', group)
    data = await mem.getdata()
    let grupo = data.filter((elem) => elem.name == 'Manuel')[0].grupos[1]
    expect(group.name).toBe(grupo.name)
    expect(group.owner).toBe(grupo.owner)
    expect(group.description).toBe(grupo.description)
    expect(group.games.length).toBe(grupo.games.length)
})

test('Find user', async() => {
    let username = 'Daniel'
    let user = await mem.getUser(username)
    expect(user.name).toBe(username)
})
