## Kiwi Power's Fork on the Squirrel Idea plugin

Kiwi power's fork of [Shvetsgroup's Squirrel plugin for intellij](https://github.com/shvetsgroup/squirrel-lang-idea-plugin) 

See original [README.md](ORIGINAL_README.md)

### Local installation
- Uninstall the Squirrel ide plugin
- Download the latest Squirrel-<version>.zip from the [releases page](../../releases).
- Open settings, plugins, click "install plugin from disk", and choose the zip file from 2. 

### Features
- Control+b for 
    - parameters
    - variables
    - constants
    - functions
    - methods
    - classes
    - class members
    - enum constants
    - includes
    - just from agent.on -> device.send (and agent.send -> device.on)
    
### Build Locally

```bash
 ./build.sh 
```
Produces the zip under build/distribution

For developing in Intellij, just do file->new->project->Intellij Plugin Project and point to the directory where this project is checked out. Don't try to get gradle to generate the idea files for you (or link the gradle build to the project).

To re-generate the gen src from intellij click Control+Shift+g from the Squirrel.bnf file. 

There are a few tests from the original plugin, but nothing new. The SamplesTest is useful for seeing what AST is produced from some squirrel code.

### Known bugs

- For performance the files and element->resolution are cached. New files/ new usages will need a restart to start appearing. I'm happy with this trade off for the moment: being quick enough to browse is more important that being 
- The synthetic semi colon detection is broken in some cases. Add semi-colons to includes and local variables if Intellij shows red. It sometimes stops the references from being resolved.
