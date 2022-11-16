'use strict'

const fetch = require('node-fetch')
const borga = require('./../lib/data/borga-data-elastic')

function insertDummies() {
    const prms = [
        borga.insertUser('Severina Virgílio', 'Severina@hotmail.com'),
        borga.insertUser('Mônica Adalberto', 'Mônica@hotmail.com')
            .then(user => borga.insertGroup('Best chess games', 'best chess games ever', user.name)),
        borga.insertUser('Adelaide Lorena', 'Adelaide@hotmail.com'),
        borga.insertUser('Efigênia Quintino', 'Efigênia@hotmail.com')
    ]
    return Promise.all(prms)
}

beforeAll(async () => { 
    borga.setIndex('borga-test')
    /*
    First drop and recreate test Index
    */
    return fetch(borga.getUrl(), { method: 'delete'})
    .then(() => fetch(borga.getUrl(), { method: 'put' }))
    .then(data => insertDummies())
})

test('Create and delete a user', async () => {
    const user = await borga.insertUser('Danny Mcskill', 'mcskill@hotmail.com')
    expect(user.name).toBe('Danny Mcskill')
    expect(user.mail).toBe('mcskill@hotmail.com')
    await borga.deleteUser(user)
})

test('Get user', async () => {
    const user = await borga.getUser('Severina Virgílio')
    expect(user.mail).toBe('Severina@hotmail.com')
    expect(user.name).toBe('Severina Virgílio')
})

test('Insert group', async () => {
    const grupo = await borga.insertGroup('Best naval battles', 'best naval games ever', 'Severina Virgílio')
    expect(grupo.name).toBe('Best naval battles')
    expect(grupo.description).toBe('best naval games ever')
    expect(grupo.games.length).toBe(0)
    expect(grupo.owner).toBe('Severina Virgílio')
})

test('Get group', async () => {
    const grupo = await borga.selectGroup('Mônica Adalberto', 0)
    expect(grupo.name).toBe('Best chess games')
    expect(grupo.description).toBe('best chess games ever')
    expect(grupo.games.length).toBe(0)
    expect(grupo.owner).toBe('Mônica Adalberto')
})


test('Update group', async () => {
    let group = {
        id : 0,
        name : "name7",
        owner : "Mônica Adalberto",
        description : "descrip 7",
        games : []
    }    
    let updatedGroup = await borga.updateGroup('Mônica Adalberto', group)
    expect(updatedGroup.name).toBe("name7")
    expect(updatedGroup.owner).toBe('Mônica Adalberto')
    expect(updatedGroup.description).toBe("descrip 7")
    expect(updatedGroup.games.length).toBe(0)
})

test('Delete group', async () => {
    let deletedGroup = await borga.deleteGroup('Mônica Adalberto', 0)
    expect(deletedGroup.name).toBe("name7")
    expect(deletedGroup.owner).toBe('Mônica Adalberto')
    expect(deletedGroup.description).toBe("descrip 7")
    expect(deletedGroup.games.length).toBe(0)
})
