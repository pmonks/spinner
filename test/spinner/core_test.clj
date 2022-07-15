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

(ns ^:deprecated spinner.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [spinner.core :as spin]))

; These tests only exists to check backwards compatibility of the (deprecated) spinner.core namespace

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
  (testing "Custom everything"
    (is (= (do (spin/start! {:frames     (:box-fade spin/styles)
                             :delay      (/ spin/default-delay-ms 2)
                             :fg-colour  :bright-yellow
                             :bg-colour  :bright-red
                             :attributes [:bold :blink-fast]})
               (Thread/sleep 2000)
               (spin/stop!))
           nil)))

  (testing "Printing messages while a spinner is active"
    (is (= (do
             (print "\nSomebody set up us the bomb.... ")
             (flush)
             (spin/start! {:fg-colour :bright-yellow :bg-colour :red :attribute :bold})
             (Thread/sleep 500)
             (spin/print "\nAll your base are belong to us... ")
             (Thread/sleep 500)
             (spin/stop!)
             (println))
           nil)))

  (testing "Function"
    (is (= (spin/spin! (fn [] (Thread/sleep 2000))
                       {:frames     (:ascii-bouncing-ball spin/styles)
                        :delay      (* spin/default-delay-ms 2)
                        :fg-colour  :red
                        :bg-colour  :bright-white})
           nil))))
