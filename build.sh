#!/bin/bash

rm -rf gen
java -cp "tools/grammar-kit.jar" org.intellij.grammar.Main gen src/com/sqide/Squirrel.bnf

./gradlew clean compileJava

java -cp 'tools/*' jflex.Main --skel idea/tools/lexer/idea-flex.skeleton --nobak src/com/sqide/lexer/SquirrelLexer.flex -d gen/com/sqide/lexer/
java -cp 'tools/*' jflex.Main --skel idea/tools/lexer/idea-flex.skeleton --nobak src/com/sqide/lexer/SquirrelDocLexer.flex -d gen/com/sqide/lexer/

java -cp "build/classes/java/main:tools/grammar-kit.jar" org.intellij.grammar.Main gen src/com/sqide/Squirrel.bnf

./gradlew clean buildPlugin