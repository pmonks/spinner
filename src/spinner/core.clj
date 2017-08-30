;
; Copyright © 2014-2017 Peter Monks (pmonks@gmail.com)
;
; All rights reserved. This program and the accompanying materials
; are made available under the terms of the Eclipse Public License v1.0
; which accompanies this distribution, and is available at
; http://www.eclipse.org/legal/epl-v10.html
;
; Contributors:
;    Peter Monks - initial implementation

(ns spinner.core
  (:require [clojure.string :as s]
            [jansi-clj.core :as jansi])
  (:refer-clojure :exclude [print]))

(def ^:private os-name (System/getProperty "os.name"))

(def is-windows?
  "Are we running on Windows?"
  (.startsWith (.toLowerCase ^String os-name) "windows"))

(def default-style :ascii-spinner)

(def styles
  "A selection of predefined styles of spinner. Only ASCII spinners are known to work reliably -
   other styles depend on the operating system, terminal encoding, phase of the moon, and
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
    :line-quadrants      ["┤" "┘" "┴" "└" "├" "┌" "┬" "┐"]
    :line-up-down        ["☱" "☲" "☴" "☲"]
    :dot-spinner         [\⋮ \⋰ \⋯ \⋱]
    :dot-waving          ["⢄" "⢂" "⢁" "⡁" "⡈" "⡐" "⡠" "⡐"  "⡈" "⡁" "⢁" "⢂"]
    :dot-around          ["⢹" "⢺" "⢼" "⣸" "⣇" "⡧" "⡗" "⡏"]
    :dot-gap-around      ["⣾" "⣽" "⣻" "⢿" "⡿" "⣟" "⣯" "⣷"]
    :arrows              [\← \↖ \↑ \↗ \→ \↘ \↓ \↙]
    :circle-halves       ["◐" "◓" "◑" "◒"]
    :circle-quadrants    ["◴" "◷" "◶" "◵"]
    :circle-up-down      ["◡" "⊙" "◠" "⊙"]
    :square-quadrants    ["◰" "◳" "◲" "◱"]
    :square-shrink       ["■" "□" "▪" "▫"]
    :triangle-around     ["◢" "◣" "◤" "◥"]
    :braille             ["⠋" "⠙" "⠹" "⠸" "⠼" "⠴" "⠦" "⠧" "⠇" "⠏"]
    :pointing-fingers    ["👆" "👉" "👇" "👈"]
    :clocks              ["🕐" "🕑" "🕒" "🕓" "🕔" "🕕" "🕖" "🕗" "🕘" "🕙" "🕚" "🕛"]
    :earth-spinning      ["🌍" "🌎" "🌏"]
    :moon-phases         ["🌑" "🌒" "🌓" "🌔" "🌕" "🌖" "🌗" "🌘"]
  })

(defn- select-values
  "Solution 3 from http://blog.jayfields.com/2011/01/clojure-select-keys-select-values-and.html"
  [map ks]
  (remove nil? (reduce #(conj %1 (map %2)) [] ks)))

(defn- select-value-default
  "Selects the first value of ks in map, with default-value if none of ks were found."
  [map ks default-value]
  (let [value (first (select-values map ks))]
    (if (nil? value)
      default-value
      value)))

(def ^:private console-charset (java.nio.charset.Charset/forName
                                 (.getEncoding (java.io.OutputStreamWriter. System/out))))

(defn- byte-length-in-charset
  "Returns the byte length of s, in charset cs."
  [^String s ^java.nio.charset.Charset cs]
  (count (.getBytes s cs)))

(defn- console-byte-length
  "Returns the byte length of s, in the charset of the console."
  [^String s]
  (byte-length-in-charset s console-charset))

(def ^:private pending-messages (atom ""))

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
  (let [messages (first (swap*! pending-messages (constantly "")))]
    (clojure.core/print messages)))

(defn- spinner
  ([] (spinner nil))
  ([options]
    (let [options     (if (nil? options) {} options)
          delay-in-ms (:delay options 100)
          frames      (:frames options (default-style styles))
          fg-colour   (select-value-default options [:fg-colour :fg-color] :default)
          bg-colour   (select-value-default options [:bg-colour :bg-color] :default)
          attribute   (:attribute options :default)]
      (try
        (loop [i (int 0)]
          (clojure.core/print (str (jansi/a  attribute
                                   (jansi/bg bg-colour
                                   (jansi/fg fg-colour (nth frames i))))
                                   " "))
          (flush)
          (Thread/sleep delay-in-ms)
          (clojure.core/print (str (jansi/cursor-left 2)
                                   (jansi/erase-line)))
          (print-pending-messages)
          (flush)
          (recur (int (mod (inc i) (count frames)))))
        (catch InterruptedException ie
          (comment "Swallow the exception silently and terminate."))
        (finally
          (comment "But remember to erase the last frame.")
          (clojure.core/print (str (jansi/cursor-left 2)
                                   (jansi/erase-line)))
          (print-pending-messages)
          (flush)))
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
  (if (active? spinner)
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
  (if (not (active? spinner))
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
   * no newlines are inserted - if message(s) are to appear on new lines the caller needs to include \newline in the value(s)"
  [& more]
  (swap! pending-messages str (s/join \space more))
  nil)
