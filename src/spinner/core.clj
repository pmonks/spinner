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
  "Deprecated namespace from the original version of the library.  Only retained
  for backwards compatibility reasons. Superceded by the
  [[progress.indeterminate]] namespace."
  (:require [progress.indeterminate :as pi]
            [progress.util          :as u])
  (:refer-clojure :exclude [print]))

(defn- fix-delay-opt
  "'Fixes' the old `:delay` opt by replacing it with `:delay-in-ms`."
  [m]
  (when (and (contains? m :delay)
             (not (contains? m :delay-in-ms)))
    (assoc (dissoc m :delay) :delay-in-ms (:delay m))))

(def ^:deprecated is-windows?
  "See [[progress.util/is-windows?]]"
  u/is-windows?)

(def ^:deprecated default-style
  "See [[progress.indeterminate/default-style]]"
  pi/default-style)

(def ^:deprecated default-delay-ms
  "See [[progress.indeterminate/default-delay-ms]]"
  pi/default-delay-ms)

(def ^:deprecated styles
  "See [[progress.indeterminate/styles]]"
  pi/styles)

(defn ^:deprecated active?
  "See [[progress.indeterminate/active?]]"
  []
  (pi/active?))

(defn ^:deprecated start!
  "See [[progress.indeterminate/start!]]"
  ([] (start! nil))
  ([opts]
    (pi/start! (fix-delay-opt opts))))

(defn ^:deprecated stop!
  "See [[progress.indeterminate/stop!]]"
  []
  (pi/stop!))

(defn ^:deprecated spin!
  "See [[progress.indeterminate/animatef!]]"
  ([f] (spin! f nil))
  ([f opts]
    (pi/animatef! (fix-delay-opt opts) f)))

(defn ^:deprecated print
  "See [[progress.indeterminate/print]]"
  [& more]
  (apply pi/print more))
