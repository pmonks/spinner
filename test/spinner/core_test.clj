;
; Copyright © 2016 Peter Monks (pmonks@gmail.com)
;
; All rights reserved. This program and the accompanying materials
; are made available under the terms of the Eclipse Public License v1.0
; which accompanies this distribution, and is available at
; http://www.eclipse.org/legal/epl-v10.html
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

(fact "Display"
  (let [s (create!)]
    (do (start! s) (Thread/sleep 1000) (stop! s))) => nil?)

(fact "Custom colours"
  (let [s (create! { :fg-colour :white :bg-colour :blue })]
    (do (start! s) (Thread/sleep 1000) (stop! s))) => nil?)

(fact "Custom styles"
  (let [s (create! { :frames (:dot-spinner styles) })]
    (do (start! s) (Thread/sleep 1000) (stop! s))) => nil?

  (let [s (create! { :frames (:up-and-down styles) })]
    (do (start! s) (Thread/sleep 1000) (stop! s))) => nil?

  (let [s (create! { :frames (:fade-in-and-out styles) })]
    (do (start! s) (Thread/sleep 1000) (stop! s))) => nil?

  (let [s (create! { :frames (:side-to-side styles) })]
    (do (start! s) (Thread/sleep 1000) (stop! s))) => nil?

  (let [s (create! { :frames (:quadrants styles) })]
    (do (start! s) (Thread/sleep 1000) (stop! s))) => nil?

  (let [s (create! { :frames (:arrows styles) })]
    (do (start! s) (Thread/sleep 1000) (stop! s))) => nil?

  (let [s (create! { :frames (:pointing-fingers styles) })]
    (do (start! s) (Thread/sleep 1000) (stop! s))) => nil?)

(fact "Print messages"
  (do
    (print "Reticulating splines... ")
    (flush)
    (let [s (create-and-start!)]
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
      (println))) => nil?

  (do
    (print "Reticulating splines... ")
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
      (println))) => nil?

  (do
    (print "Reticulating splines... ")
    (flush)
    (let [s (create-and-start! { :frames (:arrows styles) })]
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
