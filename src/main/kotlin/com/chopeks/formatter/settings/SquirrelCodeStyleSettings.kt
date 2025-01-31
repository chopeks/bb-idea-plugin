package com.chopeks.formatter.settings

import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CustomCodeStyleSettings

class SquirrelCodeStyleSettings(container: CodeStyleSettings) : CustomCodeStyleSettings("SquirrelCodeStyleSettings", container) {
	var SPACE_BEFORE_FUNCTION_EXPRESSION_PARENTHESES: Boolean = false
	var SPACE_BEFORE_FOREACH_PARENTHESES: Boolean = true
	var SPACE_BEFORE_FOREACH_LBRACE: Boolean = true
	var SPACE_WITHIN_FOREACH_PARENTHESES: Boolean = false
	var SPACE_WITHIN_EMPTY_BRACES: Boolean = false
	var SPACE_WITHIN_EMPTY_BRACKETS: Boolean = false

//	var CLASS_ATTRIBUTES_WRAP: Int = DO_NOT_WRAP
//	var ENUM_WRAP: Int = WRAP_ALWAYS
//	var TABLE_WRAP: Int = DO_NOT_WRAP
//
//	var ENUM_LBRACE_ON_NEXT_LINE: Boolean = false
//	var ENUM_RBRACE_ON_NEXT_LINE: Boolean = true
//
//	var TABLE_LBRACE_ON_NEXT_LINE: Boolean = false
//	var TABLE_RBRACE_ON_NEXT_LINE: Boolean = false
//
//	var CLASS_ATTRIBUTE_LBRACE_ON_NEXT_LINE: Boolean = false
//	var CLASS_ATTRIBUTE_RBRACE_ON_NEXT_LINE: Boolean = false
}
