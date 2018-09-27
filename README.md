[![Build Status](https://travis-ci.com/pmonks/spinner.svg?branch=master)](https://travis-ci.com/pmonks/spinner)
[![Open Issues](https://img.shields.io/github/issues/pmonks/spinner.svg)](https://github.com/pmonks/spinner/issues)
[![License](https://img.shields.io/github/license/pmonks/spinner.svg)](https://github.com/pmonks/spinner/blob/master/LICENSE)
[![Dependencies Status](https://versions.deps.co/pmonks/spinner/status.svg)](https://versions.deps.co/pmonks/spinner)

# spinner

A simple text spinner for command line Clojure apps.

What is it useful for?

To give the user of a command line app a simple indeterminate progress indicator for long running operations.
Supports output of additional messages while a spinner is active.

Here it is in action (from the unit tests):
<p align="center">
  <img alt="spinner example screenshot" src="https://raw.githubusercontent.com/pmonks/spinner/master/spinner-demo.gif"/>
</p>

## Installation

spinner is available as a Maven artifact from [Clojars](https://clojars.org/org.clojars.pmonks/spinner).
Plonk the following in your project.clj :dependencies, substitute "#.#.#" for the latest version number,
`lein deps` and you should be good to go:

```clojure
[org.clojars.pmonks/spinner "#.#.#"]
```

The latest version is:

[![version](https://clojars.org/org.clojars.pmonks/spinner/latest-version.svg)](https://clojars.org/org.clojars.pmonks/spinner)

## Usage

The spinner functionality is provided by the `spinner.core` namespace.

Require it in the REPL:

```clojure
(require '[spinner.core :as spin] :reload-all)
```

Require it in your application:

```clojure
(ns my-app.core
  (:require [spinner.core :as spin]))
```

**Important Note:** your REPL must be run in a trampoline (`lein trampoline repl`) in order for ANSI escape sequences to be available.

[The API documentation](https://pmonks.github.io/spinner/) has full details on the functionality provided by the library, and [the unit tests](https://github.com/pmonks/spinner/blob/master/test/spinner/core_test.clj) have several examples of usage.

## Tested Versions

spinner is [tested on](https://travis-ci.com/pmonks/spinner):

|                | JVM v1.6         | JVM v1.7       | JVM v1.8        | JVM v9         | JVM v10        | JVM v11          |
|           ---: |  :---:           |  :---:         |  :---:          |  :---:         |  :---:         |  :---:           |
| Clojure 1.6.0  | ❌<sup>1,2</sup> | ❌<sup>2</sup> | ❌<sup>2</sup> | ❌<sup>2</sup> | ❌<sup>2</sup> | ❌<sup>2,3</sup> |
| Clojure 1.7.0  | ❌<sup>1</sup>   | ✅             | ✅             | ✅             | ✅             | ✅<sup>3</sup>   |
| Clojure 1.8.0  | ❌<sup>1</sup>   | ✅             | ✅             | ✅             | ✅             | ✅<sup>3</sup>   |
| Clojure 1.9.0  | ❌<sup>1</sup>   | ✅             | ✅             | ✅             | ✅             | ✅<sup>3</sup>   |
| Clojure 1.10.0 | ❌<sup>1</sup>   | ❌<sup>4</sup> | ✅             | ✅             | ✅             | ✅<sup>3</sup>   |

<sup>1</sup> Leiningen v2.8 only supports JVM v1.7 and up

<sup>2</sup> Midje (used for unit testing) only supports Clojure v1.7 and up

<sup>3</sup> Clojure doesn't yet support JVM v11 (see [CLJ-2374](https://dev.clojure.org/jira/browse/CLJ-2374)) - automated unit testing on JVM v11 is configured, but this configuration is currently failing

<sup>4</sup> Clojure v1.10 only supports JVM v1.8 and up

## Developer Information

[GitHub project](https://github.com/pmonks/spinner)

[Bug Tracker](https://github.com/pmonks/spinner/issues)

## License

Copyright © 2014 Peter Monks (pmonks@gmail.com)

Distributed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v20.html) either version 2.0 or (at your option) any later version.
