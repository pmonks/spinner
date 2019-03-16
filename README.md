[![Build Status](https://travis-ci.com/clj-commons/spinner.svg?branch=master)](https://travis-ci.com/clj-commons/spinner)
[![Open Issues](https://img.shields.io/github/issues/clj-commons/spinner.svg)](https://github.com/clj-commons/spinner/issues)
[![License](https://img.shields.io/github/license/clj-commons/spinner.svg)](https://github.com/clj-commons/spinner/blob/master/LICENSE)
[![Dependencies Status](https://versions.deps.co/clj-commons/spinner/status.svg)](https://versions.deps.co/clj-commons/spinner)

# spinner

A simple text spinner for command line Clojure apps.

What is it useful for?

To give the user of a command line app a simple indeterminate progress indicator for long running operations.
Supports output of additional messages while a spinner is active.

Here it is in action (from the unit tests):
<p align="center">
  <img alt="spinner example screenshot" src="https://raw.githubusercontent.com/clj-commons/spinner/master/spinner-demo.gif"/>
</p>

As you can see, using Unicode characters in spinners may be unreliable, depending on your OS, terminal, font, encoding, phase of the moon, etc.

## Installation

spinner is available as a Maven artifact from [Clojars](https://clojars.org/clj-commons/spinner).  The latest version is:

[![version](https://clojars.org/clj-commons/spinner/latest-version.svg)](https://clojars.org/clj-commons/spinner)

### Trying it Out
If you prefer to kick the library's tyres without creating a project, you can use the [`lein try` plugin](https://github.com/rkneufeld/lein-try):

```shell
$ lein trampoline try clj-commons/spinner   # See note below regarding use of 'lein trampoline'
```

or (as of v0.5.0), if you have installed the [Clojure CLI tools](https://clojure.org/guides/getting_started#_clojure_installer_and_cli_tools):

```shell
$ clj -Sdeps '{:deps {clj-commons/spinner {:mvn/version "#.#.#"}}}'  # Where #.#.# is replaced with an actual version number >= 0.5.0
```

Either way, you will be dropped in a REPL with the library downloaded and ready for use.

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

**Important Note:** if you're using leiningen, your REPL must be run in a trampoline (`lein trampoline repl`) in order for ANSI escape sequences to be available.

[The API documentation](https://clj-commons.github.io/spinner/) has full details on the functionality provided by the library, and [the unit tests](https://github.com/clj-commons/spinner/blob/master/test/spinner/core_test.clj) have several examples of usage.

## Tested Versions

spinner is [tested on](https://travis-ci.com/clj-commons/spinner):

|                | JVM v1.6         | JVM v1.7       | JVM v1.8        | JVM v9         | JVM v10        | JVM v11         |
|           ---: |  :---:           |  :---:         |  :---:          |  :---:         |  :---:         |  :---:          |
| Clojure 1.4.0  | ❌<sup>1,2</sup> | ❌<sup>1</sup> | ❌<sup>1</sup> | ❌<sup>1</sup> | ❌<sup>1</sup> | ❌<sup>1</sup> |
| Clojure 1.5.1  | ❌<sup>2</sup>   | ✅             | ✅             | ✅             | ✅             | ✅             |
| Clojure 1.6.0  | ❌<sup>2</sup>   | ✅             | ✅             | ✅             | ✅             | ✅             |
| Clojure 1.7.0  | ❌<sup>2</sup>   | ✅             | ✅             | ✅             | ✅             | ✅             |
| Clojure 1.8.0  | ❌<sup>2</sup>   | ✅             | ✅             | ✅             | ✅             | ✅             |
| Clojure 1.9.0  | ❌<sup>2</sup>   | ✅             | ✅             | ✅             | ✅             | ✅             |
| Clojure 1.10.0 | ❌<sup>2,3</sup> | ❌<sup>3</sup> | ✅             | ✅             | ✅             | ✅             |

<sup>1</sup> I chose to only go back as far as Clojure v1.5.1.  If anyone needs this on older versions, PRs are welcome!

<sup>2</sup> Leiningen v2.8 only supports JVM v1.7 and up

<sup>3</sup> Clojure v1.10 only supports JVM v1.8 and up

## Developer Information

[GitHub project](https://github.com/clj-commons/spinner)

[Bug Tracker](https://github.com/clj-commons/spinner/issues)

## License

Copyright © 2014 Peter Monks (pmonks@gmail.com)

Distributed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v20.html) either version 2.0 or (at your option) any later version.
