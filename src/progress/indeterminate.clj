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

(ns progress.indeterminate
  "Indetermine progress indicator (aka a \"spinner\"), for the case where the progress of a long-running task cannot be determined."
  (:require [clojure.string :as s]
            [jansi-clj.core :as jansi]
            [progress.ansi  :as ansi]
            [progress.util  :as u])
  (:refer-clojure :exclude [print]))

(def ^:private fut  (atom nil))
(def ^:private s    (atom :inactive))
(def ^:private msgs (atom nil))

(defn state
  "What state is the indeterminate progress indicator currently in?  One of:
  * :inactive
  * :active
  * :shutting-down"
  []
  @s)

(defn active?
  "Is an indeterminate progress indicator active (currently running)?"
  []
  (= :active @s))

(defn print
  "Schedules the given values for printing (ala clojure.core/print), since clojure.core/print (and similar output fns) interfere with an active indeterminate progress indicator.

   Notes:
   * output is emitted in between 'frames' of the progress indicator, so may not appear immediately
   * values are space delimited (as in clojure.core/print) - use clojure.core/str for finer control
   * no newlines are inserted - if message(s) are to appear on new lines the caller needs to include \\newline in the value(s)"
  [& more]
  (when (seq more)
    (let [msg (s/join " " more)]
      (if (= @s :active)
        (swap! msgs str msg)
        (do
          (clojure.core/print msg)   ; If a progress indicator isn't active, just print immediately
          (flush)))))
  nil)

(defn- print-pending-messages
  "Prints all pending messages"
  []
  (when-let [messages (first (u/swap*! msgs (constantly nil)))]
    (clojure.core/print messages)
    (flush)
    (ansi/save-cursor!)))


(def default-style
  "The default indeterminate progress indicator style used, if one isn't specified.  This is known to function on all platforms."
  :ascii-spinner)

(def default-delay-ms
  "The default delay between frames (in milliseconds), if one isn't specified."
  100)

(def styles
  "A selection of predefined styles of indeterminate progress indicators. Only ASCII progress indicators are known to
   work reliably - other styles depend on the operating system, terminal font & encoding, phase of the moon, and how
   long since your dog last pooped."
  {
    ; ASCII indeterminate progress indicators are reliable across platforms
    :ascii-spinner        [\| \/ \- \\]
    :ascii-bouncing-ball  [\. \o \O \° \O \o]
    :ascii-back-and-forth ["[=----]" "[-=---]" "[--=--]" "[---=-]" "[----=]" "[---=-]" "[--=--]" "[-=---]"]

    ; Unicode indeterminate progress indicators are unreliable across platforms (especially Windows)
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

(defn- indeterminate-progress-indicator
  "Indeterminate progress indicator logic, for use in a future or Thread or wotnot"
  ([] (indeterminate-progress-indicator nil))
  ([{:keys [delay-in-ms frames fg-colour bg-colour attributes]
     :or   {delay-in-ms default-delay-ms
            frames      (default-style styles)
            fg-colour   :default
            bg-colour   :default
            attributes  [:default]}}]
    (ansi/save-cursor!)
    (loop [i 0]
      (clojure.core/print (str (ansi/apply-colours-and-attrs fg-colour bg-colour attributes (nth frames (mod i (count frames))))
                               " "))
      (flush)
      (Thread/sleep delay-in-ms)
      (ansi/restore-cursor!)
      (jansi/erase-line!)
      (print-pending-messages)
      (when (active?)
        (recur (inc i))))
    nil))

(defn start!
  "Not intended for public use. Use animate! or animatef! instead."
  ([] (start! nil))
  ([opts]
   (when-not (compare-and-set! s :inactive :active)
     (throw (java.lang.IllegalStateException. "Progress indicator is already active.")))

   (flush)   ; Flush any residual I/O to stdout before we start animating
   (reset! fut  (future (indeterminate-progress-indicator opts)))
   (reset! msgs nil)
   nil))

(defn stop!
  "Not intended for public use. Use animate! or animatef! instead."
  []
  (when (compare-and-set! s :active :shutting-down)
    @@fut                     ; Wait for the future to stop (deref the atom AND the future)
    (print-pending-messages)  ; Flush any remaining messages
    (reset! fut nil)
    (reset! s   :inactive))
  nil)

(defn animatef!
  "Starts the indeterminate progress indicator, calls fn f (a function of zero parameters), then stops it. Returns the result of f.

  Note that the `animate!` macro is preferred over this function.

  opts is a map, optionally containing these keys:
    :frames     - the frames (a sequence of strings) to use for the indeterminate progress indicator (default is (:ascii-spinner styles))
    :delay      - the delay (in ms) between frames (default is 100ms)
    :fg-colour  - the foregound colour of the indeterminate progress indicator (default is :default) - see https://github.com/xsc/jansi-clj#colors for allowed values, and prefix with bright- to get the bright equivalent
    :bg-colour  - the background colour of the indeterminate progress indicator (default is :default) - see https://github.com/xsc/jansi-clj#colors for allowed values, and prefix with bright- to get the bright equivalent
    :attributes - the attributes of the indeterminate progress indicator (default is [:default]) - see https://github.com/xsc/jansi-clj#attributes for allowed values"
  ([f] (animatef! nil f))
  ([opts f]
    (when f
      (start! opts)
      (try
       (f)
       (finally
         (stop!))))))

(defmacro animate!
  "Wraps the given forms in the indeterminate progress indicator. If the first form is the keyword `:opts`, the second form must be a map, optionally containing these keys:
    :frames     - the frames (a sequence of strings) to use for the indeterminate progress indicator (default is (:ascii-spinner styles))
    :delay      - the delay (in ms) between frames (default is 100ms)
    :fg-colour  - the foregound colour of the indeterminate progress indicator (default is :default) - see https://github.com/xsc/jansi-clj#colors for allowed values, and prefix with bright- to get the bright equivalent
    :bg-colour  - the background colour of the indeterminate progress indicator (default is :default) - see https://github.com/xsc/jansi-clj#colors for allowed values, and prefix with bright- to get the bright equivalent
    :attributes - the attributes of the indeterminate progress indicator (default is [:default]) - see https://github.com/xsc/jansi-clj#attributes for allowed values"
  [& body]
  (if (= :opts (first body))
    `(animatef! ~(second body) (fn [] ~@(rest (rest body))))
    `(animatef! (fn [] ~@body))))
