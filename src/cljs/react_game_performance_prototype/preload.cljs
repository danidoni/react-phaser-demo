(ns react-game-performance-prototype.preload)

(defn create-logo [game center-x center-y]
  (let [logo (.sprite game.add center-x center-y "logo")]
    (.setTo logo.anchor 0.5)
    logo))

(defn create-preload-bar [game center-x center-y]
  (let [preload-bar (.sprite game.add center-x (+ center-y 128) "preloadBar")]
    (.setTo preload-bar.anchor 0.5)
    (.setPreloadSprite game.load preload-bar)
    preload-bar))

(defn state []
  (reify
    Object

    (preload [game]
      (let [center-x (aget game.world "centerX")
            center-y (aget game.world "centerY")]
        (create-logo game center-x center-y)
        (create-preload-bar game center-x center-y)
        (.image game.load "backyard" "img/backyard.png")
        (.image game.load "apple" "img/apple.png")
        (.image game.load "candy" "img/candy.png")
        (.image game.load "rotate" "img/rotate.png")
        (.image game.load "toy" "img/rubber_duck.png")
        (.image game.load "arrow" "img/arrow.png")
        (.spritesheet game.load "pet" "img/pet.png" 97 83 5 1 1)))

    (create [game]
      (.start game.state "HomeState"))))
