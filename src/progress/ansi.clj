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
  (jansi/cursor! y x)   ; Note these are reversed compared to the jansi-clj docs due to https://github.com/xsc/jansi-clj/issues/4
  (jansi/erase-line!)
  (print (jansi/a :bold (jansi/fg-bright :yellow (jansi/bg :red (str "DEBUG: " (s/join " " args))))))
  (restore-cursor!))

(defn debug-print
  "Send debug output to the upper left corner of the screen, where (hopefully) it minimises interference with everything else."
  [& args]
  (apply debug-print-at 1 1 args))

(defn apply-colour
  "Applies an 'enhanced' colour keyword (which may include the prefix 'bright-') to either the foreground or background of body."
  [fg? colour-key s]
  (let [bright?     (s/starts-with? (name colour-key) "bright-")
        colour-name (if bright? (keyword (subs (name colour-key) (count "bright-"))) colour-key)]
    (case [fg? bright?]
      [true  true]  (jansi/fg-bright colour-name s)
      [true  false] (jansi/fg        colour-name s)
      [false true]  (jansi/bg-bright colour-name s)
      [false false] (jansi/bg        colour-name s))))

(defn apply-attributes
  "Applies all of provided attributes (a seq) to s (a string)."
  [attributes s]
  ((apply comp (map #(partial jansi/a %) attributes)) s))

(defn apply-colours-and-attrs
  "Applies the foreground colour, background colour, and attributes (a seq) to s (a string)."
  [fg-colour bg-colour attrs s]
  (apply-attributes (if (seq attrs) attrs [:default])
    (apply-colour false (if bg-colour bg-colour :default)
      ((partial apply-colour true (if fg-colour fg-colour :default)) s))))
