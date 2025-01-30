package com.sqide.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;
import com.sqide._SquirrelLexer;

import static com.sqide.SquirrelTokenTypesSets.MULTI_LINE_COMMENT_BODY;

public class SquirrelDocLexer extends MergingLexerAdapter {

    public SquirrelDocLexer() {
        super(new FlexAdapter(new _SquirrelLexer()), TokenSet.create(MULTI_LINE_COMMENT_BODY));
    }
}