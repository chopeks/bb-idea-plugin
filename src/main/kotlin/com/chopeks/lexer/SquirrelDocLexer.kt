package com.chopeks.lexer

import com.chopeks.SquirrelTokenTypesSets.MULTI_LINE_COMMENT_BODY
import com.chopeks._SquirrelLexer
import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.psi.tree.TokenSet

class SquirrelDocLexer : MergingLexerAdapter(FlexAdapter(_SquirrelLexer()), TokenSet.create(MULTI_LINE_COMMENT_BODY))