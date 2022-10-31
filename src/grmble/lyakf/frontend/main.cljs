(ns grmble.lyakf.frontend.main
  (:require
   [grmble.lyakf.frontend.model :as  model]
   [grmble.lyakf.frontend.event]
   [grmble.lyakf.frontend.sub]
   [grmble.lyakf.frontend.view.page :as page]
   [grmble.lyakf.frontend.util :refer [<sub >evt]]
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]
   [re-frame.core :as rf]
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


;;
;; https://day8.github.io/re-frame/Loading-Initial-Data/
;;
(rf/reg-event-fx :load-config
                 (fn [_ _]
                   {:http-xhrio {:uri             "config.json"
                                 :method          :get
                                 :response-format (ajax/json-response-format {:keywords? true})
                                 :on-success [:config-loaded]
                                 :on-error [:config-not-found]}}))
(rf/reg-event-db :config-loaded
                 (fn [db [_ config]]
                   (-> db
                       (assoc :config (merge (:config db) config))
                       (assoc-in [:ui :initialized?] true))))
(rf/reg-event-db :config-not-found
                 (fn [db _]
                   (assoc-in db [:ui :initialized?] true)))

(defn loader [body]
  (error/boundary
   (if (and true (<sub [:ui :initialized?]))
     body
     [page/loading-page])))


;; init! is called initially by shadlow-cljs (init-fn)
;; after-load! is called after every load
(defn ^:dev/after-load after-load! []
  (k/start! {;; renders into dom element #app - hard coded
             :root-component [loader [page/current-page]]
             :initial-db model/default-db
             :app-db-spec :grmble.lyakf.frontend.model/db-spec
             :routes routes
             :hash-routing? true
             :base-path BASE-PATH}))
(defn init! []
  (>evt [:load-config])
  (after-load!)
  (println "init! complete"))

