# KiWi Power Squirrel Idea plugin

KiWi Power fork of [Shvetsgroup's Squirrel plugin for IntelliJ](https://github.com/shvetsgroup/squirrel-lang-idea-plugin)

See original [README.md](ORIGINAL_README.md)

## Installation
* Uninstall the Squirrel ide plugin
* Download the latest `Squirrel-KiwiPowerFork-<version>.zip` from the [releases page](../../releases).
* Follow the [install plugin from disk](https://www.jetbrains.com/help/idea/managing-plugins.html) instructions

# Features

* Enhances navigation using `Ctrl+B` `⌘+B`, `Ctrl+Click` `⌘+Click` for
  * parameters
  * variables
  * constants
  * functions
  * methods
  * classes
  * class members
  * enum constants
  * ElectricImp [Builder](https://github.com/electricimp/Builder) [`@include`](https://developer.electricimp.com/tools/builder#include)
  * ElectricImp [`agent`](https://developer.electricimp.com/api/agent)`.on` → [`device`](https://developer.electricimp.com/api/device)`.send` and `agent.send → device.on`

# Local development

## Prerequisites

* Java 8+
* IntelliJ

## Plugin development environment

* Install [Grammar-Kit](https://plugins.jetbrains.com/plugin/6606-grammar-kit) for `*.bnf` and `*.flex` file support
  * Adds BNF Grammars and JFlex files editing support including parser/PSI code generator
* Clone repository
* Follow instructions on the plugin development environment setup [page](https://www.jetbrains.org/intellij/sdk/docs/tutorials/build_system/prerequisites.html)
  * Enable the *Gradle* plugin
  * Enable the *Plugin DevKit*
* Instead of creating a plugin project from scratch, use `File | New… | Project from Existing Sources` and open the cloned repository directory
* Running `./gradlew buildPlugin` from the command line or executing the gradle `build` task from IntelliJ produces the zip under `build/distributions`

To re-generate the gen src from intellij click Control+Shift+g from the Squirrel.bnf file.

# Known issues

* For performance the files and element → resolution are cached
  * New files/usages will need a restart to start appearing
* Synthetic semi-colon detection is broken in some cases
  * Add semi-colons to includes and local variables if IntelliJ shows red
  * It sometimes stops the references from being resolved
