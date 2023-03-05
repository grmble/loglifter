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
* Start the local dev environment: `bb watch`.

Alternatively, you may want to `bb start` once, then
run multiple commands like `bb watch`, `bb release`

