(ns grmble.lyakf.frontend.main
  (:require
   [grmble.lyakf.frontend.spec]
   [grmble.lyakf.frontend.subscriptions]
   [grmble.lyakf.frontend.view.page :as page]
   [grmble.lyakf.frontend.util :refer [<sub >evt]]
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]
   [re-frame.core :as rf]
   [kee-frame.core :as k]
   [kee-frame.error :as error]))

;; compile time constant - (when DEBUG ...) will be optimized away
(goog-define ^boolean DEBUG true)

;; another compile time constant - base-path for router
(goog-define ^String BASE-PATH "")

;; println prints to the browser console
(enable-console-print!)

(def routes
  [BASE-PATH
   ["/" :home]
   ["/data" :data]
   ["/config" :config]
   ["/dev" :dev]])

(def initial-db
  {:ui {:initialized? false
        :current-tab :home}
   :config {:show-dev-tab? false}
   :training-programs {}})

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
   (if (and true (<sub [:initialized?]))
     body
     [page/loading-page])))


;; init! is called initially by shadlow-cljs (init-fn)
;; after-load! is called after every load
(defn ^:dev/after-load after-load! []
  (k/start! {;; renders into dom element #app - hard coded
             :root-component [loader [page/current-page]]
             :initial-db initial-db
             :app-db-spec ::grmble.lyakf.frontend.spec/db-spec
             :routes routes
             ;; route-hashing does not work with gh pages deployment
             ;; via compile time BASE-PATH
             :hash-routing? false}))
(defn init! []
  (>evt [:load-config])
  (after-load!)
  (println "init! complete"))

