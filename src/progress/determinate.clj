;
; Copyright © 2022 Peter Monks
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at https://mozilla.org/MPL/2.0/.
;
; SPDX-License-Identifier: MPL-2.0
;

(ns progress.determinate
  "Determinate progress indicator (aka a \"progress bar\"), for the case where
  the progress of a long-running task can be determined."
  (:require [clojure.string :as s]
            [jansi-clj.core :as jansi]
            [wcwidth.api    :as w]
            [embroidery.api :as e]
            [progress.ansi  :as ansi]))

(def ^:private lock (Object.))

(def default-style
  "The default determinate progress indicator style used, if one isn't
  specified, as a `keyword` that has an associated entry in [styles]. This style
  is known to function on all platforms."
  :ascii-basic)

(def styles
  "A selection of predefined styles of determinate progress indicators,
  represented as a `map`. Only ASCII progress indicators are known to work
  reliably - other styles depend on the operating system, terminal font &
  encoding, phase of the moon, and how long since your dog last pooped."
  {
    ; ASCII determinate progress indicators are reliable across platforms
    :ascii-basic {:left  "["
                  :right "]"
                  :empty " "
                  :full  "#"}   ; Note: does not have a :tip
    :ascii-boxes {:left  "▉"
                  :right "▉"
                  :empty " "
                  :full  "░"
                  :tip   "▓"}

    ; Emoji determinate progress indicators are unreliable across platforms (especially Windows)
    :emoji-circles {:left  "【" ; Note: double width without whitespace, despite appearances
                    :right "】" ; Note: double width without whitespace, despite appearances
                    :empty "⚫"
                    :full  "⚪️"
                    :tip   "🟡"}
    :emoji-boxes   {:empty "⬛️"
                    :full  "⬜️"
                    :tip   "🟨"}
  })

(defn- clamp
  "Clamps a value within a range."
  [mn mx x]
  (max mn (min mx x)))

(defn- redraw-progress-indicator!
  "Redraws the progress indicator."
  [style style-widths label line width counter? total units new-value]
  ; Make sure this code is non re-entrant
  (locking lock
    (let [percent-complete (/ (double new-value) total)
          body-cols        (- width
                              (if-not (s/blank? label) (:label style-widths) 0)
                              (if (:left  style)       (:left  style-widths) 0)
                              (if (:right style)       (:right style-widths) 0)
                              (if counter?             (inc (* 2 (count (str total)))) 0)
                              (if (and counter? (not (s/blank? units))) (:units style-widths) 0))
          tip-cols         (if (:tip style) 1 0)
          tip-chars        (* tip-cols (get style-widths :tip 0))
          fill-cols        (clamp 0 body-cols (Math/ceil (* percent-complete body-cols)))
          fill-chars       (- (Math/ceil (/ fill-cols (:full style-widths))) tip-chars)
          empty-cols       (- body-cols (* fill-chars (:full style-widths)))  ; We do it this way due to rounding
          empty-chars      (Math/floor (/ empty-cols (:empty style-widths)))]
      (when line
        (ansi/save-cursor!)
        (jansi/cursor! 1 line))
      (print (str ; Go to the start of the line
                  "\r"

                  ; Label (optional)
                  (when-not (s/blank? label)
                    (ansi/apply-colours-and-attrs (:label-fg-colour style)
                                                  (:label-bg-colour style)
                                                  (:label-attrs     style)
                                                  (str label " ")))

                  ; Left (optional)
                  (when (:left style)
                    (ansi/apply-colours-and-attrs (:left-fg-colour style)
                                                  (:left-bg-colour style)
                                                  (:left-attrs     style)
                                                  (:left           style)))
                  ; Full
                  (ansi/apply-colours-and-attrs (:full-fg-colour style)
                                                (:full-bg-colour style)
                                                (:full-attrs     style)
                                                (s/join (repeat fill-chars (:full style))))
                  ; Tip (optional)
                  (when (:tip style)
                    (ansi/apply-colours-and-attrs (:tip-fg-colour style)
                                                  (:tip-bg-colour style)
                                                  (:tip-attrs     style)
                                                  (:tip           style)))
                  ; Empty
                  (ansi/apply-colours-and-attrs (:empty-fg-colour style)
                                                (:empty-bg-colour style)
                                                (:empty-attrs     style)
                                                (s/join (repeat empty-chars (:empty style))))
                  ; Right (optional)
                  (when (:right style)
                    (ansi/apply-colours-and-attrs (:right-fg-colour style)
                                                  (:right-bg-colour style)
                                                  (:right-attrs     style)
                                                  (:right           style)))
                  ; Counter (optional)
                  (when counter?
                    (ansi/apply-colours-and-attrs (:counter-fg-colour style)
                                                  (:counter-bg-colour style)
                                                  (:counter-attrs     style)
                                                  (str " " (int new-value) "/" (int total)
                                                       (when-not (s/blank? units)
                                                         (ansi/apply-colours-and-attrs (:units-fg-colour style)
                                                                                       (:units-bg-colour style)
                                                                                       (:units-attrs     style)
                                                                                       (str " " units))))))))
      (jansi/erase-line!)
      (when line (ansi/restore-cursor!))
      (flush))))

(defn- poll-atom
  "Polls atom `value-atom` every `poll-interval-ms` and calls `render-fn!` (a
  function of one argument - the current value of the atom), if it has changed.
  Will stop when `stop-flag-atom` becomes logically `true`."
  [value-atom stop-flag-atom ^long poll-interval-ms render-fn!]
  (loop [previous-value nil]
    (let [current-value @value-atom]
      (when-not (and @stop-flag-atom
                     (= current-value previous-value))
        (render-fn! current-value)
      (when-not @stop-flag-atom
        (when (pos? poll-interval-ms) (Thread/sleep poll-interval-ms))
        (recur current-value))))))

(defn- valid-width
  "Returns a valid width for `s` (throws on zero or non-printing)."
  [s]
  (when s
    (let [width (w/display-width s)]
      (if (pos? width)
        width
        (throw (ex-info (str "Invalid width (" width ") for style string " s) {:string s :width width}))))))

(defn animatef!
  "Wraps execution of the given function in a determinate progress indicator,
  monitoring atom `a` (a number between `0` and `(:total opts)`, representing
  progress).

  **Note: the [[animate!]] macro is preferred over this function.**

  The optional `opts` map may have an/all of these keys:

  * `:style`     - a map defining the style (characters, colours, and
                   attributes) to use when printing the progress indicator.
                   Optional, default: `(:ascii-basic styles)`
  * `:label`     - a `String` to display before the progress indicator - this
                   could be the filename for a lengthy file download, for
                   example. Optional, default: `nil`
  * `:line`      - the line number on the screen at which to display the
                   progress indicator (note: 1-based). Optional, default: `nil`
                   (display at current line)
  * `:width`     - the (approximate) desired width of the progress indicator,
                   including any labels and counters. This is approximate
                   because emoji-based styles may not take up an even fraction
                   of the desired width. Optional, default: `72`
  * `:total`     - the final number that the atom will reach. Optional, default:
                   `100` (i.e. the atom represents a %age)
  * `:units`     - a unit label (`String`) to display after the counter - this
                   could be a file size unit (`\"KB\"`, `\"MB\"`, etc.), for
                   example. Optional, default: `nil`
  * `:counter?`  - whether to display a counter to the right of the progress
                   indicator. Optional, default: `true` (display a counter)
  * `:preserve?` - flag indicating whether to preserve the progress indicator on
                   screen after it finishes (vs erasing it). Optional, default:
                   `false` (erase it)
  * `:redraw-rate` - how many times per second `a` will be checked for changes,
                   and the progress indicator redrawn if the value of `a` has
                   changed. Optional, default `10`"
  ([a f] (animatef! a nil f))
  ([a
    {:keys [style label line width total units counter? preserve? redraw-rate]
       :or {style       (get styles default-style)
            total       100
            width       72
            counter?    true
            preserve?   false
            redraw-rate 10}}
    f]
    (when (and a f)
      (let [style-widths     (merge {:empty (valid-width (:empty style))
                                     :full  (valid-width (:full  style))}
                                    (when-not (s/blank? label) {:label (inc (valid-width label))})   ; Include space delimiter
                                    (when (:left  style)       {:left  (valid-width (:left  style))})
                                    (when (:right style)       {:right (valid-width (:right style))})
                                    (when (:tip   style)       {:tip   (valid-width (:tip   style))})
                                    (when-not (s/blank? units) {:units (inc (valid-width units))}))  ; Include space delimiter
            render-fn!       (partial redraw-progress-indicator! style style-widths label line width counter? total units)
            stop-flag        (atom false)
            poll-interval-ms (Math/round (double (/ 1000 redraw-rate)))
            fut              (e/future* (poll-atom a stop-flag poll-interval-ms render-fn!))]
        (try
          (f)
          (finally
            ; Teardown logic
            (swap! stop-flag (constantly true))
            (future-cancel fut)  ; Just in case...
            (locking lock  ; Make sure this isn't re-entrant with the future, since the TTY can only save a single cursor position at a time
              (if preserve?
                ; Make sure we draw the indicator with the final value of the atom
                (do
                  (render-fn! @a)
                  (when-not line (println)))
                ; Erase the line the indicator was on
                (do
                  (when line
                    (ansi/save-cursor!)
                    (jansi/cursor! 1 line))
                  (print "\r")
                  (jansi/erase-line!)
                  (when line (ansi/restore-cursor!))))
              (flush))))))))

(defmacro animate!
  "Wraps execution of the given forms in a determinate progress indicator,
  monitoring atom `a` (a number between `0` and `(:total opts)`, representing
  progress). If the first form is the keyword `:opts`, the second form _must_ be
  a map, containing any/all of these keys:

  * `:style`     - a map defining the style (characters, colours, and
                   attributes) to use when printing the progress indicator.
                   Optional, default: `(:ascii-basic styles)`
  * `:label`     - a `String` to display before the progress indicator - this
                   could be the filename for a lengthy file download, for
                   example. Optional, default: `nil`
  * `:line`      - the line number on the screen at which to display the
                   progress indicator (note: 1-based). Optional, default: `nil`
                   (display at current location)
  * `:width`     - the (approximate) desired width of the progress indicator,
                   including any labels and counters. This is approximate
                   because emoji-based styles may not take up an even fraction
                   of the desired width. Optional, default: `72`
  * `:total`     - the final number that the atom will reach. Optional, default:
                   `100` (i.e. the atom represents a %age)
  * `:units`     - a unit label (`String`) to display after the counter - this
                   could be a file size unit (`\"KB\"`, `\"MB\"`, etc.), for
                   example. Optional, default: `nil`
  * `:preserve?` - flag indicating whether to preserve the progress indicator on
                   screen after it finishes (vs erasing it). Optional, default:
                   `false` (erase it)
  * `:counter?`  - whether to display a counter to the right of the progress
                   indicator. Optional, default: `true` (display a counter)"
  [a & body]
  (if (= :opts (first body))
    `(animatef! ~a ~(second body) (fn [] ~@(rest (rest body))))
    `(animatef! ~a (fn [] ~@body))))
