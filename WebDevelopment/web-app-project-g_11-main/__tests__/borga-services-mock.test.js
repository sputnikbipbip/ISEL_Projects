'use strict'
const express = require('express')
const fs = require('fs/promises')
const services = require('../lib/services/borga-services')

/**
 * Setup express instance and routes
 */

const app = express()
require('../lib/routes/borga-routes')(app, true)

test('Get game by id', async () => {
    const game = await services.getgame('BBg2uXXdB8')
    return services
        .getgame('BBg2uXXdB8')
        .then(resp => {
            expect(resp).toMatchObject(game)
        }) 
})

test('Get game by name', async () => {
  const game = await services.getgame('BBg2uXXdB8')
  return services
  .getgamename('Kingdomino')
      .then(resp => {
        expect(resp[0].name).toBe(game.name)
        expect(resp[0].url).toBe(game.url)
        expect(resp[0].id).toBe(game.id)
      }) 
})

test('Get 3 most popular games', async () => {
    const mostPopular = [
            {
              id: '3IPVIROfvl',
              name: 'Brass: Birmingham',
              url: 'https://www.boardgameatlas.com/game/3IPVIROfvl/brass-birmingham',
              rating: 4.334490740740742
            },
            {
              id: 'mce5HZPnF5',
              name: 'Pandemic Legacy: Season 1',
              url: 'https://www.boardgameatlas.com/game/mce5HZPnF5/pandemic-legacy-season-1-red-edition',
              rating: 4.240980414428841
            },
            {
              id: 'RLlDWHh7hR',
              name: 'Gloomhaven',
              url: 'https://www.boardgameatlas.com/game/RLlDWHh7hR/gloomhaven',
              rating: 4.209888615522257
            }]
    return services
        .getpopulargames(3)
        .then(resp => {
            expect(resp).toMatchObject(mostPopular)
        }) 
})