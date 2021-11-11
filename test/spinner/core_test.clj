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

(ns spinner.core-test
  (:require [clojure.java.io :as    io]
            [clojure.test    :refer :all]
            [spinner.core    :refer :all]))

(println "\n☔️ Running tests on Clojure" (clojure-version) "/ JVM" (System/getProperty "java.version") (str "(" (System/getProperty "java.vm.name") " v" (System/getProperty "java.vm.version") ")"))

(deftest spinner-tests
  (testing "Creation"
    (is (create!)))

  (testing "Start and stop"
    (let [s (create!)]
      (is (= (do (start! s) (stop! s)) nil))))

  (testing "Display - default spinner"
    (let [s (create!)]
      (is (= (do (start! s) (Thread/sleep 1000) (stop! s)) nil))))

  (testing "Display - custom colours"
    (let [s (create! { :fg-colour :white :bg-colour :blue })]
      (is (= (do (start! s) (Thread/sleep 1000) (stop! s)) nil))))

  (testing "Display - custom styles"
    (doall
      (for [style (sort (keys styles))]
        (do
          (clojure.core/print (str "\n" (name style) ": "))
          (flush)
          (let [s (create! { :frames (style styles) })]
            (is (= (do (start! s) (Thread/sleep 2000) (stop! s)) nil)))))))

  (testing "Display - leading message"
    (clojure.core/print "\nSome kind of long running processing happens here... ")
    (flush)
    (doall
      (for [style (sort (keys styles))]
        (do
          (let [s (create! { :frames (style styles) })]
            (is (= (do (start! s) (Thread/sleep 1000) (stop! s)) nil)))))))

  (testing "Display - print messages while a spinner is active"
    (is (= (do
             (print "\nReticulating splines... ")
             (flush)
             (let [s (create-and-start! { :fg-colour :white :bg-colour :blue })]
               (Thread/sleep 500)
               (print "\nInserting sublimated messages... ")
               (Thread/sleep 500)
               (print "\nAttempting to lock back buffer... ")
               (Thread/sleep 500)
               (print "\nTime-compressing simulator clock... ")
               (Thread/sleep 500)
               (print "\nLecturing errant subsystems... ")
               (Thread/sleep 500)
               (print "\nRetracting Phong shader... ")
               (Thread/sleep 500)
               (stop! s)
               (println)))
           nil))))
