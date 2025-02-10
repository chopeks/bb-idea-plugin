package com.chopeks.highlighting

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey

object SquirrelSyntaxHighlightingColors {
	val SINGLE_LINE_COMMENT: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
	val MULTI_LINE_COMMENT: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
	val MULTI_LINE_DOC_COMMENT: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_DOC_COMMENT", DefaultLanguageHighlighterColors.DOC_COMMENT)
	val KEYWORD: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
	val STRING: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_STRING", DefaultLanguageHighlighterColors.STRING)
	val NUMBER: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
	val BRACKETS: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_BRACKET", DefaultLanguageHighlighterColors.BRACKETS)
	val BRACES: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_BRACES", DefaultLanguageHighlighterColors.BRACES)
	val PARENTHESES: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
	val CLASS_ATTRIBUTES: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_CLASS_ATTRIBUTES", DefaultLanguageHighlighterColors.METADATA)
	val OPERATOR: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
	val IDENTIFIER: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
	val DOT: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_DOT", DefaultLanguageHighlighterColors.DOT)
	val SEMICOLON: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
	val COLON: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_COLON", HighlighterColors.TEXT)
	val COMMA: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_COMMA", DefaultLanguageHighlighterColors.COMMA)
	val BAD_CHARACTER: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_BAD_TOKEN", HighlighterColors.BAD_CHARACTER)
	val CLASS_NAME: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_CLASS_NAME", DefaultLanguageHighlighterColors.CLASS_NAME)
	val FUNCTION_CALL: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_FUNCTION_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL)
	val STRING_LINK: TextAttributesKey =
		TextAttributesKey.createTextAttributesKey("SQ_STRING_LINK", DefaultLanguageHighlighterColors.STRING)
}
