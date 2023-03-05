(ns grmble.loglifter.frontend.main
  (:require
   [grmble.loglifter.frontend.model :as  model]
   [grmble.loglifter.frontend.event]
   [grmble.loglifter.frontend.sub]
   [grmble.loglifter.frontend.view.page :as page]
   [grmble.loglifter.frontend.util :refer [<sub >evt]]
   [day8.re-frame.http-fx]

   [kee-frame.core :as k]
   [kee-frame.error :as error]))

;; for DEBUG switch use predefined goog.DEBUG
;;
;; another compile time constant - base-path for router
(goog-define ^String BASE-PATH "")

;; println prints to the browser console
(enable-console-print!)

(def routes
  [["/" :home]
   ["/data" :data]
   ["/config" :config]
   ["/dev" :dev]])

(k/reg-controller :data
                  {:params (fn [match]
                             (when (= (get-in match [:data :name]) :data)
                               true))
                   :start  [:load-history]
                   :stop   [:dispose-history]})

(defn loader [body]
  (error/boundary
   (if (and true (<sub [:transient :initialized?]))
     body
     [page/loading-page])))


;; init! is called initially by shadlow-cljs (init-fn)
;; after-load! is called after every load
(defn ^:dev/after-load after-load! []
  (k/start! {;; renders into dom element #app - hard coded
             :root-component [loader [page/current-page]]
             :initial-db model/default-db
             :app-db-spec :grmble.loglifter.frontend.model/db-spec
             :routes routes
             :hash-routing? true
             :base-path BASE-PATH}))
(defn init! []
  (>evt [:load-config])
  (after-load!)
  (println "init! complete"))

