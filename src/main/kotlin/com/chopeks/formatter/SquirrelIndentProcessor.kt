package com.chopeks.formatter

import com.chopeks.SquirrelTokenTypes
import com.chopeks.SquirrelTokenTypesSets
import com.chopeks.formatter.settings.SquirrelCodeStyleSettings
import com.chopeks.util.SquirrelFormatterUtil
import com.chopeks.util.UsefulPsiTreeUtil
import com.intellij.formatting.Indent
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.formatter.FormatterUtil

object SquirrelIndentProcessor {
	@JvmStatic
	fun getChildIndent(node: ASTNode, cmSettings: CommonCodeStyleSettings, sqSettings: SquirrelCodeStyleSettings?): Indent {
		val elementType = node.elementType
		val prevSibling = UsefulPsiTreeUtil.getPrevSiblingSkipWhiteSpacesAndComments(node)
		val prevSiblingType = prevSibling?.elementType
		val parent = node.treeParent
		val parentType = parent?.elementType
		val superParent = parent?.treeParent
		val superParentType = superParent?.elementType

		// COMMENTS
		if (parent == null || parent.treeParent == null) {
			return Indent.getNoneIndent()
		}

		if (elementType === SquirrelTokenTypesSets.MULTI_LINE_COMMENT_BODY) {
			return Indent.getContinuationIndent()
		}
		if (elementType === SquirrelTokenTypesSets.DOC_COMMENT_LEADING_ASTERISK || elementType === SquirrelTokenTypesSets.MULTI_LINE_COMMENT_END) {
			return Indent.getSpaceIndent(1, true)
		}

		if (cmSettings.KEEP_FIRST_COLUMN_COMMENT && (elementType === SquirrelTokenTypes.SINGLE_LINE_COMMENT || elementType === SquirrelTokenTypes.MULTI_LINE_COMMENT)) {
			val previousNode = node.treePrev
			if (previousNode != null && previousNode.elementType === SquirrelTokenTypesSets.WHITE_SPACE && previousNode.text.endsWith("\n")) {
				return Indent.getAbsoluteNoneIndent()
			}
		}

		if (SquirrelTokenTypesSets.COMMENTS.contains(elementType) && prevSiblingType === SquirrelTokenTypes.LBRACE && parentType === SquirrelTokenTypes.CLASS_BODY) {
			return Indent.getNormalIndent()
		}

		if (elementType === SquirrelTokenTypes.SEMICOLON && FormatterUtil.isPrecededBy(node, SquirrelTokenTypes.SINGLE_LINE_COMMENT, SquirrelTokenTypesSets.WHITE_SPACES)) {
			return Indent.getContinuationIndent()
		}

		// BRACES & BLOCKS
		val braceStyle = if (superParentType === SquirrelTokenTypes.CLASS_BODY) cmSettings.CLASS_BRACE_STYLE else (if (superParentType === SquirrelTokenTypes.FUNCTION_BODY) cmSettings.METHOD_BRACE_STYLE else cmSettings.BRACE_STYLE)

		if (elementType === SquirrelTokenTypes.LBRACE || elementType === SquirrelTokenTypes.RBRACE) {
			when (braceStyle) {
				CommonCodeStyleSettings.END_OF_LINE -> {
					if (elementType === SquirrelTokenTypes.LBRACE && FormatterUtil.isPrecededBy(parent, SquirrelTokenTypes.SINGLE_LINE_COMMENT, SquirrelTokenTypesSets.WHITE_SPACES)) {
						// Use Nystrom style rather than Allman.
						return Indent.getContinuationIndent()
					} // FALL THROUGH

					return Indent.getNoneIndent()
				}

				CommonCodeStyleSettings.NEXT_LINE, CommonCodeStyleSettings.NEXT_LINE_IF_WRAPPED -> return Indent.getNoneIndent()
				CommonCodeStyleSettings.NEXT_LINE_SHIFTED, CommonCodeStyleSettings.NEXT_LINE_SHIFTED2 -> return Indent.getNormalIndent()
				else -> return Indent.getNoneIndent()
			}
		}

		if (parentType === SquirrelTokenTypes.BLOCK) {
			val psi = node.psi
			if (psi.parent is PsiFile) {
				return Indent.getNoneIndent()
			}
			return Indent.getNormalIndent()
		}

		// --- Expressions
		if (parentType === SquirrelTokenTypes.PARENTHESIZED_EXPRESSION) {
			if (elementType === SquirrelTokenTypes.LPAREN || elementType === SquirrelTokenTypes.RPAREN) {
				return Indent.getNoneIndent()
			}
			return Indent.getContinuationIndent()
		}
		if (parentType === SquirrelTokenTypes.ARRAY_EXPRESSION) {
			if (elementType === SquirrelTokenTypes.LBRACKET || elementType === SquirrelTokenTypes.RBRACKET) {
				return Indent.getNoneIndent()
			}
			return Indent.getContinuationIndent()
		}
		if (SquirrelTokenTypesSets.BINARY_EXPRESSIONS.contains(parentType) && prevSibling != null) {
			return Indent.getContinuationIndent()
		}
		if (parentType === SquirrelTokenTypes.TERNARY_EXPRESSION && (elementType === SquirrelTokenTypes.COLON || elementType === SquirrelTokenTypes.QUESTION || prevSiblingType === SquirrelTokenTypes.COLON || prevSiblingType === SquirrelTokenTypes.QUESTION)) {
			return Indent.getContinuationIndent()
		}
		if (elementType === SquirrelTokenTypes.CALL_EXPRESSION) {
			if (FormatterUtil.isPrecededBy(node, SquirrelTokenTypes.ASSIGNMENT_OPERATOR)) {
				return Indent.getContinuationIndent()
			}
		}
		if ((elementType === SquirrelTokenTypes.REFERENCE_EXPRESSION || SquirrelTokenTypesSets.BINARY_EXPRESSIONS.contains(elementType)) &&
			(FormatterUtil.isPrecededBy(node, SquirrelTokenTypes.ASSIGNMENT_OPERATOR) || FormatterUtil.isPrecededBy(node, SquirrelTokenTypes.EQ))
		) {
			return Indent.getContinuationIndent()
		}
		if (elementType === SquirrelTokenTypes.DOT || prevSiblingType === SquirrelTokenTypes.DOT) {
			return Indent.getContinuationIndent()
		}

		// --- Statements
		if (parentType === SquirrelTokenTypes.ENUM_DECLARATION && SquirrelFormatterUtil.isBetweenBraces(node)) {
			return Indent.getNormalIndent()
		}
		if (parentType === SquirrelTokenTypes.TABLE_EXPRESSION && SquirrelFormatterUtil.isBetweenBraces(node)) {
			return Indent.getNormalIndent()
		}
		if (parentType === SquirrelTokenTypes.FUNCTION_BODY && SquirrelFormatterUtil.isSimpleStatement(node)) {
			return Indent.getNormalIndent()
		}

		if (elementType === SquirrelTokenTypes.LPAREN && parentType === SquirrelTokenTypes.ARGUMENTS) {
			return Indent.getContinuationWithoutFirstIndent()
		}
		if (elementType === SquirrelTokenTypes.RPAREN && parentType === SquirrelTokenTypes.ARGUMENTS) {
			if (prevSiblingType === SquirrelTokenTypes.ARGUMENT_LIST) {
				val childs = prevSibling!!.getChildren(null)
				val n = childs.size
				if (n > 2 && childs[n - 1] is PsiErrorElement && childs[n - 2].elementType === SquirrelTokenTypes.COMMA) {
					return Indent.getContinuationWithoutFirstIndent()
				}
			}
		}
		if (parentType === SquirrelTokenTypes.ARGUMENT_LIST) {
			return Indent.getContinuationWithoutFirstIndent()
		}
		if (parentType === SquirrelTokenTypes.PARAMETER_LIST) {
			return Indent.getContinuationWithoutFirstIndent()
		}

		if (elementType === SquirrelTokenTypes.CLASS_MEMBER) {
			return Indent.getNormalIndent()
		}

		if (parentType === SquirrelTokenTypes.FOR_STATEMENT && SquirrelFormatterUtil.isSimpleStatement(node) && prevSiblingType === SquirrelTokenTypes.RPAREN) {
			return Indent.getNormalIndent()
		}
		if (parentType === SquirrelTokenTypes.FOREACH_STATEMENT && SquirrelFormatterUtil.isSimpleStatement(node) && prevSiblingType === SquirrelTokenTypes.RPAREN) {
			return Indent.getNormalIndent()
		}
		if (parentType === SquirrelTokenTypes.WHILE_STATEMENT && SquirrelFormatterUtil.isSimpleStatement(node) && prevSiblingType === SquirrelTokenTypes.RPAREN) {
			return Indent.getNormalIndent()
		}
		if (parentType === SquirrelTokenTypes.DO_WHILE_STATEMENT && SquirrelFormatterUtil.isSimpleStatement(node) && prevSiblingType === SquirrelTokenTypes.DO) {
			return Indent.getNormalIndent()
		}
		if (parentType === SquirrelTokenTypes.SWITCH_STATEMENT && (elementType === SquirrelTokenTypes.SWITCH_CASE || elementType === SquirrelTokenTypes.DEFAULT_CASE)) {
			return Indent.getNormalIndent()
		}
		if (SquirrelTokenTypesSets.STATEMENTS.contains(elementType) && (parentType === SquirrelTokenTypes.SWITCH_CASE || parentType === SquirrelTokenTypes.DEFAULT_CASE)) {
			return Indent.getNormalIndent()
		}
		if (parentType === SquirrelTokenTypes.IF_STATEMENT && SquirrelFormatterUtil.isSimpleStatement(node) && (prevSiblingType === SquirrelTokenTypes.RPAREN || prevSiblingType === SquirrelTokenTypes.ELSE)) {
			return Indent.getNormalIndent()
		}
		if (parentType === SquirrelTokenTypes.TRY_STATEMENT && SquirrelFormatterUtil.isSimpleStatement(node) && prevSiblingType === SquirrelTokenTypes.TRY) {
			return Indent.getNormalIndent()
		}
		if (parentType === SquirrelTokenTypes.CATCH_PART && SquirrelFormatterUtil.isSimpleStatement(node) && prevSiblingType === SquirrelTokenTypes.RPAREN) {
			return Indent.getNormalIndent()
		}
		return Indent.getNoneIndent()
	}
}