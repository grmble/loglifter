(ns grmble.lyakf.frontend.date
  (:require
   [re-frame.core :as rf]))

(defn current-date [cofx]
  (let [now (.toISOString (js/Date.))]
    ;; (tick/today) pulled in almost 200kb ...
    (assoc cofx :current-date (subs now 0 10))))

(rf/reg-cofx :current-date
             current-date)
