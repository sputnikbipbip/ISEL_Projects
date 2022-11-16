window.onload = setup

function setup() {
    /**
     * Add listener to userallgroups Delete
     */
    document
        .querySelectorAll('.btn-danger')
        .forEach(btn => btn.addEventListener('click', () => handlerDeleteGroup(btn)))
}
/**
 * @param {Element} btn 
 */
async function handlerDeleteGroup(btn) {
    /**
   * e.g. DELETE http://localhost:3000/users/:username/groups/:GID
   */
    try {
        const path = document.location.href.replace('/users', '/api/users') + '/' + btn.dataset.groupId
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

/**
 * 
 * @param {string} title 
 * @param {string} message 
 * @param {string} kind 
 */
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