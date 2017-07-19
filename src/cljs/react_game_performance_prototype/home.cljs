(ns react-game-performance-prototype.home)

(defn create-background-sprite! [game]
  (let [background (.sprite game.add 0 0 "backyard")]
    (aset background "inputEnabled" true)
    (.add background.events.onInputDown
          #(.start game.state "GameState")
          game)
    background))

(defn create-start-banner! [game style]
  (let [y (+ (aget game.world "centerY") 200)
        banner (.text game.add 30 y "TOUCH TO START" style)]
    banner))

(defn state []
  (let [message-atom (atom nil)]
    (reify
      Object
      (init [game message]
        (reset! message-atom message))

      (create [game]
        (let [center-y (aget game.world "centerY")
              style #js {:font "35px Arial" :fill "#fff"}]
          (create-background-sprite! game)
          (create-start-banner! game style)

          (if message-atom
            (.text game.add 60 (- center-y 200) @message-atom style)))))))
