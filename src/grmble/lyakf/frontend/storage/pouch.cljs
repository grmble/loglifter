(ns grmble.lyakf.frontend.storage.pouch
  (:require
   ["pouchdb" :as PouchDB]
   ["pouchdb-upsert" :as upsertPlugin]))

(PouchDB/plugin upsertPlugin)

(def db (PouchDB. "lyakf-v1"))

(defn db-upsert [id obj]
  (db.upsert id (fn [db-obj]
                  (js/Object.assign db-obj obj))))

(defn db-get [id]
  (db.get id))

(defn db-remove [id]
  (db.remove id))

