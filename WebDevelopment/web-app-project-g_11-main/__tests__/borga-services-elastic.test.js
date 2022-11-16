'use strict'

const fetch = require('node-fetch')
const express = require('express')
const app = express()
require('../lib/routes/borga-routes')(app, true)
const services = require('../lib/services/borga-services')
const borga = require('../lib/data/borga-data-elastic')

function insertDummies() {
    const prms = [
        services.postuser('Zé Povino','zePovino@gmail.com')
        .then(user => {
            //save in array, for test purpose
            services.createGroup(
                'Quinta dos animais',
                'Os jogos relacionados com o livro de George Orwell',
                user.name
            )
        }),
        services.postuser('Maria Carcaça','mariaCarcaça@gmail.com')
        .then(user => {
            services.createGroup(
                'Quinta dos animais',
                'Jogos com animais',
                user.name
                )
        }),
        services.postuser('Mike Tyson','mikeTyson@gmail.com')
        .then(user => {
            return services.createGroup(
                'Box',
                'Jogos relacionados com MMA',
                user.name
                )
        })
        .then(group => {
            services.addGameToGroup(group.id, 'TAAifFP590', 'Mike Tyson')
        }),
        services.postuser('Miley Cirus','dragQueen@gmail.com')
        .then(user => {
            return services.createGroup(
                'Action games',
                'All wrecking ball games',
                user.name
                ) 
        })
        .then(group => {
            services.addGameToGroup(group.id, 'TAAifFP590', 'Miley Cirus')
        }),
        services.postuser('Britney Spears','queenBritney@gmail.com')
    ]
    return Promise.all(prms)
}

beforeAll(async () => { 
    /*
    First drop and recreate test Index
    */
    borga.setIndex('services-test')
    return fetch(borga.getUrl() , { method: 'delete'})
    .then(() => fetch(borga.getUrl(), { method: 'put' }))
    .then(data => insertDummies())
})

test('CREATE group', async () => {
    return services.createGroup(
        'Batalha Naval',
        'Jogos relacionados com batalha naval',
        'Zé Povino'
        )
        .then(group =>{
            expect(group.name).toBe('Batalha Naval')
            expect(group.description).toBe('Jogos relacionados com batalha naval')
            expect(group.games.length).toBe(0)
            expect(group.owner).toBe('Zé Povino')
        })
})

test('ADD game to group', async () => {
    const groups = await services.getAllGroups('Zé Povino')
    return services.addGameToGroup(
            groups[0].id,
            'mce5HZPnF5',
            'Zé Povino'
        )
        .then(
            group => {
            expect(group.games[0].name).toBe('Pandemic Legacy: Season 1')
            expect(group.games[0].id).toBe('mce5HZPnF5')
            expect(group.games[0].url).toBe('https://www.boardgameatlas.com/game/mce5HZPnF5/pandemic-legacy-season-1-red-edition')
        })
})

test('GET group from user', async () => {
    const groups = await services.getAllGroups('Mike Tyson')
    return services.getInfoGroup(
            groups[0].id,
            'Mike Tyson'
        )
        .then(group => {
            expect(group.name).toBe('Box')
            expect(group.id).toBe(0)
            expect(group.owner).toBe('Mike Tyson')
            expect(group.description).toBe('Jogos relacionados com MMA')
            expect(group.games.length).toBe(1)
        })
})

test('DELETE group from user', async () => {
    const group = await services.createGroup(
        'Dummy',
        'dummies',
        'Maria Carcaça'
        )
    const deleteGroup = await services.getInfoGroup(
        group.id,
        'Maria Carcaça'
    )
    return services.deleteGroup(
            deleteGroup.id,
            'Maria Carcaça'
        )
        .then(result => {
            expect(result).toMatchObject({"description": "dummies", "games": [], "id": 1, "name": "Dummy", "owner": 'Maria Carcaça'})
        })
})
