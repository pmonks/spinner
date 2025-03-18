;
; Copyright © 2022 Peter Monks
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at https://mozilla.org/MPL/2.0/.
;
; SPDX-License-Identifier: MPL-2.0
;

(ns progress.indeterminate
  "Indeterminate progress indicator (aka a \"spinner\"), for the case where the
  progress of a long-running task cannot be determined."
  (:require [clojure.string     :as s]
            [jansi-clj.core     :as jansi]
            [embroidery.api     :as e]
            [progress.ansi      :as ansi]
            [progress.3rd-party :as tp])
  (:refer-clojure :exclude [print]))

(def ^:private fut  (atom nil))
(def ^:private s    (atom :inactive))
(def ^:private msgs (atom nil))

(defn state
  "What state is the indeterminate progress indicator currently in?  One of:

  * `:inactive`
  * `:active`
  * `:shutting-down`"
  []
  @s)

(defn active?
  "Is an indeterminate progress indicator active (currently running)?"
  []
  (= :active @s))

(defn print
  "Schedules the given values for printing, since [clojure.core/print](https://clojuredocs.org/clojure.core/print)
  and related output fns interfere with an active indeterminate progress
  indicator.

  Notes:

  * output is emitted in between 'frames' of the progress indicator, so may not
    appear immediately
  * values are space delimited as in [clojure.core/print](https://clojuredocs.org/clojure.core/print)
    - use [clojure.core/str](https://clojuredocs.org/clojure.core/str),
    [clojure.core/format](https://clojuredocs.org/clojure.core/format), etc. for
    finer control
  * no newlines are inserted - if message(s) are to appear on new lines the
    caller needs to include `\\n` in the value(s)"
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
  (when-let [messages (first (tp/swap*! msgs (constantly nil)))]
    (clojure.core/print messages)
    (flush)
    (ansi/save-cursor!)))

(def default-style
  "The default indeterminate progress indicator style used, if one isn't
  specified, as a `keyword` that has an associated entry in [styles]. This style
  is known to function on all platforms."
  :ascii-spinner)

(def default-delay-ms
  "The default delay between frames (in milliseconds), if one isn't specified."
  100)

(def styles
  "A selection of predefined styles of determinate progress indicators,
  represented as a `map`. Only ASCII progress indicators are known to work
  reliably - other styles depend on the operating system, terminal font &
  encoding, phase of the moon, and how long since your dog last pooped."
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
  "Indeterminate progress indicator logic, for use in a `future` or `Thread` or
  wotnot."
  ([] (indeterminate-progress-indicator nil))
  ([{:keys [delay-in-ms frames fg-colour bg-colour attributes]
     :or   {delay-in-ms default-delay-ms
            frames      (default-style styles)
            fg-colour   :default
            bg-colour   :default
            attributes  [:default]}}]
    (let [delay-in-ms (long (Math/round (double delay-in-ms)))]  ; Coerce delay-in-ms to a long
      (ansi/save-cursor!)
      (loop [i 0]
        (clojure.core/print (str (ansi/apply-colours-and-attrs fg-colour bg-colour attributes (nth frames (mod i (count frames))))
                                 " "))
        (flush)
        (when (pos? delay-in-ms) (Thread/sleep delay-in-ms))  ; Thread/sleep throws on negative values, and sleeping for 0ms makes no sense
        (ansi/restore-cursor!)
        (jansi/erase-line!)
        (print-pending-messages)
        (when (active?)
          (recur (inc i)))))
    nil))

(defn ^:no-doc start!
  "Not intended for public use. Use [animate!] or [animatef!] instead."
  ([] (start! nil))
  ([opts]
   (when-not (compare-and-set! s :inactive :active)
     (throw (java.lang.IllegalStateException. "Progress indicator is already active.")))
   (flush)   ; Flush any residual I/O to stdout before we start animating
   (reset! msgs nil)
   (reset! fut  (e/future* (indeterminate-progress-indicator opts)))
   nil))

(defn ^:no-doc stop!
  "Not intended for public use. Use [animate!] or [animatef!] instead."
  []
  (when (compare-and-set! s :active :shutting-down)
    (try
      @@fut                     ; Wait for the future to stop (deref the atom AND the future)
      (print-pending-messages)  ; Flush any remaining messages
      (finally
        (reset! fut nil)
        (reset! s   :inactive))))
  nil)

(defn animatef!
  "Starts the indeterminate progress indicator, calls fn `f` (a function of zero
  parameters), then stops the progress indicator. Returns the result of `f`.

  **Note: the [[animate!]] macro is preferred over this function.**

  The optional `opts` map may have an/all of these keys:

  * `:frames`      - the frames (a sequence of `String`s) to use for the
                     indeterminate progress indicator (default is
                     `(:ascii-spinner styles)`)
  * `:delay-in-ms` - the delay (in ms) between frames (default is `100`ms)
  * `:fg-colour`   - the foregound colour of the indeterminate progress
                     indicator (default is `:default`) - see [the `jansi-clj`
                     docs](https://github.com/xsc/jansi-clj#colors) for allowed
                     values, and prefix with `bright-` to get the bright
                     equivalent
  * `:bg-colour`   - the background colour of the indeterminate progress
                     indicator (default is `:default`) - see [the `jansi-clj`
                     docs](https://github.com/xsc/jansi-clj#colors) for allowed
                     values, and prefix with `bright-` to get the bright
                     equivalent
  * `:attributes`  - the attributes of the indeterminate progress indicator
                     (default is `[:default]`) - see [the `jansi-clj`
                     docs](https://github.com/xsc/jansi-clj#attributes) for
                     allowed values"
  ([f] (animatef! nil f))
  ([opts f]
    (when f
      (start! opts)
      (try
       (f)
       (finally
         (stop!))))))

(defmacro animate!
  "Wraps the given forms in an indeterminate progress indicator. If the first
  form is the keyword `:opts`, the second form _must_ be a map, containing
  any/all of these keys:

  * `:frames`      - the frames (a sequence of `String`s) to use for the
                     indeterminate progress indicator (default is
                     `(:ascii-spinner styles)`)
  * `:delay-in-ms` - the delay (in ms) between frames (default is `100`ms)
  * `:fg-colour`   - the foregound colour of the indeterminate progress
                     indicator (default is `:default`) - see [the `jansi-clj`
                     docs](https://github.com/xsc/jansi-clj#colors) for allowed
                     values, and prefix with `bright-` to get the bright
                     equivalent
  * `:bg-colour`   - the background colour of the indeterminate progress
                     indicator (default is `:default`) - see [the `jansi-clj`
                     docs](https://github.com/xsc/jansi-clj#colors) for allowed
                     values, and prefix with `bright-` to get the bright
                     equivalent
  * `:attributes`  - the attributes of the indeterminate progress indicator
                     (default is `[:default]`) - see [the `jansi-clj`
                     docs](https://github.com/xsc/jansi-clj#attributes) for
                     allowed values"
  [& body]
  (if (= :opts (first body))
    `(animatef! ~(second body) (fn [] ~@(rest (rest body))))
    `(animatef! (fn [] ~@body))))
