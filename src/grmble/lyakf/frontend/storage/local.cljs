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

(def ^:private history-prefix (str prefix "history-"))

(defn- history-key [date]
  (str history-prefix date))

(defn- history-key? [k]
  (str/starts-with? k history-prefix))

(defn- storage-indices []
  (let [nr-entries js/window.localStorage.length]
    (->> nr-entries
         (range 0))))

(defn- index->storage-key [i]
  (js/window.localStorage.key i))

(defn- load-history-item
  ([k] (load-history-item k nil))
  ([k default]
   (or (some->
        (js/window.localStorage.getItem k)
        js/JSON.parse)
       default)))

(defn- store-history-item [k s]
  (js/window.localStorage.setItem k (js/JSON.stringify s)))

(defn- format-history-item [{:keys [current-date slug repsets]}]
  (str current-date " " (name slug) " " repsets "\n"))


(defn append-history
  [{:keys [current-date] :as item}]
  (let [k     (history-key current-date)
        line  (format-history-item item)
        lines (load-history-item k "")
        lines (str lines line)]
    (store-history-item k lines)))

(defn load-history
  [cofx]
  (->> (storage-indices)
       (into []
             (comp
              (map index->storage-key)
              (filter history-key?)
              (map load-history-item)))
       (sort)
       (str/join)
       (assoc cofx :load-history)))

(defn- remove-history
  "Remove all history items (before saving the new one)"
  []
  (doseq [k (sequence (comp
                       (map index->storage-key)
                       (filter history-key?))
                      (storage-indices))]
    (js/window.localStorage.removeItem k)))

(defn store-history
  "Store all the history items.
   
   The input should be the result of `parse-history` because
   the data tab should display an error anyway instead of saving."
  [{:keys [errs by-date]}]
  (if errs
    (js/console.error "NOT OVERWRITING HISTORY, THERE ARE ERRORS")
    (do
      (remove-history)
      (doseq [[date line-vec] by-date
              :let [k (history-key date)]]
        (store-history-item k (str/join "\n" line-vec))))))


(rf/reg-fx ::store store)
(rf/reg-fx ::append-history append-history)
(rf/reg-fx ::store-history store-history)
(rf/reg-cofx ::load load)
(rf/reg-cofx ::load-history load-history)
