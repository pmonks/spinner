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

(ns progress.determinate-test
  (:require [clojure.test         :refer [deftest testing is]]
            [wcwidth.api          :as w]
            [progress.ansi        :as ansi]
            [progress.determinate :as pd]))

(defn slow-counter
  "Counts from 0 to steps-1 in approximately time-to-take milliseconds, updating atom a with the current count as it goes. Returns the sum of the series."
  [a time-to-take steps]
  (let [sleep-time (Math/round (double (/ time-to-take steps)))]
    (reduce + (map #(do (Thread/sleep sleep-time) (swap! a inc) %) (range steps)))))

(defn slow-counter-to-100
  "Counts from 0 to 99 in aooroximately time-to-take milliseconds, updating atom a as it goes. Returns the sum of the series (4950)."
  [a time-to-take]
  (slow-counter a time-to-take 100))

(defn slow-counter-to-10-in-250
  "Counts from 0 to 9 in 250ms, updating atom a as it goes. Returns the sum of the series (49)."
  [a]
  (slow-counter a 250 10))

(defn slow-counter-to-100-in-1000
  "Counts from 0 to 99 in 1000ms, updating atom a as it goes. Returns the sum of the series (4950)."
  [a]
  (slow-counter-to-100 a 1000))

(deftest test-function-vs-macro
  (testing "No atom or code provided - animatef! fn"
    (is (= nil (pd/animatef! nil nil))))

  (testing "No atom code provided - animate! macro"
    (is (= nil (pd/animate! nil))))

  (testing "Default for 0.5 second - animatef! fn"
    (is (= nil (pd/animatef! (atom 0) (fn [] (Thread/sleep 500))))))

  (testing "Default for 1 second - animate! macro"
    (is (= nil (pd/animate! (atom 0) (Thread/sleep 500))))))

(deftest test-results
  (testing "Animate around a value"
    (is (= :a-value (pd/animate! (atom 0) :a-value))))

  (testing "Animate around a function"
    (is (= 4 (pd/animate! (atom 0) (* 2 2))))))

(deftest test-updates
  (testing "Animate around some steps"
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a (slow-counter-to-100-in-1000 a))))))
  (testing "Jump steps"
    (is (= :foo (let [a (atom 0)]
                  (pd/animate! a (doall (for [x [25 50 75 100]] (do (Thread/sleep 250) (swap! a (constantly x))))) :foo))))))

(deftest test-computation-results
  (testing "Computation results"
    (is (= 45     (let [a (atom 0)]
                    (pd/animate! a :opts {:total 10} (slow-counter-to-10-in-250 a)))))
    (is (= 9/2    (let [a (atom 0)]
                    (pd/animate! a :opts {:total 1.0} (reduce + (map #(do (Thread/sleep 25) (swap! a inc) (/ % 10)) (range 10)))))))
    (is (= 499500 (let [a (atom 0)]
                    (pd/animate! a :opts {:total 1000} (slow-counter a 250 1000)))))))

(deftest test-option-style
  (testing "Style with zero width characters"
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"(?i).*invalid\s+width.*"
                          (let [a (atom 0)]
                                     (pd/animate! a
                                                  :opts {:style {:empty (w/code-point-to-string 0x20DD)}}  ; Combining enclosing circle (zero width)
                                                  :foo))))
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"(?i).*invalid\s+width.*"
                          (let [a (atom 0)]
                                     (pd/animate! a
                                                  :opts {:style {:tip (w/code-point-to-string 0x001B)}}  ; ANSI ESC (non-printing)
                                                  :foo)))))
  ; These ones run for longer (1 second each) so that they can be visually verified
  (testing "Built-in style - ASCII"
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a :opts {:style (:ascii-boxes pd/styles)} (slow-counter-to-100-in-1000 a))))))
  (testing "Built-in style - double width characters"
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a
                               :opts {:style (:emoji-circles pd/styles)}
                               (slow-counter-to-100-in-1000 a)))))
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a
                               :opts {:style (:emoji-boxes pd/styles)}
                               (slow-counter-to-100-in-1000 a))))))
  (testing "Custom style - ASCII with colours and attributes"
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a
                               :opts {:style {:left            ">"
                                              :left-bg-colour  :bright-red
                                              :left-fg-colour  :bright-yellow
                                              :left-attrs      [:strikethrough]
                                              :right           "<"
                                              :right-bg-colour :bright-red
                                              :right-fg-colour :bright-yellow
                                              :right-attrs      [:strikethrough]
                                              :empty           "."
                                              :empty-attrs     [:underline]
                                              :full            "*"
                                              :full-bg-colour  :bright-yellow
                                              :full-fg-colour  :black
                                              :full-attrs      [:bold]
                                              :tip             "@"
                                              :tip-bg-colour   :black
                                              :tip-fg-colour   :bright-yellow
                                              :tip-attrs       [:italic]}}
                               (slow-counter-to-100-in-1000 a))))))
  (testing "Custom style - double width characters"
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a
                               :opts {:style {:left            "🌜"
                                              :right           "🌛"
                                              :empty           "🫥"  ; Note - doesn't work properly on ITerm2 due to https://gitlab.com/gnachman/iterm2/-/issues/10509
                                              :full            "😐"
                                              :tip             "🤔"}}
                               (slow-counter-to-100-in-1000 a))))))
  (testing "Custom style - mixed width characters"
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a
                               :opts {:style {:left            "["
                                              :right           "]"
                                              :empty           " "
                                              :full            "🔀"
                                              :tip             ">"}}
                               (slow-counter-to-100-in-1000 a))))))
  (testing "Custom style - label"
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a
                               :opts {:label "download.zip"}
                               (slow-counter-to-100-in-1000 a)))))))

(deftest test-option-counter
  (testing "Options - no counter"
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a :opts {:counter? false} (slow-counter-to-100-in-1000 a)))))))

(deftest test-option-units
  (testing "Options - units"
    (is (= 74305 (let [a (atom 0)]
                   (pd/animate! a :opts {:units "MB" :total 386} (slow-counter a 750 386)))))))

(deftest test-option-preserve
  (testing "Options - preserve"
    (is (= 45   (let [a (atom 0)]
                  (pd/animate! a :opts {:total 10 :preserve? true} (slow-counter-to-10-in-250 a)))))))

(deftest test-option-width
  (testing "Options - custom width"
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a
                               :opts {:width 40}
                               (slow-counter-to-100-in-1000 a)))))
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a
                               :opts {:width 20}
                               (slow-counter-to-100-in-1000 a)))))))

(deftest test-option-line
  (testing "Options - custom line location"
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a
                               :opts {:line 10}
                               (slow-counter-to-100-in-1000 a)))))
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a
                               :opts {:line 1}
                               (slow-counter-to-100-in-1000 a)))))))

(deftest test-option-combos
  (testing "Option combos - line with concurrent text output"
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a
                               :opts {:line 2}
                               (reduce + (map #(do
                                                 (Thread/sleep 10)
                                                 (swap! a inc)
                                                 (ansi/print-at 1 1 "Now up to" %)
                                                 %)
                                              (range 100))))))))
  (testing "Option combos - line and preserve"
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a
                               :opts {:line 1 :preserve? true}
                               (slow-counter-to-100-in-1000 a)))))))

(defn async-indicator-at-line
  "Asynchronously starts an indicator at line line, running the logic in fn f, a function of one argument (the atom to update)."
  [line f]
  (let [a (atom 0)]
    (future (pd/animate! a :opts {:line line} (f a)))))

(deftest test-multiple-indicators
  (testing "Multiple progress indicators"
    (is (= [4950 4950 4950 4950]
           (map deref
                (doall (map #(async-indicator-at-line (inc %) (fn [a] (slow-counter-to-100 a (+ 500 (* 100 (rand-int 6))))))
                            (range 4))))))))
