;
; Copyright © 2022 Peter Monks
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at https://mozilla.org/MPL/2.0/.
;
; SPDX-License-Identifier: MPL-2.0
;

(ns progress.ansi
  "Handy ANSI related functionality. Note: requiring this namespace has the side
  effect of enabling [JANSI](https://github.com/fusesource/jansi?tab=readme-ov-file#example-usage)."
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

(defn print-at
  "Send text output to the specified screen locations (note: ANSI screen
  coordinates are 1-based). msgs may include jansi formatting."
  [x y & msgs]
  (save-cursor!)
  (jansi/cursor! x y)
  (jansi/erase-line!)
  (apply print msgs)
  (restore-cursor!))

(defn debug-print-at
  "Send debug output to the specified screen location (note: ANSI screen
  coordinates are 1-based)."
  [x y & args]
  (print-at x y (jansi/a :bold (jansi/fg-bright :yellow (jansi/bg :red (str "DEBUG: " (s/join " " args)))))))

(defn debug-print
  "Send debug output to the upper left corner of the screen, where (hopefully)
  it minimises interference with everything else."
  [& args]
  (apply debug-print-at 1 1 args))

(defn apply-colour
  "Applies an 'enhanced' colour keyword (which may include the prefix 'bright-')
  to either the foreground or background of body."
  [fg? colour-key s]
  (let [bright?     (s/starts-with? (name colour-key) "bright-")
        colour-name (if bright? (keyword (subs (name colour-key) (count "bright-"))) colour-key)]
    (case [fg? bright?]
      [true  true]  (jansi/fg-bright colour-name s)
      [true  false] (jansi/fg        colour-name s)
      [false true]  (jansi/bg-bright colour-name s)
      [false false] (jansi/bg        colour-name s))))

(defn apply-attributes
  "Applies all of provided attributes (a seq) to `s` (a `String`)."
  [attributes s]
  ((apply comp (map #(partial jansi/a %) attributes)) s))

(defn apply-colours-and-attrs
  "Applies the foreground colour, background colour, and attributes (a seq) to
  `s` (a `String`)."
  [fg-colour bg-colour attrs s]
  (apply-attributes (if (seq attrs) attrs [:default])
    (apply-colour false (if bg-colour bg-colour :default)
      ((partial apply-colour true (if fg-colour fg-colour :default)) s))))
