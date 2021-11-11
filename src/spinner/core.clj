;
; Copyright © 2014 Peter Monks
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

(ns spinner.core
  (:require [clojure.string :as s]
            [jansi-clj.core :as jansi])
  (:refer-clojure :exclude [print]))

(def is-windows?
  "Are we running on Windows?  If so, best to stick to the default spinner style. 😢"
  (.startsWith ^String (s/lower-case (System/getProperty "os.name")) "windows"))

(def default-style
  "The default spinner style used, if one isn't specified.  This is known to function on all platforms."
  :ascii-spinner)

(def default-delay-ms
  "The default delay between frames (in milliseconds), if one isn't specified."
  100)

(def styles
  "A selection of predefined styles of spinner. Only ASCII spinners are known to work reliably -
   other styles depend on the operating system, terminal font & encoding, phase of the moon, and
   how long since your dog last pooped."
  {
    ; ASCII spinners are reliable across platforms
    :ascii-spinner       [\| \/ \- \\]
    :ascii-bouncing-ball [\. \o \O \° \O \o]

    ; Unicode spinners are unreliable across platforms (especially Windows)
    :box-up-down         [\▁ \▃ \▄ \▅ \▆ \▇ \█ \▇ \▆ \▅ \▄ \▃]
    :box-around          [\▖ \▘ \▝ \▗]
    :box-fade            [\space \░ \▒ \▓ \█ \▓ \▒ \░]
    :box-side-to-side    ["▉" "▊" "▋" "▌" "▍" "▎" "▏" "▎" "▍" "▌" "▋" "▊" "▉"]
    :box-edges           ["▌" "▀" "▐" "▄"]
    :line-quadrants      ["┘" "└" "┌" "┐"]
    :line-up-down        ["☱" "☲" "☴" "☲"]
    :dot-spinner         [\⋮ \⋰ \⋯ \⋱]
    :dot-waving          ["⢄" "⢂" "⢁" "⡁" "⡈" "⡐" "⡠" "⡐"  "⡈" "⡁" "⢁" "⢂"]
    :dot-around          ["⣷" "⣯" "⣟" "⡿" "⢿" "⣻" "⣽" "⣾"]
    :arrows              [\← \↖ \↑ \↗ \→ \↘ \↓ \↙]
    :circle-halves       ["◐" "◓" "◑" "◒"]
    :circle-quadrants    ["◴" "◷" "◶" "◵"]
    :square-quadrants    ["◰" "◳" "◲" "◱"]
    :braille             ["⠋" "⠙" "⠸" "⠴" "⠦" "⠇"]
    :pointing-fingers    ["👆" "👉" "👇" "👈"]
    :clocks              ["🕐" "🕑" "🕒" "🕓" "🕔" "🕕" "🕖" "🕗" "🕘" "🕙" "🕚" "🕛"]
    :earth-spinning      ["🌍" "🌎" "🌏"]
    :moon-phases         ["🌑" "🌒" "🌓" "🌔" "🌕" "🌖" "🌗" "🌘"]
  })

(defn- select-values
  "Solution 3 from http://blog.jayfields.com/2011/01/clojure-select-keys-select-values-and.html"
  [m ks]
  (when (and m ks)
    (remove nil? (reduce #(conj %1 (m %2)) [] ks))))

(defn- select-value-default
  "Selects the first value of ks in m, with default-value if none of ks were found."
  [m ks default-value]
  (let [value (first (select-values m ks))]
    (if (nil? value)
      default-value
      value)))

(def ^:private pending-messages (atom nil))

(defn- swap*!
  "Like clojure.core/swap! but returns a vector of [old-value new-value].
   From http://stackoverflow.com/questions/22409638/remove-first-item-from-clojure-vector-atom-and-return-it"
  [atom f & args]
  (loop []
    (let [ov @atom
          nv (apply f ov args)]
      (if (compare-and-set! atom ov nv)
        [ov nv]
        (recur)))))

(defn- print-pending-messages
  []
  (when-let [messages (first (swap*! pending-messages (constantly nil)))]
    (clojure.core/print messages)
    (flush)))

(defn- spinner
  ([] (spinner nil))
  ([options]
    (let [delay-in-ms (get options :delay default-delay-ms)
          frames      (get options :frames (default-style styles))
          fg-colour   (select-value-default options [:fg-colour :fg-color] :default)
          bg-colour   (select-value-default options [:bg-colour :bg-color] :default)
          attribute   (get options :attribute :default)]
      (try
        (loop [i (int 0)]
          (try
            (clojure.core/print (str (jansi/a  attribute
                                     (jansi/bg bg-colour
                                     (jansi/fg fg-colour (nth frames i))))
                                     " "))
            (flush)
            (Thread/sleep delay-in-ms)
          (finally  ; Always erase the spinner, even if we were interrupted
            (let [frame-length (inc (count (str (nth frames i))))]
              (clojure.core/print (str (jansi/cursor-left frame-length)
                                       (jansi/erase-line)))
              (flush))))
          (print-pending-messages)
          (recur (int (mod (inc i) (count frames)))))
        (catch InterruptedException _
          (comment "Swallow the exception silently and terminate.")))
      nil)))

(defn active?
  "Is the given spinner active?"
  [spinner]
  (.isAlive ^Thread spinner))

(defn create!
  "Creates a spinner and returns it, but does not start it.

   Optionally accepts an options map - supported options are:
   {
     :frames - the frames (array of strings) to use for the spinner (default is (:ascii-spinner styles))
     :delay - the delay (in ms) between frames (default is 100ms)
     :fg-colour / :fg-color - the foregound colour of the spinner (default is :default) - see https://github.com/xsc/jansi-clj#colors for allowed values
     :bg-colour / :bg-colour - the background colour of the spinner (default is :default) - see https://github.com/xsc/jansi-clj#colors for allowed values
     :attribute - the attribute of the spinner (default is :default) - see https://github.com/xsc/jansi-clj#attributes for allowed values
   }

   Note: this method has the side effect of enabling JANSI - see https://github.com/xsc/jansi-clj#globally-enabledisable-ansi-codes"
  ([] (create! nil))
  ([options]
   (jansi/enable!)
   (doto (Thread. ^Runnable #(spinner options))
     (.setDaemon true))))

(defn start!
  "Starts the given spinner."
  [spinner]
  (when (active? spinner)
    (throw (java.lang.IllegalStateException. "Spinner is already active.")))
  (.start ^Thread spinner)
  nil)

(defn create-and-start!
  "Creates and starts a spinner, returning it."
  ([] (create-and-start! nil))
  ([options]
   (let [spinner (create! options)]
     (start! spinner)
     spinner)))

(defn stop!
  "Stops the given spinner.
   Note: after being stopped, a spinner cannot be restarted."
  [spinner]
  (when (not (active? spinner))
    (throw (java.lang.IllegalStateException. "Spinner is not active.")))
  (doto ^Thread spinner
    (.interrupt)
    (.join))
  (reset! pending-messages "")
  nil)

(defn spin!
  "Creates and starts a spinner, calls fn f, then stops the spinner. Returns the result of f."
  ([f] (spin! f nil))
  ([f options]
   (let [spinner (create-and-start! options)]
     (try
       (f)
       (finally
         (stop! spinner))))))

(defn print
  "Schedules the given values for printing (ala clojure.core/print), without interrupting the active spinner.
   Notes:
   * will only produce output if a spinner is active
   * output is emitted in between 'frames' of the spinner, so may not appear immediately
   * values are space delimited (as in clojure.core/print) - use clojure.core/str for finer control
   * no newlines are inserted - if message(s) are to appear on new lines the caller needs to include \\newline in the value(s)"
  [& more]
  (swap! pending-messages str (s/join \space more))
  nil)
