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
  (:require [clojure.test :refer [deftest testing is]]
            [spinner.core :as    spin]))

(println "\n☔️ Running tests on Clojure" (clojure-version) "/ JVM" (System/getProperty "java.version") (str "(" (System/getProperty "java.vm.name") " v" (System/getProperty "java.vm.version") ")"))

(deftest spinner-tests
;  (testing "Creation"
;    (is (spin/create!)))

;  (testing "Start and stop"
;    (let [s (spin/create!)]
;      (is (= (do (spin/start! s) (spin/stop! s)) nil))))

  (testing "Display - default spinner"
    (let [s (spin/create!)]
      (is (= (do (spin/start! s) (Thread/sleep 1000) (spin/stop! s)) nil))))

  (testing "Display - custom colours"
    (let [s (spin/create! {:fg-colour :white :bg-colour :blue})]
      (is (= (do (spin/start! s) (Thread/sleep 1000) (spin/stop! s)) nil))))

  (testing "Display - custom styles"
    (doall
      (for [style (sort (keys spin/styles))]
        (do
          (print (str "\n" (name style) ": "))
          (flush)
          (let [s (spin/create! {:frames (style spin/styles)})]
            (is (= (do (spin/start! s) (Thread/sleep 2000) (spin/stop! s)) nil)))))))

;  (testing "Display - leading message"
;    (print "\nSome kind of long running processing happens here... ")
;    (flush)
;    (doall
;      (for [style (sort (keys spin/styles))]
;        (let [s (spin/create! {:frames (style spin/styles)})]
;          (is (= (do (spin/start! s) (Thread/sleep 1000) (spin/stop! s)) nil))))))
;
;  (testing "Display - print messages while a spinner is active"
;    (is (= (do
;             (print "\nReticulating splines... ")
;             (flush)
;             (let [s (spin/create-and-start! { :fg-colour :white :bg-colour :blue })]
;               (Thread/sleep 500)
;               (spin/print "\nInserting sublimated messages... ")
;               (Thread/sleep 500)
;               (spin/print "\nAttempting to lock back buffer... ")
;               (Thread/sleep 500)
;               (spin/print "\nTime-compressing simulator clock... ")
;               (Thread/sleep 500)
;               (spin/print "\nLecturing errant subsystems... ")
;               (Thread/sleep 500)
;               (spin/print "\nRetracting Phong shader... ")
;               (Thread/sleep 500)
;               (spin/stop! s)
;               (println)))
;           nil))))

)
