(ns react-game-performance-prototype.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [react-game-performance-prototype.boot :as boot]
              [react-game-performance-prototype.preload :as preload]
              [react-game-performance-prototype.home :as home]
              [react-game-performance-prototype.game :as game]))

(defn create-game []
  (let [game (Phaser.Game. 360 640 Phaser.AUTO "game-div")]
    (.add game.state "BootState" boot/state)
    (.add game.state "PreloadState" preload/state)
    (.add game.state "GameState" game/state)
    (.add game.state "HomeState" home/state)
    (.start game.state "BootState")
    game))

(defn render-game-component []
  [:div {:id "game-div"}])

(defn build-game-component []
  (let [game (atom nil)]
    (reagent/create-class
     {:component-did-mount
      (fn []
        (reset! game (create-game)))
      :display-name "Whichone game component"
      :component-will-unmount #(.destroy @game)
      :reagent-render #(render-game-component)})))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [build-game-component] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
