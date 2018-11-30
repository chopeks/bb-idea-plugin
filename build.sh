#!/bin/bash
IDEA_VERSION=183.4284.148

if [[ ! -d "idea" ]]; then
    if [[ ! -f "ideaIC-2018.3.zip" ]]; then
        echo "Downloading idea"
        wget -q "https://www.jetbrains.com/intellij-repository/releases/com/jetbrains/intellij/idea/ideaIC/$IDEA_VERSION/ideaIC-$IDEA_VERSION.zip"
    fi
    echo "unzipping idea"
    unzip -q "ideaIC-$IDEA_VERSION.zip" -d idea
fi

rm -rf gen
java -cp "tools/grammar-kit.jar" org.intellij.grammar.Main gen src/com/sqide/Squirrel.bnf

java -cp 'tools/*' jflex.Main --skel tools/idea-flex.skeleton --nobak src/com/sqide/lexer/SquirrelLexer.flex -d gen/com/sqide/lexer/
java -cp 'tools/*' jflex.Main --skel tools/idea-flex.skeleton --nobak src/com/sqide/lexer/SquirrelDocLexer.flex -d gen/com/sqide/lexer/

./gradlew clean compileJava
java -cp "build/classes/java/main:tools/grammar-kit.jar" org.intellij.grammar.Main gen src/com/sqide/Squirrel.bnf

./gradlew clean buildPlugin