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
            [jansi-clj.core       :as jansi]
            [progress.determinate :as pd]))

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
                  (pd/animate! a (apply + (map #(do (Thread/sleep 10) (swap! a inc) %) (range 100))))))))
  (testing "Basic options"
    (is (= 4950 (let [a (atom 0)]
                  (pd/animate! a :opts {:style (:ascii-boxes pd/styles)} (apply + (map #(do (Thread/sleep 10) (swap! a inc) %) (range 100))))))))
  )