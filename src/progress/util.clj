;
; Copyright © 2022 Peter Monks
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at https://mozilla.org/MPL/2.0/.
;
; SPDX-License-Identifier: MPL-2.0
;

(ns progress.util
  (:require [clojure.string :as s]))

(def is-windows?
  "Are we running on Windows?  If so, best to stick with ASCII-only progress
  indicators...  😢"
  (s/starts-with? (s/lower-case (System/getProperty "os.name")) "windows"))
