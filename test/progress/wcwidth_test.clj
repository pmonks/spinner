;
; Copyright © 2022 Peter Monks
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

(ns progress.wcwidth-test
  (:require [clojure.test     :refer [deftest testing is]]
            [progress.wcwidth :as wcw]))

(deftest test-wcwidth
  (testing "ASCII codes"
    (is (zero?  (wcw/wcwidth 0x0000)))    ; NUL
    (is (= -1   (wcw/wcwidth 0x007F)))    ; DEL
    (is (=  1   (wcw/wcwidth 0x0020)))    ; space
    (is (=  1   (wcw/wcwidth 0x0023)))    ; #
    (is (=  1   (wcw/wcwidth 0x0036)))    ; 6
    (is (=  1   (wcw/wcwidth 0x0041)))    ; A
    (is (=  1   (wcw/wcwidth 0x2588)))    ; █
    (is (= '(1) (distinct (map wcw/wcwidth (range 0x21 0x7E))))))   ; All of the printable ASCII characters

  (testing "ASCII from Clojure character literals"
    (is (=  1 (wcw/wcwidth (int \space))))
    (is (=  1 (wcw/wcwidth (int \#))))
    (is (=  1 (wcw/wcwidth (int \6))))
    (is (=  1 (wcw/wcwidth (int \A)))))

  (testing "Unicode (single width")

  (testing "Unicode (zero width)"
    (is (zero? (wcw/wcwidth 0x0F35)))   ; Very close to middle of combinging characters
    (is (zero? (wcw/wcwidth 0x0F37)))   ; Very close to middle of combinging characters
    (is (zero? (wcw/wcwidth 0x0311)))   ; Lowest block of combining characters
    (is (zero? (wcw/wcwidth 0xE0100)))  ; Highest block of combining characters
    (is (zero? (wcw/wcwidth 0x0B01)))   ; Random entries in combining characters from here on
    (is (zero? (wcw/wcwidth 0x1DC4)))
    (is (zero? (wcw/wcwidth 0x0AE2)))
    (is (zero? (wcw/wcwidth 0x0AE3)))
    (is (zero? (wcw/wcwidth 0xA825)))
    (is (zero? (wcw/wcwidth 0xA826))))



  (testing "Unicode (negative width)")

  (testing "Unicode (double width)"))
