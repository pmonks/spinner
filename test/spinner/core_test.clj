;
; Copyright © 2014 Peter Monks
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at https://mozilla.org/MPL/2.0/.
;
; SPDX-License-Identifier: MPL-2.0
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
                             :delay      (/ spin/default-delay-ms 2)  ; Note: we use the old/deprecated :delay opt here, to ensure that fix-delay-opt is working correctly
                             :fg-colour  :bright-yellow
                             :bg-colour  :bright-red
                             :attributes [:bold :blink-fast]})
               (Thread/sleep 250)
               (spin/stop!))
           nil)))

  (testing "Printing messages while a spinner is active"
    (is (= (do
             (print "\nSomebody set up us the bomb.... ")
             (flush)
             (spin/start! {:fg-colour :bright-yellow :bg-colour :red :attribute :bold})
             (Thread/sleep 250)
             (spin/print "\nAll your base are belong to us... ")
             (Thread/sleep 250)
             (spin/stop!)
             (println))
           nil)))

  (testing "Function"
    (is (= (spin/spin! (fn [] (Thread/sleep 250))
                       {:frames     (:ascii-bouncing-ball spin/styles)
                        :delay      (* spin/default-delay-ms 2)  ; Note: we use the old/deprecated :delay opt here, to ensure that fix-delay-opt is working correctly
                        :fg-colour  :red
                        :bg-colour  :bright-white})
           nil))))
