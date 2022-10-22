(ns grmble.lyakf.frontend.util
  (:require
   [re-frame.core]))

;; https://day8.github.io/re-frame/correcting-a-wrong/#lambdaisland-naming-lin
(def <sub (comp deref re-frame.core/subscribe))   ;; same as `listen` (above)
(def >evt re-frame.core/dispatch)
