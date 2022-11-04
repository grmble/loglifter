(ns grmble.lyakf.frontend.storage.local
  (:require
   [grmble.lyakf.frontend.model]
   [cljs.spec.alpha :as s]
   [clojure.string :as str]
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

(defn append-history
  [{:keys [current-date slug repsets]}]
  (let [k     (str prefix "history-" current-date)
        line  (str current-date " " slug " " repsets "\n")
        lines (or (js/JSON.parse (js/window.localStorage.getItem k))
                  "")
        lines (str lines line)]
    (js/window.localStorage.setItem k (js/JSON.stringify lines))))

(defn load-history
  [cofx]
  (let [history-prefix (str prefix "history-")
        nr-entries     js/window.localStorage.length]
    (->> (range 0 nr-entries)
         (into []
               (comp
                (map (fn [i] (js/window.localStorage.key i)))
                (filter #(str/starts-with? % history-prefix))
                (map (fn [k]
                       (some->
                        (js/window.localStorage.getItem k)
                        js/JSON.parse)))))
         (str/join)
         (assoc cofx :load-history))))


(rf/reg-fx ::store store)
(rf/reg-fx ::append-history append-history)
(rf/reg-cofx ::load load)
(rf/reg-cofx ::load-history load-history)


(comment
  (or nil #js []))
