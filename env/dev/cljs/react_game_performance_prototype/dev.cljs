(ns ^:figwheel-no-load react-game-performance-prototype.dev
  (:require
    [react-game-performance-prototype.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
