| | | |
|---:|:---:|:---:|
| [**main**](https://github.com/pmonks/spinner/tree/main) | [![CI](https://github.com/pmonks/spinner/workflows/CI/badge.svg?branch=main)](https://github.com/pmonks/spinner/actions?query=workflow%3ACI+branch%3Amain) | [![Dependencies](https://github.com/pmonks/spinner/workflows/dependencies/badge.svg?branch=main)](https://github.com/pmonks/spinner/actions?query=workflow%3Adependencies+branch%3Amain) |
| [**dev**](https://github.com/pmonks/spinner/tree/dev) | [![CI](https://github.com/pmonks/spinner/workflows/CI/badge.svg?branch=dev)](https://github.com/pmonks/spinner/actions?query=workflow%3ACI+branch%3Adev) | [![Dependencies](https://github.com/pmonks/spinner/workflows/dependencies/badge.svg?branch=dev)](https://github.com/pmonks/spinner/actions?query=workflow%3Adependencies+branch%3Adev) |

[![Latest Version](https://img.shields.io/clojars/v/com.github.pmonks/spinner)](https://clojars.org/com.github.pmonks/spinner/) [![Open Issues](https://img.shields.io/github/issues/pmonks/spinner.svg)](https://github.com/pmonks/spinner/issues) [![License](https://img.shields.io/github/license/pmonks/spinner.svg)](https://github.com/pmonks/spinner/blob/main/LICENSE)

# spinner

A simple text spinner for command line Clojure apps.

What is it useful for?

To give the user of a command line app a simple indeterminate progress indicator for long running operations.

Here it is in action (from the unit tests):
<p align="center">
  <img alt="Spinner example screenshot" src="https://raw.githubusercontent.com/pmonks/spinner/main/spinner-demo.gif"/>
</p>

Note that using Unicode characters in spinners may be unreliable, depending on your OS, terminal, font, encoding, phase of the moon, etc.

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

```clojure
(require '[spinner.core :as spin] :reload-all)
(spin/spin! #(Thread/sleep 5000))
```

## Usage

The functionality is provided by the `spinner.core` namespace.

Require it in the REPL:

```clojure
(require '[spinner.core :as spin] :reload-all)
```

Require it in your application:

```clojure
(ns my-app.core
  (:require [spinner.core :as spin]))
```

### API Documentation

[API documentation is available here](https://pmonks.github.io/spinner/).  [The unit tests](https://github.com/pmonks/spinner/blob/main/test/spinner/core_test.clj) provide comprehensive usage examples.

## Contributor Information

[Contributing Guidelines](https://github.com/pmonks/spinner/blob/main/.github/CONTRIBUTING.md)

[Bug Tracker](https://github.com/pmonks/spinner/issues)

[Code of Conduct](https://github.com/pmonks/spinner/blob/main/.github/CODE_OF_CONDUCT.md)

### Developer Workflow

This project uses the [git-flow branching strategy](https://nvie.com/posts/a-successful-git-branching-model/), with the caveat that the permanent branches are called `main` and `dev`, and any changes to the `main` branch are considered a release and auto-deployed (JARs to Clojars, API docs to GitHub Pages, etc.).

For this reason, **all development must occur either in branch `dev`, or (preferably) in temporary branches off of `dev`.**  All PRs from forked repos must also be submitted against `dev`; the `main` branch is **only** updated from `dev` via PRs created by the core development team.  All other changes submitted to `main` will be rejected.

### Why are there so many different groupIds on Clojars for this project?

The project was originally developed under my personal GitHub account.  In early 2018 it was transferred to the `clj-commons` GitHub organisation, but then, as that group refined their scope and mission, it was determined that it no longer belonged there, and the project were transferred back in late 2021.  During this time the build tooling for the project also changed from Leiningen to tools.build, which created further groupId churn (tools.build introduced special, useful semantics for `com.github.username` groupIds that don't exist with Leiningen or Clojars).

## License

Copyright © 2014 Peter Monks

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

SPDX-License-Identifier: [Apache-2.0](https://spdx.org/licenses/Apache-2.0)
