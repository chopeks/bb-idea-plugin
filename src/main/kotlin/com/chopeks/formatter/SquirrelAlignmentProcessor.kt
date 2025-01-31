package com.chopeks.formatter

import com.chopeks.SquirrelTokenTypes
import com.chopeks.SquirrelTokenTypesSets
import com.chopeks.formatter.settings.SquirrelCodeStyleSettings
import com.intellij.formatting.Alignment
import com.intellij.lang.ASTNode
import com.intellij.psi.codeStyle.CommonCodeStyleSettings

object SquirrelAlignmentProcessor {
	fun createChildAlignment(child: ASTNode?, myNode: ASTNode, cmSettings: CommonCodeStyleSettings, sqSettings: SquirrelCodeStyleSettings?): Alignment? {
		val elementType = myNode.elementType
		val myBaseAlignment = Alignment.createAlignment()

		if (SquirrelTokenTypesSets.BINARY_EXPRESSIONS.contains(elementType) && cmSettings.ALIGN_MULTILINE_BINARY_OPERATION) {
			return myBaseAlignment
		}

		if (elementType === SquirrelTokenTypes.TERNARY_EXPRESSION && cmSettings.ALIGN_MULTILINE_TERNARY_OPERATION) {
			return myBaseAlignment
		}

		if (elementType === SquirrelTokenTypes.PARAMETER_LIST) {
			if (cmSettings.ALIGN_MULTILINE_PARAMETERS) {
				return myBaseAlignment
			}
		}
		if (elementType === SquirrelTokenTypes.ARGUMENTS) {
			if (cmSettings.ALIGN_MULTILINE_PARAMETERS_IN_CALLS) {
				return myBaseAlignment
			}
		}

		return null
	}
}
