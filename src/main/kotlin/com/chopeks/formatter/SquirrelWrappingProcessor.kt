package com.chopeks.formatter

import com.chopeks.SquirrelTokenTypes
import com.chopeks.SquirrelTokenTypesSets
import com.chopeks.formatter.settings.SquirrelCodeStyleSettings
import com.intellij.formatting.Wrap
import com.intellij.formatting.WrapType
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.formatter.WrappingUtil

object SquirrelWrappingProcessor {
	private val SQUIRREL_TERNARY_EXPRESSION_WRAP_KEY = Key.create<Wrap>("TERNARY_EXPRESSION_WRAP_KEY")
	private val SQUIRREL_EXPRESSION_LIST_WRAP_KEY = Key.create<Wrap>("EXPRESSION_LIST_WRAP_KEY")
	private val SQUIRREL_ARGUMENT_LIST_WRAP_KEY = Key.create<Wrap>("ARGUMENT_LIST_WRAP_KEY")
	private val SQUIRREL_TYPE_LIST_WRAP_KEY = Key.create<Wrap>("TYPE_LIST_WRAP_KEY")

	@JvmStatic
	fun createChildWrap(child: ASTNode, defaultWrap: Wrap, myNode: ASTNode, cmSettings: CommonCodeStyleSettings, sqSettings: SquirrelCodeStyleSettings?): Wrap {
		val childType = child.elementType
		val elementType = myNode.elementType
		if (childType === SquirrelTokenTypes.COMMA || childType === SquirrelTokenTypes.SEMICOLON) return defaultWrap

		if (elementType === SquirrelTokenTypes.IF_STATEMENT && childType === SquirrelTokenTypes.ELSE) {
			return createWrap(cmSettings.ELSE_ON_NEW_LINE)
		}
		if (elementType === SquirrelTokenTypes.DO_WHILE_STATEMENT && childType === SquirrelTokenTypes.WHILE) {
			return createWrap(cmSettings.WHILE_ON_NEW_LINE)
		}
		if (elementType === SquirrelTokenTypes.TRY_STATEMENT && childType === SquirrelTokenTypes.CATCH_PART) {
			return createWrap(cmSettings.CATCH_ON_NEW_LINE)
		}

		/*
        if (elementType == ENUM_DECLARATION) {
            if (childType == ENUM_ITEM && cmSettings.ENUM_CONSTANTS_WRAP != DO_NOT_WRAP) {
                if (myNode.getFirstChildNode() == child) {
                    return createWrap(sqSettings.ENUM_LBRACE_ON_NEXT_LINE);
                }
                if (childType == RBRACE) {
                    return createWrap(sqSettings.ENUM_RBRACE_ON_NEXT_LINE);
                }
                return Wrap.createWrap(WrappingUtil.getWrapType(cmSettings.ENUM_CONSTANTS_WRAP), true);
            }
        }

        if (elementType == TABLE_EXPRESSION) {
            if (childType == TABLE_ITEM && sqSettings.TABLE_WRAP != DO_NOT_WRAP) {
                if (myNode.getFirstChildNode() == child) {
                    return createWrap(sqSettings.TABLE_LBRACE_ON_NEXT_LINE);
                }
                if (childType == RBRACE) {
                    return createWrap(sqSettings.TABLE_RBRACE_ON_NEXT_LINE);
                }
                return Wrap.createWrap(WrappingUtil.getWrapType(sqSettings.TABLE_WRAP), true);
            }
        }

        if (elementType == CLASS_ATTRIBUTE) {
            if (childType == TABLE_ITEM && sqSettings.CLASS_ATTRIBUTES_WRAP != DO_NOT_WRAP) {
                if (myNode.getFirstChildNode() == child) {
                    return createWrap(sqSettings.CLASS_ATTRIBUTE_LBRACE_ON_NEXT_LINE);
                }
                if (childType == RBRACE) {
                    return createWrap(sqSettings.CLASS_ATTRIBUTE_RBRACE_ON_NEXT_LINE);
                }
                return Wrap.createWrap(WrappingUtil.getWrapType(sqSettings.CLASS_ATTRIBUTES_WRAP), true);
            }
        }*/
		if (elementType === SquirrelTokenTypes.PARAMETER_LIST) {
			if (cmSettings.METHOD_PARAMETERS_WRAP != CommonCodeStyleSettings.DO_NOT_WRAP) {
				if (myNode.firstChildNode === child) {
					return createWrap(cmSettings.METHOD_PARAMETERS_LPAREN_ON_NEXT_LINE)
				}
				if (childType === SquirrelTokenTypes.RPAREN) {
					return createWrap(cmSettings.METHOD_PARAMETERS_RPAREN_ON_NEXT_LINE)
				}
				return Wrap.createWrap(WrappingUtil.getWrapType(cmSettings.METHOD_PARAMETERS_WRAP), true)
			}
		}

		if (elementType === SquirrelTokenTypes.ARGUMENT_LIST) {
			if (cmSettings.CALL_PARAMETERS_WRAP != CommonCodeStyleSettings.DO_NOT_WRAP) {
				val wrap: Wrap?
				// First, do persistent object management.
				if (child === myNode.firstChildNode) {
					val childs = myNode.getChildren(SquirrelTokenTypesSets.EXPRESSIONS)
					wrap = if (childs.size >= 7) { // Approximation; SQUIRREL_style uses dynamic programming with cost-based analysis to choose.
						Wrap.createWrap(WrapType.ALWAYS, true)
					} else {
						Wrap.createWrap(WrapType.NORMAL, true) // NORMAL,CHOP_DOWN_IF_LONG
					}
					if (myNode.lastChildNode !== child) {
						myNode.putUserData(SQUIRREL_ARGUMENT_LIST_WRAP_KEY, wrap)
					}
				} else {
					wrap = myNode.getUserData(SQUIRREL_ARGUMENT_LIST_WRAP_KEY)

					if (myNode.lastChildNode === child) {
						myNode.putUserData(SQUIRREL_ARGUMENT_LIST_WRAP_KEY, null)
					}
				}
				// Second, decide what object to return.
				if (childType === SquirrelTokenTypes.MULTI_LINE_COMMENT || childType === SquirrelTokenTypes.FUNCTION_EXPRESSION) {
					return Wrap.createWrap(WrapType.NONE, false)
				}
				return wrap ?: Wrap.createWrap(WrappingUtil.getWrapType(cmSettings.CALL_PARAMETERS_WRAP), false)
			}
		}

		return defaultWrap
	}

	private fun createWrap(isNormal: Boolean): Wrap {
		return Wrap.createWrap(if (isNormal) WrapType.NORMAL else WrapType.NONE, true)
	}
}