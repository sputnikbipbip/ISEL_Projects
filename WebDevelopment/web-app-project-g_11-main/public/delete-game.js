
window.onload = setup

function setup() {
    /**
     * Add listener to getUserGroupInfo Delete
     */
    document
        .querySelectorAll('.btn-danger')
        .forEach(btn => btn.addEventListener('click', () => handlerDeleteGame(btn)))
}


 async function handlerDeleteGame(btn) {
    /**
     * GET '/users/:username/groups/:GID'                  current page
     * DELETE '/users/:username/groups/:GID/games/:id'     deletion page
     */
    try {
        const path = document.location.href.replace('/users', '/api/users') + '/games/' + btn.dataset.gameId
        const resp = await fetch(path, { method: 'DELETE'})
        if(resp.status != 200) {
            const body = await resp.text()
            showAlert('ERROR ' + resp.statusText + ' ' + resp.status, body)
            return
        }
        btn
            .parentElement // <p></p>
            .parentElement // <li></li>
            .remove()
    } catch(err) {
        showAlert('ERROR', err)
    }
}