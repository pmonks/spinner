# spinner

[![CI](https://github.com/pmonks/spinner/actions/workflows/ci.yml/badge.svg?branch=dev)](https://github.com/pmonks/spinner/actions?query=workflow%3ACI+branch%3Adev)
[![Dependencies](https://github.com/pmonks/spinner/actions/workflows/dependencies.yml/badge.svg?branch=dev)](https://github.com/pmonks/spinner/actions?query=workflow%3Adependencies+branch%3Adev)
<br/>
[![Latest Version](https://img.shields.io/clojars/v/com.github.pmonks/spinner)](https://clojars.org/com.github.pmonks/spinner/)
[![Open Issues](https://img.shields.io/github/issues/pmonks/spinner.svg)](https://github.com/pmonks/spinner/issues)
[![License](https://img.shields.io/github/license/pmonks/spinner.svg)](https://github.com/pmonks/spinner/blob/release/LICENSE)
![Maintained](https://badges.ws/badge/?label=maintained&value=yes,+at+author's+discretion)

Progress indicators for command line Clojure apps, including support for indeterminate tasks (those where progress cannot be measured) and determinate tasks (those where progress can be measured).  The former are represented using "spinners", while the latter are represented using "progress bars".

#### Why?

To give the user of a command line app a visual progress indicator during long running processes.

Here it is in action (from the [demo script](https://github.com/pmonks/spinner/blob/dev/demo.clj):

<p align="center">
  <img alt="Spinner example screenshot" src="https://raw.githubusercontent.com/pmonks/spinner/dev/spinner-demo.gif"/>
</p>

Note that using Unicode characters in progress indicators may be unreliable, depending on your OS, terminal, font, encoding, phase of the moon, etc.

## Installation

`spinner` is available as a Maven artifact from [Clojars](https://clojars.org/com.github.pmonks/spinner).

## Usage

[API documentation is available here](https://pmonks.github.io/spinner/).  The [unit](https://github.com/pmonks/spinner/blob/release/test/progress/indeterminate_test.clj) [tests](https://github.com/pmonks/spinner/blob/release/test/progress/determinate_test.clj) provide comprehensive usage examples (alternative animation sets, formatting, etc.).

### Trying it Out

**Important Notes:**

1. If you're using leiningen, your REPL **must** be run in a trampoline (`lein trampoline repl`) in order for the ANSI escape sequences emitted by `spinner` to function.

2. If you're using the Clojure CLI tools, you **must** use the `clojure` binary, as the `clj` binary wraps the JVM in `rlwrap` which then incorrectly interprets some of the ANSI escape sequences emitted by `spinner`. Some other readline alternatives (notably [Rebel Readline](https://github.com/bhauman/rebel-readline)) have been reported to work correctly.

#### Clojure CLI

```shell
$ clojure -Sdeps '{:deps {com.github.pmonks/spinner {:mvn/version "RELEASE"}}}'
```

#### Leiningen

```shell
$ lein trampoline try com.github.pmonks/spinner
```

#### deps-try

Doesn't work properly, for the same reason the `clj` command line doesn't work properly (`rlwrap` intercepts the ANSI escape sequences emitted by this library and misinterprets them).

### Demo

```clojure
;; Indeterminate Task (aka "spinner")

(require '[progress.indeterminate :as pi])

(print "Something uncountably slow is happening... ")
(pi/animate! :opts {:frames (:clocks pi/styles)}
  (Thread/sleep 5000))
(println)


;; Determinate Task (aka "progress bar")

(require '[progress.determinate :as pd])

(println "And now something countably slow is happening...")
(let [a (atom 0)]
  (pd/animate! a :opts {:total 1000000
                        :redraw-rate 60
                        :style (:ascii-boxes pd/styles)}  ; :emoji-boxes is also fun to try
    (run! (fn [_] (Thread/sleep 0 10) (swap! a inc)) (range 1000000))))  ; Count up to a million, slowly
(println)
```

## Contributor Information

[Contributing Guidelines](https://github.com/pmonks/spinner/blob/release/.github/CONTRIBUTING.md)

[Bug Tracker](https://github.com/pmonks/spinner/issues)

[Code of Conduct](https://github.com/pmonks/spinner/blob/release/.github/CODE_OF_CONDUCT.md)

### Developer Workflow

This project uses the [git-flow branching strategy](https://nvie.com/posts/a-successful-git-branching-model/), and the permanent branches are called `release` and `dev`.  Any changes to the `release` branch are considered a release and auto-deployed (JARs to Clojars, API docs to GitHub Pages, etc.).

For this reason, **all development must occur either in branch `dev`, or (preferably) in temporary branches off of `dev`.**  All PRs from forked repos must also be submitted against `dev`; the `release` branch is **only** updated from `dev` via PRs created by the core development team.  All other changes submitted to `release` will be rejected.

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

Distributed under the [Mozilla Public License, version 2.0](https://www.mozilla.org/en-US/MPL/2.0/).

SPDX-License-Identifier: [`MPL-2.0`](https://spdx.org/licenses/MPL-2.0)
