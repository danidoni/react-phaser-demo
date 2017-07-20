(ns react-game-performance-prototype.game
  (:require [react-game-performance-prototype.entities :as entities]))

(def ^:const style #js {:font "20px Arial" :fill "#fff"})

(defn stats [game-state]
  (:stats @game-state))

(defn stats-pet-health [game-state]
  (:health (:pet (stats game-state))))

(defn stats-pet-fun [game-state]
  (:fun (:pet (stats game-state))))

(defn selected-item [game-state]
  (:selected-item @game-state))

(defn ui-blocked [game-state]
  (:ui-blocked @game-state))

(defn create-new-item [game x y selected-item]
  (let [item (.sprite game.add x y (name selected-item))]
    (.setTo item.anchor 0.5)
    item))

(defn create-pick-item-fn [game-state clear-selection-fn type]
  (fn [sprite _]
    (if-not (:ui-blocked @game-state)
      (do
        (println "Picked item: " type)

        (clear-selection-fn)
        (aset sprite "alpha" 0.4)
        (swap! game-state assoc-in [:selected-item] type)))))

(defn create-clear-selection-fn [game-state apple candy toy rotate]
  (fn []
    (aset @apple "alpha" 1)
    (aset @candy "alpha" 1)
    (aset @toy "alpha" 1)
    (aset @rotate "alpha" 1)
    (swap! game-state assoc-in [:selected-item] nil)))

(defn eat-item [game-state pet refresh-stats-fn clear-selection-fn new-item]
  (let [animations    (aget @pet "animations")
        pet-health    (stats-pet-health game-state)
        pet-fun       (stats-pet-fun    game-state)
        item-type     (selected-item game-state)
        stats         (stats game-state)
        selected-item (get stats item-type)
        health-buff   (:health selected-item)
        fun-buff      (:fun selected-item)]
    (println "Eating" item-type "," health-buff "health," fun-buff "fun")
    (.destroy new-item)
    (.play animations "funnyfaces")
    (swap! game-state assoc-in [:ui-blocked] false)
    (swap! game-state assoc-in [:stats :pet :health] (+ pet-health health-buff))
    (swap! game-state assoc-in [:stats :pet :fun] (+ pet-fun fun-buff))
    (refresh-stats-fn)
    (clear-selection-fn)))

(defn create-pet-movement! [game game-state event pet refresh-stats-fn clear-selection-fn new-item]
  (let [movement (.tween game.add @pet)
        x (aget event.position "x")
        y (aget event.position "y")]
    (.to movement #js {:x x :y y} 700)
    (.add movement.onComplete #(eat-item game-state pet refresh-stats-fn clear-selection-fn new-item))
    (.start movement)))

(defn create-place-item-fn [game game-state pet refresh-stats-fn clear-selection-fn]
  (fn [sprite event]
    (println "Placing item")
    (let [selected-item (selected-item game-state)
          ui-blocked (ui-blocked game-state)
          x (aget event.position "x")
          y (aget event.position "y")]
      (if (and selected-item (not ui-blocked))
        (do
          (swap! game-state assoc-in [:ui-blocked] true)

          ;; Move the pet towards the item
          (create-pet-movement! game
                                game-state
                                event
                                pet
                                refresh-stats-fn
                                clear-selection-fn
                                (create-new-item game x y selected-item)))))))

(defn handle-rotation-on-complete [game-state sprite refresh-stats-fn]
  (let [current-fun (:fun @game-state)]
    ;; Release the UI
    (swap! game-state assoc-in [:ui-blocked] false)

    (aset sprite "alpha" 1)

    ;; Increase the fun of the pet
    (swap! game-state assoc-in [:pet :fun] (+ current-fun 10))

    ;; Update the visuals for the stats
    (refresh-stats-fn)))

(defn create-pet-rotation! [game game-state pet sprite refresh-stats-fn]
  (let [pet-rotation (.tween game.add @pet)]
    ;; Make the pet do two loops
    (.to pet-rotation #js {:angle "+720"} 1000)
    (.add pet-rotation.onComplete #(handle-rotation-on-complete game-state sprite refresh-stats-fn) game)
    (.start pet-rotation)))

(defn create-rotate-pet-fn [game game-state pet refresh-stats-fn clear-selection-fn]
  (fn [sprite _]
    (if-not (:ui-blocked @game-state)
      (do
        ;; Block the UI until the rotation ends
        (swap! game-state assoc-in [:ui-blocked] true)
        (clear-selection-fn)
        ;; Fade the sprite to acknowledge the selection
        (aset sprite "alpha" 0.4)
        (create-pet-rotation! game game-state pet sprite refresh-stats-fn)))))

(defn create-refresh-stats-fn [health-text-entity fun-text-entity game-state]
  (fn []
    {:pre [(map? @game-state)]}
    (let [pet-stats  (:pet (:stats @game-state))
          pet-health (:health pet-stats)
          pet-fun    (:fun pet-stats)]
      (aset @health-text-entity "text" pet-health)
      (aset @fun-text-entity "text" pet-fun))))

(defn create-game-over-fn [game]
  (fn []
    (.start game.state "HomeState" true false "GAME OVER")))

(defn create-reduce-properties-fn [game-state refresh-stats-fn]
  (fn []
    (let [pet                (:pet (:stats @game-state))
          current-pet-health (:health pet)
          current-pet-fun    (:fun pet)]
      (swap! game-state assoc-in [:stats :pet :health] (- current-pet-health 10))
      (swap! game-state assoc-in [:stats :pet :fun] (- current-pet-fun 15))
      (refresh-stats-fn))))

(defn new-game []
  {:stats {:pet   {:health 100 :fun 100}
           :apple {:health 20}
           :candy {:health -10 :fun 10}
           :toy   {:fun 20}}
   :ui-blocked false
   :selected-item nil})

(defn listen [element event callback]
  (-> @element
      (aget "events")
      (aget event)
      (.add callback)))

(defn state []
  (let [stats-decreaser (atom nil)
        health-text (atom nil)
        fun-text (atom nil)
        fps-text (atom nil)
        background (atom nil)
        apple (atom nil)
        candy (atom nil)
        toy (atom nil)
        rotate (atom nil)
        pet (atom nil)
        game-state (atom nil)]
    (reify
     Object
     (create [game]
       (let [clear-selection-fn (create-clear-selection-fn game-state apple candy toy rotate)
             refresh-stats-fn   (create-refresh-stats-fn health-text fun-text game-state)
             place-item-fn      (create-place-item-fn game game-state pet refresh-stats-fn clear-selection-fn)
             rotate-pet-fn      (create-rotate-pet-fn game game-state pet refresh-stats-fn clear-selection-fn)]

         (reset! game-state (new-game))

         (reset! background (entities/create-background! game))
         (reset! pet (entities/create-pet! game))
         (reset! apple (entities/create-apple! game))
         (reset! candy (entities/create-candy! game))
         (reset! toy (entities/create-toy! game))
         (reset! rotate (entities/create-rotate! game))

         (listen background "onInputDown" place-item-fn)
         (listen apple "onInputDown" (create-pick-item-fn game-state clear-selection-fn :apple))
         (listen candy "onInputDown" (create-pick-item-fn game-state clear-selection-fn :candy))
         (listen toy "onInputDown" (create-pick-item-fn game-state clear-selection-fn :toy))
         (listen rotate "onInputDown" rotate-pet-fn)

         (.text game.add 10 20 "Health: " style)
         (.text game.add 140 20 "Fun: " style)
         (.text game.add 270 20 "FPS: " style)

         (reset! health-text (.text game.add 80 20 "" style))
         (reset! fun-text (.text game.add 185 20 "" style))
         (reset! fps-text (.text game.add 320 20 "" style))

         (refresh-stats-fn)
         (aset game.time "advancedTiming" true)

         (reset! stats-decreaser (.loop game.time.events
                                        (* Phaser.Timer.SECOND 5)
                                        (create-reduce-properties-fn game-state refresh-stats-fn)
                                        game))
         ))

      (update [game]
        (let [pet-health (:health (:pet (:stats @game-state)))
              pet-fun    (:fun (:pet (:stats @game-state)))]
          (if (or (<= pet-health 0)
                  (<= pet-fun 0))
            (do
              (aset @pet "frame" 4)
              (swap! game-state assoc-in [:ui-blocked] true)
              (.add game.time.events 2000 (create-game-over-fn game) game)))))

      (render [game]
        (aset @fps-text "text" (aget game.time "fps"))))))
