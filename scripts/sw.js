const basePath = '{{ base-path }}'
const cacheName = '{{ base-path }}' // multiple sws from same origin!!! 
const appShellFiles = [
    '/', // we never load /index.html - always /
    '/css/bulma.min.css',
    '/js/main.js',
    '/js/register_sw.js',
    '/config.json',
    '/manifest.json',
    // app should work without these
    /*
    '/icons/favicon-16x16.png',
    '/icons/favicon.ico',
    '/icons/android-chrome-192x192.png',
    '/icons/apple-touch-icon.png',
    '/icons/about.txt',
    '/icons/android-chrome-512x512.png',
    '/icons/favicon-32x32.png',
    */
]
const cacheContent = appShellFiles.map(v => basePath + v)

self.addEventListener('install', (e) => {
    console.log('[Service Worker] Install')
    e.waitUntil((async () => {
        const cache = await caches.open(cacheName)
        console.log('[Service Worker] Caching critical resources')
        await cache.addAll(cacheContent)
    })())
})

addEventListener('activate', event => {
    event.waitUntil(async function () {
        if (self.registration.navigationPreload) {
            await self.registration.navigationPreload.enable()
        }
    }())
    self.clients.claim();
})

self.addEventListener('fetch', (e) => {
    e.respondWith((async () => {
        let r = await caches.match(e.request)
        if (r) {
            // console.log(`[Service Worker] Cached resource: ${e.request.url}`)
            return r
        }

        const cache = await caches.open(cacheName)

        r = await e.preloadResponse
        if (r) {
            // this is to prio browser navigation while a service
            // worker is busy caching all the things
            // will not happen for lyakf anyway - there is only 1 page
            return r
        }

        const response = await fetch(e.request)
        // cache.put(e.request, response.clone())
        return response
    })());
})
