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

(ns ^:deprecated spinner.core
  (:require [progress.indeterminate :as pi]
            [progress.util          :as u])
  (:refer-clojure :exclude [print]))

(def ^:deprecated is-windows?
  "See progress.util/is-windows?"
  u/is-windows?)

(def ^:deprecated default-style
  "See progress.indeterminate/default-style"
  pi/default-style)

(def ^:deprecated default-delay-ms
  "See progress.indeterminate/default-delay-ms"
  pi/default-delay-ms)

(def ^:deprecated styles
  "See progress.indeterminate/styles"
  pi/styles)

(defn ^:deprecated active?
  "See progress.indeterminate/active?"
  []
  (pi/active?))

(defn ^:deprecated start!
  "See progress.indeterminate/start!"
  ([] (start! nil))
  ([options]
    (pi/start! options)))

(defn ^:deprecated stop!
  "See progress.indeterminate/stop!"
  []
  (pi/stop!))

(defn ^:deprecated spin!
  "See progress.indeterminate/animatef!"
  ([f] (spin! f nil))
  ([f options]
    (pi/animatef! options f)))

(defn ^:deprecated print
  "See progress.indeterminate/print"
  [& more]
  (apply pi/print more))
