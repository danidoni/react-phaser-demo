(ns react-game-performance-prototype.entities)

(defn create-background! [game]
  (let [background (.sprite game.add 0 0 "backyard")]
    (aset background "inputEnabled" true)
    background))

(defn create-pet! [game]
  (let [pet (.sprite game.add 100 400 "pet")]
    (.setTo pet.anchor 0.5)
    (.add pet.animations "funnyfaces" #js [1 2 3 2 1] 7 false)
    (aset pet "inputEnabled" true)
    (.enableDrag pet.input)
    pet))

(defn create-apple! [game]
  (let [apple (.sprite game.add 72 570 "apple")]
    (.setTo apple.anchor 0.5)
    (aset apple "inputEnabled" true)
    apple))

(defn create-candy! [game]
  (let [candy (.sprite game.add 144 570 "candy")]
    (.setTo candy.anchor 0.5)
    (aset candy "inputEnabled" true)
    candy))

(defn create-toy! [game]
  (let [toy (.sprite game.add 216 570 "toy")]
    (.setTo toy.anchor 0.5)
    (aset toy "inputEnabled" true)
    toy))

(defn create-rotate! [game]
  (let [rotate (.sprite game.add 288 570 "rotate")]
    (.setTo rotate.anchor 0.5)
    (aset rotate "inputEnabled" true)
    rotate))
