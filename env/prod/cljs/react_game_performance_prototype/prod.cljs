(ns react-game-performance-prototype.prod
  (:require [react-game-performance-prototype.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
