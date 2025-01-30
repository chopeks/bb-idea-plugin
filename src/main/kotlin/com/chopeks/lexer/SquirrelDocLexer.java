package com.chopeks.lexer;

import com.chopeks._SquirrelLexer;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;

import static com.chopeks.SquirrelTokenTypesSets.MULTI_LINE_COMMENT_BODY;

public class SquirrelDocLexer extends MergingLexerAdapter {

    public SquirrelDocLexer() {
        super(new FlexAdapter(new _SquirrelLexer()), TokenSet.create(MULTI_LINE_COMMENT_BODY));
    }
}