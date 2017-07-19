(ns react-game-performance-prototype.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

(defn wrap-middleware [handler]
  (wrap-defaults handler site-defaults))
