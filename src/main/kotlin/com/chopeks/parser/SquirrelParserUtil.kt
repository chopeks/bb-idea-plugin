package com.chopeks.parser

import com.chopeks.SquirrelTokenTypes
import com.chopeks.SquirrelTokenTypesSets
import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.tree.IElementType

object SquirrelParserUtil : GeneratedParserUtilBase() {
	@JvmStatic
	fun prevIsBrace(builder: PsiBuilder, level: Int): Boolean {
		var type: IElementType? = null
		for (i in 1..<builder.currentOffset) {
			type = builder.rawLookup(-i)
			if (!SquirrelTokenTypesSets.COMMENTS.contains(type) && !SquirrelTokenTypesSets.WHITE_SPACES.contains(type)) {
				break
			}
		}

		return (type === SquirrelTokenTypes.RBRACE || type === SquirrelTokenTypes.SEMICOLON || type === SquirrelTokenTypes.SEMICOLON_SYNTHETIC)
	}
}