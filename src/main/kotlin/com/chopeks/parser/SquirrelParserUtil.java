package com.chopeks.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.chopeks.SquirrelTokenTypes.*;
import static com.chopeks.SquirrelTokenTypesSets.COMMENTS;
import static com.chopeks.SquirrelTokenTypesSets.WHITE_SPACES;

public class SquirrelParserUtil extends GeneratedParserUtilBase {

    public static boolean prevIsBrace(@NotNull PsiBuilder builder_, int level) {
        IElementType type = null;
        for (int i = 1; i < builder_.getCurrentOffset(); i++) {
            type = builder_.rawLookup(-i);
            if (!COMMENTS.contains(type) && !WHITE_SPACES.contains(type)) {
                break;
            }
        }

        return (type == RBRACE || type == SEMICOLON || type == SEMICOLON_SYNTHETIC);
    }
}