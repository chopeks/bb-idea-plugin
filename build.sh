#!/bin/bash

if [ ! -d "idea" ]; then
    curl https://cache-redirector.jetbrains.com/www.jetbrains.com/intellij-repository/releases/com/jetbrains/intellij/idea/ideaIC/2018.3/ideaIC-2018.3.zip
    unzip ideaIC-2018.3.zip -d idea
fi

rm -rf gen
java -cp "tools/grammar-kit.jar" org.intellij.grammar.Main gen src/com/sqide/Squirrel.bnf

./gradlew clean compileJava

java -cp 'tools/*' jflex.Main --skel tools/idea-flex.skeleton --nobak src/com/sqide/lexer/SquirrelLexer.flex -d gen/com/sqide/lexer/
java -cp 'tools/*' jflex.Main --skel tools/idea-flex.skeleton --nobak src/com/sqide/lexer/SquirrelDocLexer.flex -d gen/com/sqide/lexer/

java -cp "build/classes/java/main:tools/grammar-kit.jar" org.intellij.grammar.Main gen src/com/sqide/Squirrel.bnf

./gradlew clean buildPlugin