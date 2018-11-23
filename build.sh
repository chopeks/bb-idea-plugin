#!/bin/bash

rm -rf gen
java -jar tools/grammar-kit.jar gen src/com/sqide/Squirrel.bnf
java -jar idea/tools/lexer/jflex-1.7.0.jar --skel idea/tools/lexer/idea-flex.skeleton --nobak src/com/sqide/lexer/SquirrelLexer.flex -d gen/com/sqide/lexer/
java -jar idea/tools/lexer/jflex-1.7.0.jar --skel idea/tools/lexer/idea-flex.skeleton --nobak src/com/sqide/lexer/SquirrelDocLexer.flex -d gen/com/sqide/lexer/
