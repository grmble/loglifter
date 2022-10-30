const basePath = '{{ base-path }}'
const cacheName = 'lyakf-v1'
const appShellFiles = [
    '/index.html',
    '/css/bulma.min.css',
    '/js/main.js',
    '/js/register_sw.js',
    '/config.json',
    '/icons/favicon-16x16.png',
    '/icons/favicon.ico',
    '/icons/android-chrome-192x192.png',
    '/icons/apple-touch-icon.png',
    '/icons/about.txt',
    '/icons/android-chrome-512x512.png',
    '/icons/favicon-32x32.png',
    '/lyakf.webmanifest',
]
const cacheContent = appShellFiles.map(v => basePath + v)

self.addEventListener('install', (e) => {
    console.log('[Service Worker] Install')
    e.waitUntil((async () => {
        const cache = await caches.open(cacheName)
        console.log('[Service Worker] Caching all: app shell and content')
        await cache.addAll(cacheContent)
    })())
})

self.addEventListener('fetch', (e) => {
    e.respondWith((async () => {
        const r = await caches.match(e.request)
        if (r) {
            console.log(`[Service Worker] Cached resource: ${e.request.url}`)
            return r
        }
        // const response = await fetch(e.request)
        // const cache = await caches.open(cacheName)
        // console.log(`[Service Worker] Caching new resource: ${e.request.url}`)
        // cache.put(e.request, response.clone())
        // return response
        console.log(`[Service Worker] Fetching resource: ${e.request.url}`)
        return fetch(e.request)
    })());
})
