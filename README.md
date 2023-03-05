# Loglifter - The Lifter's Log

A logging application for weight training.

Remembers your weights and knows about training programs,
just go to the gym and do what the phone is telling you to.

Progressive web app - use in the browser or install
it to your phone (will work without internet access!)

https://grmble.github.io/loglifter/


This started as an example project for teaching myself kee-frame,
here are the blog posts:

https://grmble.github.io/tags-output/lyakf/


## Local development

You need to have babashka installed: https://github.com/babashka/babashka#installation

Get the javascript dependencies: `yarn` (or `npm install`)

* Overview of tasks: `bb tasks`
* Clean the local dev environment: `bb clean`
* Start the local dev environment: `bb watch`

Alternatively, you may want to `bb start` once, then
jack in from Calva using the `shadowcljs` option.

The "re-com" warnings are coming from `re-frisk`, ignore for now.


## Releasing

* `bb release` to build a release version for local testing (no path prefix)
* `bb release partX` to release a version for the learn-you-a-keeframe blog
* `bb release loglifter` to release a version for the loglifter ghpages

Then upload to the gh pages branch (`git switch --orphan gh-pages` is your friend)


