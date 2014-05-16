;
; Copyright © 2014 Peter Monks (pmonks@gmail.com)
;
; All rights reserved. This program and the accompanying materials
; are made available under the terms of the Eclipse Public License v1.0
; which accompanies this distribution, and is available at
; http://www.eclipse.org/legal/epl-v10.html
;
; Contributors:
;    Peter Monks - initial implementation

(defproject org.clojars.pmonks/spinner "0.1.0"
  :description      "Simple text spinner for command line Clojure apps."
  :url              "https://github.com/pmonks/spinner"
  :license          {:name "Eclipse Public License"
                     :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [
                  [org.clojure/clojure "1.6.0"]
                  [jansi-clj           "0.1.0"]
                ]
  :profiles {:dev {:dependencies [
                                   [midje          "1.6.3"]
                                   [clj-ns-browser "1.3.1"]
                                 ]
                   :plugins      [[lein-midje "3.1.1"]]}   ; Don't remove this or travis-ci will assplode!
             :uberjar {:aot :all}}
  :uberjar-merge-with {#"META-INF/services/.*" [slurp str spit]}   ; Awaiting Leiningen 2.3.5 - see https://github.com/technomancy/leiningen/issues/1455
  )
