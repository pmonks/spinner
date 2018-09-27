;
; Copyright © 2014 Peter Monks (pmonks@gmail.com)
;
; All rights reserved. This program and the accompanying materials
; are made available under the terms of the Eclipse Public License v2.0
; which accompanies this distribution, and is available at
; http://www.eclipse.org/legal/epl-v20.html
;
; Contributors:
;    Peter Monks - initial implementation

(ns spinner.core-test
  (:require [clojure.java.io :as    io]
            [midje.sweet     :refer :all]
            [spinner.core    :refer :all]))

(fact "Creation"
  (create!) => truthy)

(fact "Start and stop"
  (let [s (create!)]
    (do (start! s) (stop! s))) => nil?)

(fact "Display - default spinner"
  (let [s (create!)]
    (do (start! s) (Thread/sleep 1000) (stop! s))) => nil?)

(fact "Display - custom colours"
  (let [s (create! { :fg-colour :white :bg-colour :blue })]
    (do (start! s) (Thread/sleep 1000) (stop! s))) => nil?)

(fact "Display - custom styles"
  (doall
    (for [style (sort (keys styles))]
      (do
        (clojure.core/print (str "\n" (name style) ": "))
        (flush)
        (let [s (create! { :frames (style styles) })]
          (do (start! s) (Thread/sleep 2000) (stop! s)))) => nil?)))

(fact "Display - leading message"
  (clojure.core/print "\nSome kind of long running processing happens here... ")
  (flush)
  (doall
    (for [style (sort (keys styles))]
      (do
        (let [s (create! { :frames (style styles) })]
          (do (start! s) (Thread/sleep 1000) (stop! s)))) => nil?)))

(fact "Display - print messages while a spinner is active"
  (do
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
      (println))) => nil?)
