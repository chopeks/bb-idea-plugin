#!/bin/bash

rm -rf gen
java -jar tools/grammar-kit.jar gen src/com/sqide/Squirrel.bnf
java -cp 'tools/*' jflex.Main --skel idea/tools/lexer/idea-flex.skeleton --nobak src/com/sqide/lexer/SquirrelLexer.flex -d gen/com/sqide/lexer/
java -cp 'tools/*' jflex.Main --skel idea/tools/lexer/idea-flex.skeleton --nobak src/com/sqide/lexer/SquirrelDocLexer.flex -d gen/com/sqide/lexer/
                                    