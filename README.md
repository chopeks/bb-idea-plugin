# BB Squirrel Intellij plugin

Adds support for version of Squirrel used in Battle Brothers game, plus modding helpers.

Roughly based on original Squirrel plugin for Intellij [README.md](ORIGINAL_README.md), but mostly rewritten from scratch.

## Installation

* Get Intellij IDEA with version at least 2024.2, Community or Ultimate, doesn't matter
* Uninstall old Squirrel IDE plugin
* Download the latest zip from the [releases page](../../releases).
* Follow the [install plugin from disk](https://www.jetbrains.com/help/idea/managing-plugins.html) instructions
* Add unpacked vanilla to the project and mark as source `Right click | Mark directory as | Sources root`
* Mark mod directories as sources `Right click | Mark directory as | Sources root`
* Rebuild indexes, on project root directory `Right click | Cache Recovery | Rescan Project Indexes`

## Features

- Reworked BNF based parser that fits BB version of Squirrel better
- New file templates for classes and modding related stuff
- Code highlighting
- BB aware .nut file structure validation
- Block folding
- Customizable code formatter
- Live code validation and various on-demand inspections
- Code completion for BB classes, modding hooks and modern hooks (WIP)
- String reference tracking (scripts and gfx files)
- Images and colors used in code show on gutter
- ... more coming, maybe

## Plugin development prerequisites

* Java 17+
* IntelliJ

## Plugin development environment

* Install [Grammar-Kit](https://plugins.jetbrains.com/plugin/6606-grammar-kit) for `*.bnf` and `*.flex` file support
  * Adds BNF Grammars and JFlex files editing support including parser/PSI code generator
* Clone repository
* Follow instructions on the plugin development environment setup [page](https://www.jetbrains.org/intellij/sdk/docs/tutorials/build_system/prerequisites.html)
  * Enable the *Gradle* plugin
  * Enable the *Plugin DevKit*
* Use `File | New… | Project from Existing Sources` and open the cloned repository directory

### Testing the plugin locally

* Run the Gradle `runIde` task which will start a new IntelliJ instance with the plugin installed.
* Install [PsiViewer](https://plugins.jetbrains.com/plugin/227-psiviewer) on that instance to see how files are structured (`Tools | View PSI structure for Current File...`). It helps greatly when changing code in plugin.
