window.onload = setup

function setup() {
    const inUsername = document.getElementById('inUsername')
    const inPassword = document.getElementById('inPassword')
    const inmail = document.getElementById('inmail')
    /**
     * Add listener to add user button
     */
    document
        .getElementById('btSignup')
        .addEventListener('click', () => handlerSignup(inUsername, inPassword, inmail))
}

async function handlerSignup(inUsername, inPassword, inmail) {
    try{
        if(inUsername.value == undefined || inUsername.value.length < 2){
            showAlert(`Bad parameters : ${inUsername.value}`,'Username is too short, must have more than 2 digits', kind = 'danger')
            return
        }
        if(inPassword.value == undefined || inPassword.value.length < 5){
            showAlert('Bad parameters : Password','Password is too short, must have more than 5 digits', kind = 'danger')
            return
        }

        if(!checkString(inUsername.value)) {
            showAlert(`Bad parameters : ${inUsername.value}`, `Name must be values between a-z, A-Z and 0-9`, kind = 'danger')
            return
        }
        const path = '/signup/' + inUsername.value
        const password = await digest(inPassword.value)
        const resp = await fetch(path, { 
            method: 'PUT',
            body: JSON.stringify({password: password, mail: inmail.value }),
            headers: { 'Content-Type': 'application/json' }
        })
        if(resp.status != 201) {
            const body = await resp.text()
            showAlert('ERROR', resp.statusText + '/n' + body)
            return
        }
        //if user is created successfuly, he is redirected to the user info page
        document.location.href = `/users/${inUsername.value}`
    } catch(err) {
        showAlert('ERROR', err)
    }
}

/**
 * @param {string} string 
 * @returns {Boolean} false if there are characters different from 0-9 | a-z | A-Z
 */
 function checkString(string) {
    let validChars = /[^a-zA-Z0-9]/
    for (let i = 0; i < string.length; i++) {
        if(validChars.test(string[i])) 
            return false
    }                   
    return true
}

async function digest(message) {
    const msgUint8 = new TextEncoder().encode(message)                           // encode as (utf-8) Uint8Array
    const hashBuffer = await crypto.subtle.digest('SHA-256', msgUint8)           // hash the message
    const hashArray = Array.from(new Uint8Array(hashBuffer))                     // convert buffer to byte array
    const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('') // convert bytes to hex string
    return hashHex
}

function showAlert(title, message, kind = 'danger') {
    const html = `<div class="alert alert-${kind} alert-dismissible fade show" role="alert">
                    <strong>${title}</strong>
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                  </div>`
    document
        .getElementById('alertPanel')
        .innerHTML = html
}