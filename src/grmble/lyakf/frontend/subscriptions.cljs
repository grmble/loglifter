(ns grmble.lyakf.frontend.subscriptions
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub :initialized?
            (fn [db] (-> db :ui :initialized?)))

(rf/reg-sub :show-dev-tab?
            (fn [db] (-> db :config :show-dev-tab?)))
