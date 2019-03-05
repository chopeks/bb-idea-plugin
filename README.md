# KiWi Power Squirrel Idea plugin

KiWi Power fork of [Shvetsgroup's Squirrel plugin for IntelliJ](https://github.com/shvetsgroup/squirrel-lang-idea-plugin)

Adds support for Electric Imp’s impWorks™ [Builder](https://github.com/electricimp/Builder)

Original [README.md](ORIGINAL_README.md)

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
  * [`@include`](https://developer.electricimp.com/tools/builder#include) and `@include once`
  * Electric Imp [`agent`](https://developer.electricimp.com/api/agent)`.on` → [`device`](https://developer.electricimp.com/api/device)`.send` and `agent.send → device.on`

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
* Use `File | New… | Project from Existing Sources` and open the cloned repository directory

## Test

* Run `./gradlew test` from the command line or execute the gradle `test` task

## Build

* To manually recreate the generated src from IntelliJ use the context menu in the [Squirrel.bnf](src/com/sqide/Squirrel.bnf) file, `Ctrl+⇧+G` or `⌘+⇧+G` (the build will do this automatically)
* From the context menu in the `Squirrel.bnf` file, select *Live Preview* to test the grammar before building
* Running `./gradlew buildPlugin` from the command line or executing the gradle `build` task produces a zip file under `build/distributions`

### Testing the plugin locally

* Either drag & drop the zip file into the running IntelliJ, which will need restarting to apply
* Alternatively run the Gradle `runIde` task which will start a new IntelliJ instance with the plugin installed

# Known issues

* For performance file and element resolution is cached
  * New files/usages will need a restart to start appearing
* Synthetic semi-colon detection is broken in some cases
  * Add semi-colons to includes and local variables if IntelliJ shows red
  * It sometimes stops the references from being resolved
