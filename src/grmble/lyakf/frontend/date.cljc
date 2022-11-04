(ns grmble.lyakf.frontend.date
  (:require
   [re-frame.core :as rf]
   [tick.core :as tick]))

(defn current-date [cofx]
  (assoc cofx :current-date (tick/today)))

(rf/reg-cofx :current-date
             current-date)
