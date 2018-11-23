.h1 For local development

1. clone the squirrel foe
```git@github.com:kiwipower/squirrel-lang-idea-plugin.git```

2. download intellij comminuty edition, and extract somewhere
```https://www.jetbrains.com/idea/download```

3. download intellij comminity edition sources to idea in theis project
```git clone --depth 1 git@github.com:JetBrains/intellij-community.git idea```

4. open project structure. Delete the SDK and re-add pointing to the community edition home directory. In sourcepath click + and add the idea directorty from above.

5. run the 'build.sh' file

6. compile all