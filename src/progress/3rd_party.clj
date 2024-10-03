;;; This namespace contains 3rd party code obtained from other sources. 
;;; Copyright and license information is listed on a per-var basis.

(ns progress.3rd-party)

;; Copyright 2014 A. Webb (https://stackoverflow.com/users/1756702/a-webb)
;; SPDX-License-Identifier: CC-BY-SA-3.0
(defn swap*!
  "Like [[clojure.core/swap!]] but returns a vector of `[old-value new-value]`.
   Adapted from [this StackOverflow question](https://stackoverflow.com/a/22409846)."
  [atom f & args]
  (loop []
    (let [ov @atom
          nv (apply f ov args)]
      (if (compare-and-set! atom ov nv)
        [ov nv]
        (recur)))))
