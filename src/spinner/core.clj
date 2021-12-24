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

(jansi/enable!)

(def is-windows?
  "Are we running on Windows?  If so, best to stick to the default spinner style. 😢"
  (s/starts-with? (s/lower-case (System/getProperty "os.name")) "windows"))

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
    :ascii-spinner        [\| \/ \- \\]
    :ascii-bouncing-ball  [\. \o \O \° \O \o]
    :ascii-back-and-forth ["[=----]" "[-=---]" "[--=--]" "[---=-]" "[----=]" "[---=-]" "[--=--]" "[-=---]"]

    ; Unicode spinners are unreliable across platforms (especially Windows)
    :box-up-down          [\▁ \▃ \▄ \▅ \▆ \▇ \█ \▇ \▆ \▅ \▄ \▃]
    :box-around           [\▖ \▘ \▝ \▗]
    :box-fade             [\space \░ \▒ \▓ \█ \▓ \▒ \░]
    :box-back-and-forth   ["▓░░░░" "░▓░░░" "░░▓░░" "░░░▓░" "░░░░▓" "░░░▓░" "░░▓░░" "░▓░░░"]
    :box-side-to-side     ["▉" "▊" "▋" "▌" "▍" "▎" "▏" "▎" "▍" "▌" "▋" "▊" "▉"]
    :box-edges            ["▌" "▀" "▐" "▄"]
    :box-wave             ["▁▂▃▄▅" "▂▁▂▃▄" "▃▂▁▂▃" "▄▃▂▁▂" "▅▄▃▂▁" "▆▅▄▃▂" "▇▆▅▄▃" "█▇▆▅▄" "▇█▇▆▅" "▆▇█▇▆" "▅▆▇█▇" "▄▅▆▇█" "▃▄▅▆▇" "▂▃▄▅▆"]
    :line-quadrants       ["┘" "└" "┌" "┐"]
    :line-up-down         ["☱" "☲" "☴" "☲"]
    :dot-spinner          [\⋮ \⋰ \⋯ \⋱]
    :dot-waving           ["⢄" "⢂" "⢁" "⡁" "⡈" "⡐" "⡠" "⡐"  "⡈" "⡁" "⢁" "⢂"]
    :dot-around           ["⣷" "⣯" "⣟" "⡿" "⢿" "⣻" "⣽" "⣾"]
    :dot-snake            ["⠏" "⠛" "⠹" "⢸" "⣰" "⣤" "⣆" "⡇"]
    :arrows               [\← \↖ \↑ \↗ \→ \↘ \↓ \↙]
    :circle-halves        ["◐" "◓" "◑" "◒"]
    :circle-quadrants     ["◴" "◷" "◶" "◵"]
    :square-quadrants     ["◰" "◳" "◲" "◱"]
    :braille              ["⠋" "⠙" "⠸" "⠴" "⠦" "⠇"]
    :pointing-fingers     ["👆" "👉" "👇" "👈"]
    :clocks               ["🕐" "🕑" "🕒" "🕓" "🕔" "🕕" "🕖" "🕗" "🕘" "🕙" "🕚" "🕛"]
    :earth-spinning       ["🌍" "🌎" "🌏"]
    :moon-phases          ["🌑" "🌒" "🌓" "🌔" "🌕" "🌖" "🌗" "🌘"]
  })

(def ^:private fut   (atom nil))
(def ^:private state (atom :inactive))
(def ^:private msgs  (atom nil))

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

(defn- save-cursor!
  "Issues both SCO and DEC save-cursor ANSI codes, for maximum compatibility."
  []
  (jansi/save-cursor!)             ; JANSI uses SCO code for cursor positioning, which is unfortunate as they're less widely supported
  (clojure.core/print "\u001B7")   ; So we manually send a DEC code too
  (flush))

(defn- restore-cursor!
  "Issues both SCO and DEC restore-cursor ANSI codes, for maximum compatibility."
  []
  (jansi/restore-cursor!)          ; JANSI uses SCO code for cursor positioning, which is unfortunate as they're less widely supported
  (clojure.core/print "\u001B8")   ; So we manually send a DEC code too
  (flush))

(defn- print-pending-messages
  "Prints all pending messages"
  []
  (when-let [messages (first (swap*! msgs (constantly nil)))]
    (clojure.core/print messages)
    (flush)
    (save-cursor!)))

#_{:clj-kondo/ignore [:unused-private-var]}
(defn- debug-print
  "Send debug output to the upper left corner of the screen, where (hopefully) it doesn't interfere with the spinner"
  [& args]
  (save-cursor!)
  (jansi/cursor! 0 0)
  (jansi/erase-line!)
  (clojure.core/print (jansi/a :bold (jansi/fg-bright :yellow (jansi/bg :red (str "DEBUG: " (s/join " " args))))))
  (restore-cursor!))

(defn active?
  "Is the spinner active?"
  []
  (= @state :active))

(defn- apply-colour
  "Applies an 'enhanced' colour keyword (which may include the prefix 'bright-') to either the foreground or background of body."
  [fg? key & body]
  (let [name        (name key)
        bright?     (s/starts-with? name "bright-")
        colour-name (if bright? (keyword (subs name (count "bright-"))) key)]
    (case [fg? bright?]
      [true  true]  (apply jansi/fg-bright colour-name body)
      [true  false] (apply jansi/fg        colour-name body)
      [false true]  (apply jansi/bg-bright colour-name body)
      [false false] (apply jansi/bg        colour-name body))))

(defn- apply-attributes
  "Applies all of provided attributes to body."
  [attributes & body]
  (if (seq attributes)
    (apply (apply comp (map #(partial jansi/a %) attributes)) body)
    body))

(defn- spinner
  "Spinner logic, for use in a future or Thread or wotnot"
  ([] (spinner nil))
  ([options]
    (let [delay-in-ms (get options :delay default-delay-ms)
          frames      (get options :frames (default-style styles))
          fg-colour   (get options :fg-colour :default)
          bg-colour   (get options :bg-colour :default)
          attributes  (distinct
                        (concat [(get options :attribute :default)]
                                (get options :attributes [])))]
      (save-cursor!)
      (loop [i 0]
        (clojure.core/print (str (apply-attributes attributes
                                   (apply-colour false bg-colour
                                     (apply-colour true fg-colour
                                       (nth frames (mod i (count frames))))))
                                 " "))
        (flush)
        (Thread/sleep delay-in-ms)
        (restore-cursor!)
        (jansi/erase-line!)
        (print-pending-messages)
        (when (active?)
          (recur (inc i))))
      nil)))

(defn start!
  "Starts the spinner, optionally accepting these options:
   {
     :frames - the frames (array of strings) to use for the spinner (default is (:ascii-spinner styles))
     :delay - the delay (in ms) between frames (default is 100ms)
     :fg-colour - the foregound colour of the spinner (default is :default) - see https://github.com/xsc/jansi-clj#colors for allowed values, and prefix with bright- to get the bright equivalent
     :bg-colour - the background colour of the spinner (default is :default) - see https://github.com/xsc/jansi-clj#colors for allowed values, and prefix with bright- to get the bright equivalent
     :attribute - the attribute of the spinner (default is :default) - see https://github.com/xsc/jansi-clj#attributes for allowed values
     :attributes - the attributes (plural) of the spinner (default is [:default]) - see https://github.com/xsc/jansi-clj#attributes for allowed values
   }"
  ([] (start! nil))
  ([options]
    (when (not= @state :inactive)
      (throw (java.lang.IllegalStateException. "Spinner is already active.")))

    (reset! state :active)
    (reset! fut  (future (spinner options)))
    (reset! msgs nil)
    nil))

(defn stop!
  "Stops the spinner."
  []
  (when (active?)
    (reset! state :shutting-down)
    @@fut    ; Wait for the spinner future to stop (deref the atom AND the future)
    (reset! state :inactive)
    (reset! fut   nil)
    (reset! msgs  nil))
  nil)

(defn spin!
  "Starts the spinner, calls fn f, then stops the spinner. Returns the result of f."
  ([f] (spin! f nil))
  ([f options]
    (start! options)
    (try
     (f)
     (finally
       (stop!)))))

(defn print
  "Schedules the given values for printing (ala clojure.core/print).
   Notes:
   * will only produce output if the spinner is active - throws if it is inactive
   * output is emitted in between 'frames' of the spinner, so may not appear immediately
   * values are space delimited (as in clojure.core/print) - use clojure.core/str for finer control
   * no newlines are inserted - if message(s) are to appear on new lines the caller needs to include \\newline in the value(s)"
  [& more]
  (when-not (active?)
    (throw (java.lang.IllegalStateException. "Spinner is not active.")))

  (swap! msgs str (s/join " " more))
  nil)
