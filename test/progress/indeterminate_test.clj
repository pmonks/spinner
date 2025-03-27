;
; Copyright © 2022 Peter Monks
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at https://mozilla.org/MPL/2.0/.
;
; SPDX-License-Identifier: MPL-2.0
;

(ns progress.indeterminate-test
  (:require [clojure.test           :refer [deftest testing is]]
            [jansi-clj.core         :as jansi]
            [progress.test-utils    :refer [skip-slow-tests?]]
            [progress.indeterminate :as pi]))

(jansi/erase-screen!)
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

  (testing "Default for 1/4 second - animatef! fn"
    (is (= nil (pi/animatef! (fn [] (Thread/sleep 250))))))

  (testing "Default for 1/4 second - animate! macro"
    (is (= nil (pi/animate! (Thread/sleep 250))))))

(deftest test-results
  (testing "Animate around a value"
    (is (= :a-value (pi/animate! (Thread/sleep 250) :a-value))))

  (testing "Animate around a function"
    (is (= 4 (pi/animate! (Thread/sleep 250) (* 2 2))))))

(deftest test-options
  (when-not skip-slow-tests?  ; Because GitHub Actions are hot garbage
    (testing "Non-default animation for 1 second - animatef! fn"
      (is (= nil (pi/animatef! {:frames (:ascii-bouncing-ball pi/styles)} (fn [] (Thread/sleep 250))))))

    (testing "Non-default animation for 1 second - animate! macro"
      (is (= nil (pi/animate! :opts {:frames (:ascii-bouncing-ball pi/styles)} (Thread/sleep 250)))))

    (testing "Custom colours"
      (is (= nil (pi/animate! :opts {:fg-colour :black :bg-colour :white} (Thread/sleep 250)))))

    (testing "Custom bright colours"
      (is (= nil (pi/animate! :opts {:fg-colour :bright-yellow :bg-colour :bright-red}))))

    (testing "Custom attribute"
      (is (= nil (pi/animate! :opts {:attributes [:strikethrough]} (Thread/sleep 250)))))

    (testing "Custom attributes"
      (is (= nil (pi/animate! :opts {:attributes [:strikethrough :bold :underline]} (Thread/sleep 250)))))

    (testing "Custom everything"
      (is (= nil (pi/animate! :opts {:frames     (:box-fade pi/styles)
                                    :delay-in-ms (/ pi/default-delay-ms 4)  ; Hyperspeed!
                                    :fg-colour   :bright-yellow
                                    :bg-colour   :bright-red
                                    :attributes  [:bold :blink-fast]}
                   (Thread/sleep 1000)))))

    (testing "All styles with leading message"
      (doall
        (for [style (sort (keys pi/styles))]
          (do
            (print (str "\n" (name style) ": "))
            (flush)
            (is (= nil (pi/animate! :opts {:frames (style pi/styles)} (Thread/sleep 250)))))))))

  (testing "Printing messages while an animation is active"
    (is (= nil (do
                 (print "\nReticulating splines... ")
                 (pi/animate! :opts {:fg-colour :bright-yellow :bg-colour :red :attribute :bold}
                   (Thread/sleep 250)
                   (pi/print "\nInserting sublimated messages... ")
                   (Thread/sleep 250)
                   (pi/print "\nAttempting to lock back buffer... ")
                   (Thread/sleep 250)
                   (pi/print "\nTime-compressing simulator clock... ")
                   (Thread/sleep 250)
                   (pi/print "\nLecturing errant subsystems... ")
                   (Thread/sleep 250)
                   (pi/print "\nRetracting Phong shader... ")
                   (Thread/sleep 250))
                 (println))))))
