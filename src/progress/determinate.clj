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

(ns progress.determinate
  "Determine progress indicator (aka a \"progress bar\"), for the case where the progress of a long-running task can be determined."
  (:require [clojure.string :as s]
            [jansi-clj.core :as jansi]
            [wcwidth.api    :as w]
            [progress.ansi  :as ansi]
            [progress.util  :as u]))

(def ^:private lock (Object.))

(def default-style
  "The default determinate progress indicator style used, if one isn't specified.  This is known to function on all platforms."
  :ascii-basic)

(def styles
  "A selection of predefined styles of determinate progress indicators. Only ASCII progress indicators are known to
   work reliably - other styles depend on the operating system, terminal font & encoding, phase of the moon, and how
   long since your dog last pooped."
  {
    ; ASCII determinate progress indicators are reliable across platforms
    :ascii-basic {:left  "[" :left-attrs  []
                  :right "]" :right-attrs []
                  :empty " " :empty-attrs []
                  :full  "#" :full-attrs  []}   ; Note: does not have a :tip
    :ascii-boxes {:left  "▉" :left-attrs  []
                  :right "▉" :right-attrs []
                  :empty " " :empty-attrs []
                  :full  "░" :full-attrs  []
                  :tip   "▓" :tip-attrs   []}

    ; Unicode determinate progress indicators are unreliable across platforms (especially Windows)
  })

(defn- col1-and-erase-to-eol!
  []
  (print "\r")
  (jansi/erase-line!)
  (flush))

(defn- clamp
  "Clamps a value within a range."
  [mn mx x]
  (max mn (min mx x)))

(defn- redraw-progress-indicator!
  [style style-widths line width counter? total _ _ _ new-value]
  ; Make sure this code is non re-entrant
  (locking lock
    (let [percent-complete (/ (double new-value) total)
          body-width       (- width
                              (if (:left  style-widths) (:left style-widths)  0)
                              (if (:right style-widths) (:right style-widths) 0))
          num-fill-columns (clamp 0 body-width (- (Math/ceil (* percent-complete body-width)) (if (:tip style-widths) (:tip style-widths) 0)))]
      (when line
        (ansi/save-cursor!)
        (jansi/cursor 1 line))
      (col1-and-erase-to-eol!)
      (print (str (when (:left style) (:left style))
                  (s/join (repeat (/ num-fill-columns (:full style-widths)) (:full style)))
                  (when (:tip style) (:tip style))
                  (s/join (repeat (/ (- body-width num-fill-columns) (:empty style-widths)) (:empty style)))
                  (when (:right style) (:right style))
                  (when counter? (str " " (int new-value) "/" (int total)))))  ; Counter at the end
      (flush)
      (when line (ansi/restore-cursor!))
      (flush))))


(defn animatef!
  "Starts the determinate progress indicator, monitoring atom a (a number between 0 and (:total opts) representing completeness), calls fn f (a function of zero parameters), then stops it. Returns the result of f.

  Note that the `animate!` macro is preferred over this function.

  opts is a map, optionally containing these keys:
    :style     - ####TODO!!!!
    :line      -
    :width     -
    :total     -
    :preserve  -
    :counter?  -"
  ([a f] (animatef! a nil f))
  ([a opts f]
    (when (and a f)
      ; Setup logic
      (let [style      (get opts :style (get styles default-style))
            line       (get opts :line)
            width      (get opts :width 70)
            counter?   (get opts :counter? true)
            total      (get opts :total 100)
            render-fn! (partial redraw-progress-indicator! style
                                                           (merge {:empty (w/display-width (:empty style))
                                                                   :full  (w/display-width (:full  style))}
                                                                  (when (:left  style) {:left  (w/display-width (:left  style))})   ; Precompute style element widths, so that we don't have to do it in the tight loop
                                                                  (when (:right style) {:right (w/display-width (:right style))})
                                                                  (when (:tip   style) {:tip   (w/display-width (:tip   style))}))
                                                           line
                                                           width
                                                           counter?
                                                           total)]
        (render-fn! nil nil nil @a)  ; Make sure we draw the indicator at least once up front
        (add-watch a ::pd render-fn!)
        (try
          (f)
          (finally
            ; Teardown logic
            (remove-watch a ::pd)
            (if (:preserve opts)
              (println)
              (col1-and-erase-to-eol!))))))))

(defmacro animate!
  "Wraps the given forms in the determinate progress indicator. If the first form is the keyword `:opts`, the second form must be a map, optionally containing these keys:
    :frames     - the frames (a sequence of strings) to use for the determinate progress indicator (default is (:ascii-spinner styles))
    :delay      - the delay (in ms) between frames (default is 100ms)
    :fg-colour  - the foregound colour of the determinate progress indicator (default is :default) - see https://github.com/xsc/jansi-clj#colors for allowed values, and prefix with bright- to get the bright equivalent
    :bg-colour  - the background colour of the determinate progress indicator (default is :default) - see https://github.com/xsc/jansi-clj#colors for allowed values, and prefix with bright- to get the bright equivalent
    :attributes - the attributes of the determinate progress indicator (default is [:default]) - see https://github.com/xsc/jansi-clj#attributes for allowed values"
  [a & body]
  (if (= :opts (first body))
    `(animatef! ~a ~(second body) (fn [] ~@(rest (rest body))))
    `(animatef! ~a (fn [] ~@body))))
