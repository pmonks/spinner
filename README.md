[![Build Status](https://travis-ci.org/pmonks/spinner.svg?branch=master)](https://travis-ci.org/pmonks/spinner)
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

## Developer Information

[GitHub project](https://github.com/pmonks/spinner)

[Bug Tracker](https://github.com/pmonks/spinner/issues)

## License

Copyright © 2014 Peter Monks (pmonks@gmail.com)

Distributed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) either version 1.0 or (at your option) any later version.
