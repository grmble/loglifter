const basePath = '{{ base-path }}'
const cacheName = '{{ base-path }}' // multiple sws from same origin!!! 
const appShellFiles = [
    '/', // we never load /index.html - always /
    '/css/bulma.min.css',
    '/js/main.js',
    '/js/register_sw.js',
    '/config.json',
    '/manifest.json',
    // no favicons!
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

addEventListener('activate', e => {
    self.clients.claim()
})

// stale while revalidate from https://web.dev/learn/pwa/serving/
// for ease of update
self.addEventListener("fetch", event => {
    event.respondWith(
        caches.match(event.request).then(cachedResponse => {
            const networkFetch = fetch(event.request).then(response => {
                // update the cache with a clone of the network response
                caches.open(cacheName).then(cache => {
                    cache.put(event.request, response.clone());
                });
            });
            // prioritize cached response over network
            return cachedResponse || networkFetch;
        }
        )
    )
});
