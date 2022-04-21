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

(ns progress.ansi
  "Handy ANSI related functionality. Note: requiring this namespace has the side effect of enabling JANSI."
  (:require [clojure.string :as s]
            [jansi-clj.core :as jansi]))

(jansi/enable!)

(defn save-cursor!
  "Issues both SCO and DEC save-cursor ANSI codes, for maximum compatibility."
  []
  (jansi/save-cursor!)    ; JANSI uses SCO code for cursor positioning, which is unfortunate as they're less widely supported
  (print "\u001B7")       ; So we manually send a DEC code too
  (flush))

(defn restore-cursor!
  "Issues both SCO and DEC restore-cursor ANSI codes, for maximum compatibility."
  []
  (jansi/restore-cursor!)    ; JANSI uses SCO code for cursor positioning, which is unfortunate as they're less widely supported
  (print "\u001B8")          ; So we manually send a DEC code too
  (flush))

(defn debug-print-at
  "Send debug output to the specified screen location (note: ANSI screen location are 1-based)."
  [x y & args]
  (save-cursor!)
  (jansi/cursor! x y)
  (jansi/erase-line!)
  (print (jansi/a :bold (jansi/fg-bright :yellow (jansi/bg :red (str "DEBUG: " (s/join " " args))))))
  (restore-cursor!))

(defn debug-print
  "Send debug output to the upper left corner of the screen, where (hopefully) it minimises interference with everything else."
  [& args]
  (apply debug-print-at 1 1 args))

(defn apply-colour
  "Applies an 'enhanced' colour keyword (which may include the prefix 'bright-') to either the foreground or background of body."
  [fg? colour-key & body]
  (let [bright?     (s/starts-with? (name colour-key) "bright-")
        colour-name (if bright? (keyword (subs (name colour-key) (count "bright-"))) colour-key)]
    (case [fg? bright?]
      [true  true]  (apply jansi/fg-bright colour-name body)
      [true  false] (apply jansi/fg        colour-name body)
      [false true]  (apply jansi/bg-bright colour-name body)
      [false false] (apply jansi/bg        colour-name body))))

(defn apply-attributes
  "Applies all of provided attributes (a seq) to body."
  [attributes & body]
  (apply (apply comp (map #(partial jansi/a %) attributes)) body))
