package com.chopeks.highlighting

import com.chopeks.SquirrelTokenTypes
import com.chopeks.SquirrelTokenTypesSets
import com.chopeks.lexer.SquirrelLexer
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class SquirrelSyntaxHighlighter : SyntaxHighlighterBase() {
	override fun getHighlightingLexer(): Lexer {
		return SquirrelLexer()
	}

	override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
		return pack(ATTRIBUTES[tokenType])
	}

	companion object {
		private val ATTRIBUTES: MutableMap<IElementType, TextAttributesKey> = mutableMapOf()

		init {
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.SINGLE_LINE_COMMENT, SquirrelTokenTypes.SINGLE_LINE_COMMENT)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.MULTI_LINE_COMMENT, SquirrelTokenTypes.MULTI_LINE_COMMENT)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.MULTI_LINE_DOC_COMMENT, SquirrelTokenTypesSets.MULTI_LINE_DOC_COMMENT)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.PARENTHESES, SquirrelTokenTypes.LPAREN, SquirrelTokenTypes.RPAREN)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.BRACES, SquirrelTokenTypes.LBRACE, SquirrelTokenTypes.RBRACE)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.BRACKETS, SquirrelTokenTypes.LBRACKET, SquirrelTokenTypes.RBRACKET)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.CLASS_ATTRIBUTES, SquirrelTokenTypes.CLASS_ATTR_START, SquirrelTokenTypes.CLASS_ATTR_END)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.BAD_CHARACTER, TokenType.BAD_CHARACTER)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.IDENTIFIER, SquirrelTokenTypes.IDENTIFIER)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.COLON, SquirrelTokenTypes.COLON)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.SEMICOLON, SquirrelTokenTypes.SEMICOLON)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.COMMA, SquirrelTokenTypes.COMMA)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.DOT, SquirrelTokenTypes.DOT)
			fillMap(ATTRIBUTES, SquirrelTokenTypesSets.OPERATORS, SquirrelSyntaxHighlightingColors.OPERATOR)
			fillMap(ATTRIBUTES, SquirrelTokenTypesSets.KEYWORDS, SquirrelSyntaxHighlightingColors.KEYWORD)
			fillMap(ATTRIBUTES, SquirrelTokenTypesSets.NUMBERS, SquirrelSyntaxHighlightingColors.NUMBER)
			fillMap(ATTRIBUTES, SquirrelTokenTypesSets.STRING_LITERALS, SquirrelSyntaxHighlightingColors.STRING)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.DIRECTIVE, SquirrelTokenTypes.INCLUDE_DIRECTIVE)
			fillMap(ATTRIBUTES, SquirrelSyntaxHighlightingColors.ONCE, SquirrelTokenTypes.ONCE)
		}
	}
}
