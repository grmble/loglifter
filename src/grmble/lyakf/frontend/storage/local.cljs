(ns grmble.lyakf.frontend.storage.local
  (:require
   [grmble.lyakf.frontend.model]
   [cljs.spec.alpha :as s]
   [re-frame.core :as rf]))

(def ^:private
  prefix "loglifter-v1-")

(defn store
  "Store the javascript value under the name (given in `:kvs`).
   
   If db is present, it will be validated using spec.alpha"
  ([{kvs :kvs db :db}]
   (if (or (not db)
           (s/valid? :grmble.lyakf.frontend.model/db-spec db))
     (doseq [[k v] kvs]
       (when (and k v)
         (js/window.localStorage.setItem (str prefix (name k)) (js/JSON.stringify v))))
     (js/console.error "db is not valid, not writing to local storage"))))

(defn load
  "Load a javascript obj from the name `key` and map it to `cofx`"
  [cofx ks]
  (reduce (fn [cofx k]
            (assoc cofx k (some->
                           (js/window.localStorage.getItem (str prefix (name k)))
                           (js/JSON.parse))))
          cofx ks))

(rf/reg-fx ::store store)
(rf/reg-cofx ::load load)



