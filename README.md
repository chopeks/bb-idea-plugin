# BB Squirrel Intellij plugin

Adds support for BB version of Squirrel plus some templates for modding.

Original [README.md](ORIGINAL_README.md)

## Installation
* Uninstall the Squirrel ide plugin
* Download the latest zip from the [releases page](../../releases).
* Follow the [install plugin from disk](https://www.jetbrains.com/help/idea/managing-plugins.html) instructions

## Prerequisites

* Java 17+
* IntelliJ at least 2024.2.x

## Plugin development environment

* Install [Grammar-Kit](https://plugins.jetbrains.com/plugin/6606-grammar-kit) for `*.bnf` and `*.flex` file support
  * Adds BNF Grammars and JFlex files editing support including parser/PSI code generator
* Clone repository
* Follow instructions on the plugin development environment setup [page](https://www.jetbrains.org/intellij/sdk/docs/tutorials/build_system/prerequisites.html)
  * Enable the *Gradle* plugin
  * Enable the *Plugin DevKit*
* Use `File | New… | Project from Existing Sources` and open the cloned repository directory

### Testing the plugin locally

* Either drag & drop the zip file into the running IntelliJ, which will need restarting to apply
* Alternatively run the Gradle `runIde` task which will start a new IntelliJ instance with the plugin installed
