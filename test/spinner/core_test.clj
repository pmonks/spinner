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
  (:require [clojure.test   :refer [deftest testing is]]
            [jansi-clj.core :as jansi]
            [spinner.core   :as spin]))

(println "\n☔️ Running tests on Clojure" (clojure-version) "/ JVM" (System/getProperty "java.version") (str "(" (System/getProperty "java.vm.name") " v" (System/getProperty "java.vm.version") ")"))

(println
  (jansi/yellow-bg-bright
    (jansi/red
      (jansi/bold
        "\nDO NOT RUN THESE TESTS WITH THE `clj` COMMAND!  It uses rlwrap, which misinterprets the ANSI escape codes emitted by this library."))))

(deftest states
  (testing "Start and stop"
    (is (= (do (spin/start!) (spin/stop!))
           nil)))

  (testing "Start and stop repeatedly"
    (is (= (dotimes [_ 10] (spin/start!) (spin/stop!))
           nil)))

  (testing "Stop without start"
    (is (= (spin/stop!)
           nil)))

  (testing "Double start"
    (is (thrown? java.lang.IllegalStateException
                 (try (spin/start!) (spin/start!) (finally (spin/stop!))))))

  (testing "Not active when not running"
    (is (false? (spin/active?))))

  (testing "Active when running"
    (is (true? (try (spin/start!) (spin/active?) (finally (spin/stop!)))))))

(deftest display
  (testing "Default spinner for 5 seconds"
    (is (= (do (spin/start!) (Thread/sleep 5000) (spin/stop!))
           nil)))

  (testing "Multi-character spinner for 5 seconds"
    (is (= (do (spin/start! {:frames (:box-wave spin/styles)}) (Thread/sleep 5000) (spin/stop!))
           nil)))

  (testing "Spin around a function"
    (is (= (spin/spin! (fn [] (Thread/sleep 1000) :a-value))
        :a-value)))

  (testing "Custom colours"
    (is (= (do (spin/start! {:fg-colour :black :bg-colour :white}) (Thread/sleep 1000) (spin/stop!))
           nil)))

  (testing "Custom bright colours"
    (is (= (do (spin/start! {:fg-colour :bright-yellow :bg-colour :bright-red}) (Thread/sleep 1000) (spin/stop!))
           nil)))

  (testing "Custom attribute"
    (is (= (do (spin/start! {:attribute :strikethrough}) (Thread/sleep 1000) (spin/stop!))
           nil)))

  (testing "Custom attributes"
    (is (= (do (spin/start! {:attributes [:strikethrough :bold :underline]}) (Thread/sleep 1000) (spin/stop!))
           nil)))

  (testing "Custom everything"
    (is (= (do (spin/start! {:frames     (:box-fade spin/styles)
                             :fg-colour  :bright-yellow
                             :bg-colour  :bright-red
                             :attributes [:bold :fast-blink]})
               (Thread/sleep 2000)
               (spin/stop!))
           nil)))

  (testing "Custom styles with leading message"
    (doall
      (for [style (sort (keys spin/styles))]
        (do
          (print (str "\n" (name style) ": "))
          (flush)
          (is (= (do (spin/start! {:frames (style spin/styles)}) (Thread/sleep 1000) (spin/stop!))
                 nil))))))

  (testing "Printing messages while a spinner is active"
    (is (= (do
             (print "\nReticulating splines... ")
             (flush)
             (spin/start! {:fg-colour :bright-yellow :bg-colour :red :attribute :bold})
             (Thread/sleep 500)
             (spin/print "\nInserting sublimated messages... ")
             (Thread/sleep 500)
             (spin/print "\nAttempting to lock back buffer... ")
             (Thread/sleep 500)
             (spin/print "\nTime-compressing simulator clock... ")
             (Thread/sleep 500)
             (spin/print "\nLecturing errant subsystems... ")
             (Thread/sleep 500)
             (spin/print "\nRetracting Phong shader... ")
             (Thread/sleep 500)
             (spin/stop!)
             (println))
           nil))))
