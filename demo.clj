;; Indeterminate Task (aka "spinner")

(require '[progress.indeterminate :as pi])

(print "Something uncountably slow is happening... ")
(pi/animate! :opts {:frames (:clocks pi/styles)}
  (Thread/sleep 5000))
(println)


;; Determinate Task (aka "progress bar")

(require '[progress.determinate :as pd])

(println "And now something countably slow is happening...")
(let [a (atom 0)]
  (pd/animate! a :opts {:total 1000000
                        :redraw-rate 60  ; Use 60 fps for the demo
                        :style (:coloured-ascii-boxes pd/styles)}  ; :emoji-boxes is also fun to try
    (run! (fn [_] (Thread/sleep 0 10) (swap! a inc)) (range 1000000))))  ; Count up to a million, slowly
(println)
