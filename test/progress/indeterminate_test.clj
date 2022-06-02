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

(ns progress.indeterminate-test
  (:require [clojure.test           :refer [deftest testing is]]
            [jansi-clj.core         :as jansi]
            [progress.indeterminate :as pi]))

(println "\n☔️ Running tests on Clojure" (clojure-version) "/ JVM" (System/getProperty "java.version") (str "(" (System/getProperty "java.vm.name") " v" (System/getProperty "java.vm.version") ")"))

(println
  (jansi/yellow-bg-bright
    (jansi/red
      (jansi/bold
        "\nDO NOT RUN THESE TESTS WITH THE `clj` COMMAND!  It uses rlwrap, which misinterprets the ANSI escape sequences emitted by this library."))))

(deftest test-states
  (testing "Not active when not running"
    (is (false? (pi/active?))))

  (testing "Active when running"
    (is (true? (pi/animatef! (fn [] (pi/active?)))))))

(deftest test-function-vs-macro
  (testing "No code provided - animatef! fn"
    (is (= nil (pi/animatef! nil))))

  (testing "No code provided - animate! macro"
    (is (= nil (pi/animate!))))

  (testing "Default for 1 second - animatef! fn"
    (is (= nil (pi/animatef! (fn [] (Thread/sleep 1000))))))

  (testing "Default for 1 second - animate! macro"
    (is (= nil (pi/animate! (Thread/sleep 1000))))))

(deftest test-results
  (testing "Animate around a value"
    (is (= :a-value (pi/animate! (Thread/sleep 500) :a-value))))

  (testing "Animate around a function"
    (is (= 4 (pi/animate! (Thread/sleep 500) (* 2 2))))))

(deftest test-options
  (testing "Non-default animation for 1 second - animatef! fn"
    (is (= nil (pi/animatef! {:frames (:ascii-bouncing-ball pi/styles)} (fn [] (Thread/sleep 1000))))))

  (testing "Non-default animation for 1 second - animate! macro"
    (is (= nil (pi/animate! :opts {:frames (:ascii-bouncing-ball pi/styles)} (Thread/sleep 1000)))))

  (testing "Custom colours"
    (is (= nil (pi/animate! :opts {:fg-colour :black :bg-colour :white} (Thread/sleep 1000)))))

  (testing "Custom bright colours"
    (is (= nil (pi/animate! :opts {:fg-colour :bright-yellow :bg-colour :bright-red}))))

  (testing "Custom attribute"
    (is (= nil (pi/animate! :opts {:attributes [:strikethrough]} (Thread/sleep 1000)))))

  (testing "Custom attributes"
    (is (= nil (pi/animate! :opts {:attributes [:strikethrough :bold :underline]} (Thread/sleep 1000)))))

  (testing "Custom everything"
    (is (= nil (pi/animate! :opts {:frames     (:box-fade pi/styles)
                                  :delay      (/ pi/default-delay-ms 2)
                                  :fg-colour  :bright-yellow
                                  :bg-colour  :bright-red
                                  :attributes [:bold :fast-blink]}
                 (Thread/sleep 2000)))))

  (testing "All styles with leading message"
    (doall
      (for [style (sort (keys pi/styles))]
        (do
          (print (str "\n" (name style) ": "))
          (flush)
          (is (= nil (pi/animate! :opts {:frames (style pi/styles)} (Thread/sleep 1000))))))))

  (testing "Printing messages while an animation is active"
    (is (= nil (do
                 (print "\nReticulating splines... ")
                 (pi/animate! :opts {:fg-colour :bright-yellow :bg-colour :red :attribute :bold}
                   (Thread/sleep 500)
                   (pi/print "\nInserting sublimated messages... ")
                   (Thread/sleep 500)
                   (pi/print "\nAttempting to lock back buffer... ")
                   (Thread/sleep 500)
                   (pi/print "\nTime-compressing simulator clock... ")
                   (Thread/sleep 500)
                   (pi/print "\nLecturing errant subsystems... ")
                   (Thread/sleep 500)
                   (pi/print "\nRetracting Phong shader... ")
                   (Thread/sleep 500))
                 (println))))))
