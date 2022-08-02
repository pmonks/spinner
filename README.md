| | | |
|---:|:---:|:---:|
| [**main**](https://github.com/pmonks/spinner/tree/main) | [![CI](https://github.com/pmonks/spinner/workflows/CI/badge.svg?branch=main)](https://github.com/pmonks/spinner/actions?query=workflow%3ACI+branch%3Amain) | [![Dependencies](https://github.com/pmonks/spinner/workflows/dependencies/badge.svg?branch=main)](https://github.com/pmonks/spinner/actions?query=workflow%3Adependencies+branch%3Amain) |
| [**dev**](https://github.com/pmonks/spinner/tree/dev) | [![CI](https://github.com/pmonks/spinner/workflows/CI/badge.svg?branch=dev)](https://github.com/pmonks/spinner/actions?query=workflow%3ACI+branch%3Adev) | [![Dependencies](https://github.com/pmonks/spinner/workflows/dependencies/badge.svg?branch=dev)](https://github.com/pmonks/spinner/actions?query=workflow%3Adependencies+branch%3Adev) |

[![Latest Version](https://img.shields.io/clojars/v/com.github.pmonks/spinner)](https://clojars.org/com.github.pmonks/spinner/) [![Open Issues](https://img.shields.io/github/issues/pmonks/spinner.svg)](https://github.com/pmonks/spinner/issues) [![License](https://img.shields.io/github/license/pmonks/spinner.svg)](https://github.com/pmonks/spinner/blob/main/LICENSE)

# spinner

Progress indicators for command line Clojure apps, including support for indeterminate tasks (those where progress cannot be measured) and determinate tasks (those where progress can be measured).  The former are represented using "spinners", while the latter are represented using "progress bars".

## What is it useful for?

To give the user of a command line app a visual progress indicator during long running processes.

Here it is in action (from the unit tests):
<p align="center">
  <img alt="Spinner example screenshot" src="https://raw.githubusercontent.com/pmonks/spinner/main/spinner-demo.gif"/>
</p>

Note that using Unicode characters in progress indicators may be unreliable, depending on your OS, terminal, font, encoding, phase of the moon, etc.

## Installation

`spinner` is available as a Maven artifact from [Clojars](https://clojars.org/com.github.pmonks/spinner).

### Trying it Out

**Important Notes:**

1. If you're using leiningen, your REPL **must** be run in a trampoline (`lein trampoline repl`) in order for the ANSI escape sequences emitted by `spinner` to function.

2. If you're using the Clojure CLI tools, you **must** use the `clojure` binary, as the `clj` binary wraps the JVM in `rlwrap` which then incorrectly interprets some of the ANSI escape sequences emitted by `spinner`. Some other readline alternatives (notably [Rebel Readline](https://github.com/bhauman/rebel-readline)) have been reported to work correctly.

#### Clojure CLI

```shell
$ clojure -Sdeps '{:deps {com.github.pmonks/spinner {:mvn/version "#.#.#"}}}'  # Where #.#.# is replaced with an actual version number (see badge above)
```

#### Leiningen

```shell
$ lein try com.github.pmonks/spinner
```

#### Simple REPL Session

##### Indeterminate Task (aka "spinner")

```clojure
(require '[progress.indeterminate :as pi] :reload-all)

(pi/animate!
  (pi/print "A long running process...")
  (Thread/sleep 2500)   ; Simulate a long running process
  (pi/print "\nAnother long running process...")
  (Thread/sleep 2500)   ; Simulate another long running process
  (pi/print "\nAll done!\n"))  
```

##### Determinate Task (aka "progress bar")

```clojure
(require '[progress.determinate :as pd] :reload-all)

(let [a (atom 0)]
  ; Add up all the numbers from 1 to 100... ...slowly
  (pd/animate! a (reduce + (map #(do (Thread/sleep 10) (swap! a inc) %) (range 100)))))
```

## Usage

The functionality is provided by the `progress.indeterminate` and `progress.determinate` namespaces.

Require them in the REPL:

```clojure
(require '[progress.indeterminate :as pi] :reload-all)
(require '[progress.determinate   :as pd] :reload-all)
```

Require them in your application:

```clojure
(ns my-app.core
  (:require [progress.indeterminate :as pi]
            [progress.determinate   :as pd]))
```

### API Documentation

[API documentation is available here](https://pmonks.github.io/spinner/).  The [unit](https://github.com/pmonks/spinner/blob/main/test/progress/indeterminate_test.clj) [tests](https://github.com/pmonks/spinner/blob/main/test/progress/determinate_test.clj) provide comprehensive usage examples (alternative animation sets, formatting, etc.).

## Contributor Information

[Contributing Guidelines](https://github.com/pmonks/spinner/blob/main/.github/CONTRIBUTING.md)

[Bug Tracker](https://github.com/pmonks/spinner/issues)

[Code of Conduct](https://github.com/pmonks/spinner/blob/main/.github/CODE_OF_CONDUCT.md)

### Developer Workflow

This project uses the [git-flow branching strategy](https://nvie.com/posts/a-successful-git-branching-model/), with the caveat that the permanent branches are called `main` and `dev`, and any changes to the `main` branch are considered a release and auto-deployed (JARs to Clojars, API docs to GitHub Pages, etc.).

For this reason, **all development must occur either in branch `dev`, or (preferably) in temporary branches off of `dev`.**  All PRs from forked repos must also be submitted against `dev`; the `main` branch is **only** updated from `dev` via PRs created by the core development team.  All other changes submitted to `main` will be rejected.

### Build Tasks

`spinner` uses [`tools.build`](https://clojure.org/guides/tools_build). You can get a list of available tasks by running:

```
clojure -A:deps -T:build help/doc
```

Of particular interest are:

* `clojure -T:build test` - run the unit tests
* `clojure -T:build lint` - run the linters (clj-kondo and eastwood)
* `clojure -T:build ci` - run the full CI suite (check for outdated dependencies, run the unit tests, run the linters)
* `clojure -T:build install` - build the JAR and install it locally (e.g. so you can test it with downstream code)

Please note that the `deploy` task is restricted to the core development team (and will not function if you run it yourself).

### Why are there so many different groupIds on Clojars for this project?

The project was originally developed under my personal GitHub account.  In early 2018 it was transferred to the `clj-commons` GitHub organisation, but then, as that group refined their scope and mission, it was determined that it no longer belonged there, and the project were transferred back in late 2021.  During this time the build tooling for the project also changed from Leiningen to tools.build, which created further groupId churn (tools.build introduced special, useful semantics for `com.github.username` groupIds that don't exist with Leiningen or Clojars).

### Why is it called "spinner", when it offers more than just spinners?

tl;dr - historical reasons and naming is hard.

The library started life providing a single hardcoded animation sequence (the classic "/-\\|" sequence), and then organically grew from there.  Because the name "spinner" appears in various places where changing it would break things (the GitHub repo, Maven artifact ids, etc.), I decided to stick with the name even though it's no longer very accurate.

## License

Copyright © 2014 Peter Monks

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

SPDX-License-Identifier: [Apache-2.0](https://spdx.org/licenses/Apache-2.0)
