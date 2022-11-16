'use strict'

const express = require('express')
const app = express()
require('./lib/routes/borga-routes')(app)
const PORT = 3000

let isGameMock
/**
 * node borga-server.js true -> run with games data mock
 */
if(process.argv.length > 2) {
    isGameMock = process.argv[2]
} else {
    isGameMock = false
}

app.listen(PORT, isGameMock, () => {
    console.log('Listening on port ' + PORT)
})
