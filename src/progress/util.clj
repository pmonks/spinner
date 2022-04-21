;
; Copyright © 2022 Peter Monks
;
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;     http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;
; SPDX-License-Identifier: Apache-2.0
;

(ns progress.util
  (:require [clojure.string :as s])
  (:refer-clojure :exclude [print]))

(def is-windows?
  "Are we running on Windows?  If so, best to stick with ASCII-only progress indicators...  😢"
  (s/starts-with? (s/lower-case (System/getProperty "os.name")) "windows"))

(defn swap*!
  "Like clojure.core/swap! but returns a vector of [old-value new-value].
   From http://stackoverflow.com/questions/22409638/remove-first-item-from-clojure-vector-atom-and-return-it"
  [atom f & args]
  (loop []
    (let [ov @atom
          nv (apply f ov args)]
      (if (compare-and-set! atom ov nv)
        [ov nv]
        (recur)))))
