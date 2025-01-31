package com.chopeks.util

import com.chopeks.SquirrelTokenTypes
import com.chopeks.SquirrelTokenTypesSets
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.tree.TokenSet

object SquirrelFormatterUtil {
	/**
	 * True of element is not block. Returns true even for "if (true) {};"
	 */
	fun isSimpleStatement(element: ASTNode): Boolean {
		if (element.elementType === SquirrelTokenTypes.SEMICOLON) {
			val prev = element.treePrev
			return prev != null && prev.elementType !== SquirrelTokenTypes.BLOCK
		}

		return SquirrelTokenTypesSets.STATEMENTS.contains(element.elementType) && element.elementType !== SquirrelTokenTypes.BLOCK
	}

	/**
	 * True if block, class body, table or enum has only one child.
	 */
	fun isSimpleBlock(element: ASTNode): Boolean {
		if (element.elementType === SquirrelTokenTypes.ENUM_DECLARATION) {
			return element.getChildren(TokenSet.create(SquirrelTokenTypes.ENUM_ITEM)).size <= 1
		} else if (element.elementType === SquirrelTokenTypes.TABLE_EXPRESSION) {
			return element.getChildren(TokenSet.create(SquirrelTokenTypes.TABLE_ITEM)).size <= 1
		} else if (element.elementType === SquirrelTokenTypes.CLASS_BODY) {
			return element.getChildren(TokenSet.create(SquirrelTokenTypes.CLASS_MEMBER)).size <= 1
		} else if (element.elementType === SquirrelTokenTypes.BLOCK) {
			return element.getChildren(SquirrelTokenTypesSets.STATEMENTS).size <= 1
		}
		return false
	}

	/**
	 * True if element inside block, class body, table or enum.
	 */
	fun isBetweenBraces(node: ASTNode): Boolean {
		val elementType = node.elementType
		if (elementType === SquirrelTokenTypes.LBRACE || elementType === SquirrelTokenTypes.RBRACE) return false

		var sibling = node.treePrev
		while (sibling != null) {
			if (sibling.elementType === SquirrelTokenTypes.LBRACE) return true
			sibling = sibling.treePrev
		}

		return false
	}

	fun getTextRangeFromTheBeginningOfElement(element: ASTNode, child: ASTNode): TextRange {
		return TextRange(element.startOffset, child.textRange.endOffset)
	}
}