const basePath = '{{ base-path }}'

const registerServiceWorker = async () => {
    if ('serviceWorker' in navigator) {
        try {
            const registration = await navigator.serviceWorker.register(basePath + '/sw.js', {})
            registration.addEventListener('updatefound', event => {
                const newSW = registration.installing;
                newSW.addEventListener('statechange', event => {
                    if (newSW.state == 'installed') {
                        console.log('new service worker installed')
                    }
                })
            })
        } catch (error) {
            console.error(`Registration failed with ${error}`)
        }
    }
}

registerServiceWorker()
