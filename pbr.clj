;
; Copyright © 2021 Peter Monks
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at https://mozilla.org/MPL/2.0/.
;
; SPDX-License-Identifier: MPL-2.0
;

#_{:clj-kondo/ignore [:unresolved-namespace]}
(defn set-opts
  [opts]
  (assoc opts
         :lib          'com.github.pmonks/spinner
         :version      (pbr/calculate-version 2 0)
         :prod-branch  "release"
         :write-pom    true
         :validate-pom true
         :pom          {:description      "Simple ANSI text progress indicators for command line Clojure apps."
                        :url              "https://github.com/pmonks/spinner"
                        :licenses         [:license   {:name "MPL-2.0" :url "https://www.mozilla.org/en-US/MPL/2.0/"}]
                        :developers       [:developer {:id "pmonks" :name "Peter Monks" :email "pmonks+spinner@gmail.com"}]
                        :scm              {:url "https://github.com/pmonks/spinner" :connection "scm:git:git://github.com/pmonks/spinner.git" :developer-connection "scm:git:ssh://git@github.com/pmonks/spinner.git"}
                        :issue-management {:system "github" :url "https://github.com/pmonks/spinner/issues"}}
         :codox        {:namespaces ['progress.determinate 'progress.indeterminate 'progress.util 'spinner.core]
                        :metadata   {:doc/format :markdown}}))
