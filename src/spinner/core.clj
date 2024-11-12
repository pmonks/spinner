;
; Copyright © 2014 Peter Monks
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at https://mozilla.org/MPL/2.0/.
;
; SPDX-License-Identifier: MPL-2.0
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
