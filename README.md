# spinner
A simple text spinner for command line Clojure apps.

What is it useful for?

To give the user of a command line app a simple progress indicator for long running operations.
Supports output of additional messages while a spinner is active.

## Installation

spinner is available as a Maven artifact from [Clojars](https://clojars.org/org.clojars.pmonks/spinner).
Plonk the following in your project.clj :dependencies, `lein deps` and you should be good to go:

```clojure
[org.clojars.pmonks/spinner "#.#.#"]
```

The latest version is:

[![version](https://clojars.org/org.clojars.pmonks/spinner/latest-version.svg)](https://clojars.org/org.clojars.pmonks/spinner)

## Usage

The spinner functionality is provided by the `spinner.core` namespace.

Require it in the REPL:

```clojure
(require '[spinner.core :as spin])
```

Require it in your application:

```clojure
(ns my-app.core
  (:require [spinner.core :as spin]))
```

The library provides a number of methods to manage the lifecycle of a spinner:

```
user=> (doc spinner.core/create!)
-------------------------
spinner.core/create!
([] [options])
  Creates a spinner and returns it, but does not start it.

   Optionally accepts an options map - supported options are:
   {
     :characters - the string of characters to use for the spinner (default is (:spinner styles))
     :delay - the delay (in ms) between frames (default is 100ms)
     :fg-colour / :fg-color - the foregound colour of the spinner (default is :default) - see https://github.com/xsc/jansi-clj#colors for allowed values
     :bg-colour / :bg-colour - the background colour of the spinner (default is :default) - see https://github.com/xsc/jansi-clj#colors for allowed values
     :attribute - the attribute of the spinner (default is :default) - see https://github.com/xsc/jansi-clj#attributes for allowed values
   }

   Note: this method has the side effect of enabling JANSI - see https://github.com/xsc/jansi-clj#globally-enabledisable-ansi-codes
nil
user=> (doc spinner.core/start!)
-------------------------
spinner.core/start!
([spinner])
  Starts the given spinner.
nil
user=> (doc spinner.core/stop!)
-------------------------
spinner.core/stop!
([spinner])
  Stops the given spinner.
   Note: after being stopped, a spinner cannot be restarted.
nil
user=> (doc spinner.core/active?)
-------------------------
spinner.core/active?
([spinner])
  Is the given spinner active?
nil
user=> (doc spinner.core/create-and-start!)
-------------------------
spinner.core/create-and-start!
([] [options])
  Creates and starts a spinner, returning it.
nil
user=> (doc spinner.core/spin!)
-------------------------
spinner.core/spin!
([f] [f options])
  Creates and starts a spinner, calls fn f, then stops the spinner. Returns the result of f.
nil
user=> (source spinner.core/styles)
(def styles
  "A selection of predefined styles of spinner. Only :spinner is known to work on Windows
   (the Windows command prompt is not Unicode capable)."
  {
    :spinner         "|/-\\"
    :dot-spinner     "⋮⋰⋯⋱"
    :up-and-down     "▁▃▄▅▆▇█▇▆▅▄▃"
    :fade-in-and-out " ░▒▓█▓▒░"
    :side-to-side    "▉▊▋▌▍▎▏▎▍▌▋▊▉"
    :quadrants       "┤┘┴└├┌┬┐"
  })
nil
user=> (doc spinner.core/is-windows?)
-------------------------
spinner.core/is-windows?
  Are we running on Windows?
nil
```

After being started, the spinner will continue spinning at the current cursor location until explicitly stopped.
While it is strongly recommended that you explicitly stop a spinner before terminating, the spinner runs on a
daemon thread, so any termination of the JVM should also kill the spinner.

It's difficult to show examples of the output from the program, but try the following in your REPL:

```
user=> (require '[spinner.core :as spin])
WARNING: print already refers to: #'clojure.core/print in namespace: spinner.core, being replaced by: #'spinner.core/print
nil
user=> (def s (spin/create!))
#'user/s
user=> (do (spin/start! s) (Thread/sleep 5000) (spin/stop! s))
nil
user=> (def s (spin/create! { :fg-colour :red :bg-colour :yellow }))
#'user/s
user=> (do (spin/start! s) (Thread/sleep 5000) (spin/stop! s))
nil
user=> (let [s (spin/create-and-start! { :characters (:up-and-down spin/styles) })]
  #_=>   (Thread/sleep 5000)
  #_=>   (spin/stop! s))
nil
user=> (do
  #_=>   (print "Reticulating splines... ")
  #_=>   (flush)
  #_=>   (spin/spin! #(Thread/sleep 5000))
  #_=>   (println))
Reticulating splines...
nil
user=> (do
  #_=>   (print "Reticulating splines")
  #_=>   (flush)
  #_=>   (let [s (spin/create-and-start!)]
  #_=>     (Thread/sleep 1000)
  #_=>     (spin/print ".")
  #_=>     (Thread/sleep 1000)
  #_=>     (spin/print ".")
  #_=>     (Thread/sleep 1000)
  #_=>     (spin/print ".")
  #_=>     (Thread/sleep 1000)
  #_=>     (spin/print ".")
  #_=>     (Thread/sleep 1000)
  #_=>     (spin/print ".")
  #_=>     (Thread/sleep 1000)
  #_=>     (spin/stop! s))
  #_=>   (println))
Reticulating splines.....
nil
user=> (do
  #_=>   (print "Reticulating splines... ")
  #_=>   (flush)
  #_=>   (let [s (spin/create-and-start!)]
  #_=>     (Thread/sleep 1000)
  #_=>     (spin/print "\nInserting sublimated messages... ")
  #_=>     (Thread/sleep 1000)
  #_=>     (spin/print "\nAttempting to lock back buffer... ")
  #_=>     (Thread/sleep 1000)
  #_=>     (spin/print "\nTime-compressing simulator clock... ")
  #_=>     (Thread/sleep 1000)
  #_=>     (spin/print "\nLecturing errant subsystems... ")
  #_=>     (Thread/sleep 1000)
  #_=>     (spin/print "\nRetracting Phong shader... ")
  #_=>     (Thread/sleep 1000)
  #_=>     (spin/stop! s))
  #_=>   (println))
Reticulating splines...
Inserting sublimated messages...
Attempting to lock back buffer...
Time-compressing simulator clock...
Lecturing errant subsystems...
Retracting Phong shader...
nil
```

## Developer Information

[GitHub project](https://github.com/pmonks/spinner)

[Bug Tracker](https://github.com/pmonks/spinner/issues)

[![endorse](https://api.coderwall.com/pmonks/endorsecount.png)](https://coderwall.com/pmonks)

## License

Copyright © 2014 Peter Monks (pmonks@gmail.com)

Distributed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) either version 1.0 or (at your option) any later version.
