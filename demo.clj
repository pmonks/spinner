;; Indeterminate Task (aka "spinner")

(require '[progress.indeterminate :as spinner])

(print "Something uncountably slow is happening... ")
(spinner/animate! :opts {:frames (:clocks spinner/styles)}
  (Thread/sleep 5000))
(println)


;; Determinate Task (aka "progress bar")

(require '[progress.determinate :as progress-bar])

(println "And now something countably slow is happening...")
(let [a (atom 0)]
  (progress-bar/animate! a :opts {:total 1000000
                                  :redraw-rate 60  ; Use 60 fps for the demo
                                  :style (:coloured-ascii-boxes progress-bar/styles)}  ; :emoji-boxes is also fun to try
    (run! (fn [_] (Thread/sleep 0 10) (swap! a inc)) (range 1000000))))  ; Count up to a million, slowly
(println)
