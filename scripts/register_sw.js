const basePath = '{{ base-path }}'

const registerServiceWorker = async () => {
    if ('serviceWorker' in navigator) {
        try {
            const registration = await navigator.serviceWorker.register(basePath + '/sw.js', {})
            if (registration.installing) {
                console.log('Service worker installing')
            } else if (registration.waiting) {
                console.log('Service worker installed')
            } else if (registration.active) {
                console.log('Service worker active')
                // XXX button?
                // registration.update()
            }

        } catch (error) {
            console.error(`Registration failed with ${error}`)
        }
    }
}

registerServiceWorker()
