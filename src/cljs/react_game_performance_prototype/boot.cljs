(ns react-game-performance-prototype.boot)

(defn state []
  (reify
    Object

    (init [game]
      (aset game.scale "scaleMode" Phaser.ScaleManager.SHOW_ALL)
      (aset game.scale "pageAlignHorizontally" true)
      (aset game.scale "pageAlignVertically" true))

    (preload [game]
      (.image game.load "preloadBar" "img/bar.png")
      (.image game.load "logo" "img/logo.png"))

    (create [game]
      (aset game.stage "backgroundColor" "#fff")
      (.start game.state "PreloadState"))))
