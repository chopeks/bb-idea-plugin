package com.chopeks

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class SquirrelBraceMatcher : PairedBraceMatcher {
	private val bracePairs = arrayOf(
		BracePair(SquirrelTokenTypes.LBRACE, SquirrelTokenTypes.RBRACE, true),
		BracePair(SquirrelTokenTypes.LBRACKET, SquirrelTokenTypes.RBRACKET, false),
		BracePair(SquirrelTokenTypes.LPAREN, SquirrelTokenTypes.RPAREN, false),
		BracePair(SquirrelTokenTypes.CLASS_ATTR_START, SquirrelTokenTypes.CLASS_ATTR_END, false),
	)

	override fun getPairs() = bracePairs
	override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true
	override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int) = openingBraceOffset
}