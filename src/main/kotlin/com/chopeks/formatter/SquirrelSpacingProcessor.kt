package com.chopeks.formatter

import com.chopeks.SquirrelTokenTypes
import com.chopeks.SquirrelTokenTypesSets
import com.chopeks.formatter.settings.SquirrelCodeStyleSettings
import com.chopeks.util.SquirrelFormatterUtil
import com.intellij.formatting.Block
import com.intellij.formatting.Spacing
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.BraceStyleConstant
import com.intellij.psi.formatter.common.AbstractBlock
import java.util.*

class SquirrelSpacingProcessor(private val child1: Block?, private val child2: Block, private val myNode: ASTNode, private val cmSettings: CommonCodeStyleSettings, private val sqSettings: SquirrelCodeStyleSettings) {
	val spacing: Spacing?
		get() {
			if (child1 !is AbstractBlock || child2 !is AbstractBlock) {
				return null
			}

			val elementType = myNode.elementType
			val parent = myNode.treeParent
			val parentType = if (myNode.treeParent == null) null else myNode.treeParent.elementType
			val node1 = child1.node
			val node2 = child2.node
			val type1 = node1.elementType
			val type2 = node2.elementType

			// TODO Add dependent brace style
			// TODO add control statement on one line option
			// TODO wrap parameters
			// TODO wrap arguments
			// TODO wrap array initizliaer
			// TODO binary expressions wrap
			// TODO assignment expression sign
			// todo ternary expression wrap

			// @formatter:off
        if (SquirrelTokenTypesSets.BLOCKS_WITH_BRACES.contains(elementType)) {
            if (type1 === SquirrelTokenTypes.LBRACE || type2 === SquirrelTokenTypes.RBRACE) {
                return if (type1 === SquirrelTokenTypes.LBRACE && type2 === SquirrelTokenTypes.RBRACE) {
	                addSingleSpaceIf(sqSettings.SPACE_WITHIN_EMPTY_BRACES, getBraceSettingsForElement(parent) == CommonCodeStyleSettings.NEXT_LINE)
                }
                else if (SquirrelFormatterUtil.isSimpleBlock(myNode) && getKeepSimpleInOneLineSettingsForElement(parent)) {
	                Spacing.createDependentLFSpacing(if (cmSettings.SPACE_WITHIN_BRACES) 1 else 0, 1, myNode.textRange, false, 0)
                }
                else {
	                Spacing.createSpacing(0, 0, 1, cmSettings.KEEP_LINE_BREAKS, cmSettings.KEEP_BLANK_LINES_IN_CODE)
                }
            }
        }
        
        if ((SquirrelTokenTypesSets.STATEMENTS.contains(type1) && SquirrelTokenTypesSets.STATEMENTS.contains(type2)) || (type1 === SquirrelTokenTypes.SEMICOLON && SquirrelTokenTypesSets.STATEMENTS.contains(type2))) {
            return Spacing.createSpacing(0, 0, 1, cmSettings.KEEP_LINE_BREAKS, cmSettings.KEEP_BLANK_LINES_IN_CODE)
        }
        if (SquirrelTokenTypesSets.STATEMENTS.contains(type1) && type2 === SquirrelTokenTypes.SEMICOLON) {
            return noSpace()
        }
        
                // TODO: Add class members wrap
        // TODO: Wrap if class members wrap == always
        // Always add a new line between a class attributes and a class member.
        if (type1 === SquirrelTokenTypes.CLASS_ATTRIBUTE) {
            return Spacing.createSpacing(0, 0, 1, cmSettings.KEEP_LINE_BREAKS, cmSettings.KEEP_BLANK_LINES_IN_CODE)
        }
        
        if ((type1 === SquirrelTokenTypes.CLASS_MEMBER && type2 === SquirrelTokenTypes.CLASS_MEMBER) || (type1 === SquirrelTokenTypes.SEMICOLON && type2 === SquirrelTokenTypes.CLASS_MEMBER)) {
            return Spacing.createSpacing(0, 0, 1, cmSettings.KEEP_LINE_BREAKS, cmSettings.KEEP_BLANK_LINES_IN_CODE)
        }
        if (type1 === SquirrelTokenTypes.CLASS_MEMBER && type2 === SquirrelTokenTypes.SEMICOLON) {
            return noSpace()
        }
        
                // TODO: Add table items wrap
        if (((type1 === SquirrelTokenTypes.TABLE_ITEM && type2 === SquirrelTokenTypes.TABLE_ITEM) || (type1 === SquirrelTokenTypes.COMMA && type2 === SquirrelTokenTypes.TABLE_ITEM)) && elementType !== SquirrelTokenTypes.CLASS_ATTRIBUTE) {
            return Spacing.createSpacing(0, 0, 1, cmSettings.KEEP_LINE_BREAKS, cmSettings.KEEP_BLANK_LINES_IN_CODE)
        }
        if (type1 === SquirrelTokenTypes.TABLE_ITEM && type2 === SquirrelTokenTypes.COMMA) {
            return addSingleSpaceIf(cmSettings.SPACE_BEFORE_COMMA)
        }
        
                // TODO: Add enum items wrap
        if ((type1 === SquirrelTokenTypes.ENUM_ITEM && type2 === SquirrelTokenTypes.ENUM_ITEM) || (type1 === SquirrelTokenTypes.COMMA && type2 === SquirrelTokenTypes.ENUM_ITEM)) {
            return Spacing.createSpacing(0, 0, 1, cmSettings.KEEP_LINE_BREAKS, cmSettings.KEEP_BLANK_LINES_IN_CODE)
        }
        if (type1 === SquirrelTokenTypes.ENUM_ITEM && type2 === SquirrelTokenTypes.COMMA) {
            return addSingleSpaceIf(cmSettings.SPACE_BEFORE_COMMA)
        }
        
        
                // TODO: test again when new lines in braces are ready.
        if (!SquirrelTokenTypesSets.COMMENTS.contains(type1) && SquirrelTokenTypesSets.FUNCTION_DEFINITION.contains(type2)) {
            return addLineBreak(2)
        }
        if (!SquirrelTokenTypesSets.COMMENTS.contains(type1) && type2 === SquirrelTokenTypes.CLASS_DECLARATION) {
            return addLineBreak(2)
        }
        
        
        
                // Spacing Before Parentheses
        // --------------------------
        if (type2 === SquirrelTokenTypes.ARGUMENTS && elementType === SquirrelTokenTypes.CALL_EXPRESSION) {
            return addSingleSpaceIf(cmSettings.SPACE_BEFORE_METHOD_CALL_PARENTHESES)
        }
        else if (type2 === SquirrelTokenTypes.PARAMETERS && SquirrelTokenTypesSets.FUNCTION_DEFINITION.contains(elementType)) {
            return addSingleSpaceIf(cmSettings.SPACE_BEFORE_METHOD_PARENTHESES)
        }
        else if (type2 === SquirrelTokenTypes.PARAMETERS && elementType === SquirrelTokenTypes.FUNCTION_EXPRESSION) {
            return addSingleSpaceIf(sqSettings.SPACE_BEFORE_FUNCTION_EXPRESSION_PARENTHESES)
        }
        else if (type2 === SquirrelTokenTypes.PARAMETERS && elementType === SquirrelTokenTypes.LAMBDA_FUNCTION_EXPRESSION) {
            return noSpace()
        }
        else if (type2 === SquirrelTokenTypes.LPAREN) {
            if (elementType === SquirrelTokenTypes.IF_STATEMENT) {
                return addSingleSpaceIf(cmSettings.SPACE_BEFORE_IF_PARENTHESES)
            }
            else if (elementType === SquirrelTokenTypes.FOR_STATEMENT) {
                return addSingleSpaceIf(cmSettings.SPACE_BEFORE_FOR_PARENTHESES)
            }
            else if (elementType === SquirrelTokenTypes.FOREACH_STATEMENT) {
                return addSingleSpaceIf(sqSettings.SPACE_BEFORE_FOREACH_PARENTHESES)
            }
            else if (elementType === SquirrelTokenTypes.WHILE_STATEMENT || elementType === SquirrelTokenTypes.DO_WHILE_STATEMENT) {
                return addSingleSpaceIf(cmSettings.SPACE_BEFORE_WHILE_PARENTHESES)
            }
            else if (elementType === SquirrelTokenTypes.SWITCH_STATEMENT) {
                return addSingleSpaceIf(cmSettings.SPACE_BEFORE_SWITCH_PARENTHESES)
            }
            else if (elementType === SquirrelTokenTypes.CATCH_PART) {
                return addSingleSpaceIf(cmSettings.SPACE_BEFORE_CATCH_PARENTHESES)
            }
        }
        
                // Spacing Around Operators
        // ------------------------
        if (Arrays.asList(SquirrelTokenTypes.CONST_DECLARATION, SquirrelTokenTypes.ENUM_ITEM, SquirrelTokenTypes.DEFAULT_PARAMETER, SquirrelTokenTypes.TABLE_ITEM, SquirrelTokenTypes.CLASS_MEMBER).contains(elementType) && (type1 === SquirrelTokenTypes.EQ || type2 === SquirrelTokenTypes.EQ)) {
            return addSingleSpaceIf(cmSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
        }
        else if ((type1 === SquirrelTokenTypes.EQ && elementType === SquirrelTokenTypes.VAR_INIT) || type2 === SquirrelTokenTypes.VAR_INIT) {
            return addSingleSpaceIf(cmSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
        }
        else if ((type1 === SquirrelTokenTypes.ASSIGNMENT_OPERATOR || type2 === SquirrelTokenTypes.ASSIGNMENT_OPERATOR) && elementType === 
        SquirrelTokenTypes.ASSIGN_EXPRESSION) {
            return addSingleSpaceIf(cmSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
        }
        else if (SquirrelTokenTypesSets.LOGIC_OPERATORS.contains(type1) || SquirrelTokenTypesSets.LOGIC_OPERATORS.contains(type2)) {
            return addSingleSpaceIf(cmSettings.SPACE_AROUND_LOGICAL_OPERATORS)
        }
        else if (type1 === SquirrelTokenTypes.EQUALITY_OPERATOR || type2 === SquirrelTokenTypes.EQUALITY_OPERATOR) {
            return addSingleSpaceIf(cmSettings.SPACE_AROUND_EQUALITY_OPERATORS)
        }
        else if (type1 === SquirrelTokenTypes.RELATIONAL_OPERATOR || type2 === SquirrelTokenTypes.RELATIONAL_OPERATOR) {
            return addSingleSpaceIf(cmSettings.SPACE_AROUND_RELATIONAL_OPERATORS)
        }
        else if (SquirrelTokenTypesSets.BITWISE_OPERATORS.contains(type1) || SquirrelTokenTypesSets.BITWISE_OPERATORS.contains(type2)) {
            return addSingleSpaceIf(cmSettings.SPACE_AROUND_BITWISE_OPERATORS)
        }
        else if ((type1 === SquirrelTokenTypes.ADDITIVE_OPERATOR || type2 === SquirrelTokenTypes.ADDITIVE_OPERATOR) && elementType !== SquirrelTokenTypes.PREFIX_EXPRESSION && elementType !== SquirrelTokenTypes.UNARY_EXPRESSION) {
            return addSingleSpaceIf(cmSettings.SPACE_AROUND_ADDITIVE_OPERATORS)
        }
        else if (type1 === SquirrelTokenTypes.MULTIPLICATIVE_OPERATOR || type2 === SquirrelTokenTypes.MULTIPLICATIVE_OPERATOR) {
            return addSingleSpaceIf(cmSettings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS)
        }
        else if (type1 === SquirrelTokenTypes.SHIFT_OPERATOR || type2 === SquirrelTokenTypes.SHIFT_OPERATOR) {
            return addSingleSpaceIf(cmSettings.SPACE_AROUND_SHIFT_OPERATORS)
        }
        else if (type1 === SquirrelTokenTypes.PREFIX_OPERATOR || type2 === SquirrelTokenTypes.PREFIX_OPERATOR) {
            return addSingleSpaceIf(cmSettings.SPACE_AROUND_UNARY_OPERATOR)
        }
        else if (type1 === SquirrelTokenTypes.UNARY_OPERATOR) {
            return addSingleSpaceIf(cmSettings.SPACE_AROUND_UNARY_OPERATOR)
        }
        
                // Spacing Before Left Brace
        // -------------------------
        if ((elementType === SquirrelTokenTypes.CLASS_DECLARATION || elementType === SquirrelTokenTypes.CLASS_EXPRESSION) && type2 === SquirrelTokenTypes.CLASS_BODY) {
            return setBraceSpace(cmSettings.SPACE_BEFORE_CLASS_LBRACE, cmSettings.CLASS_BRACE_STYLE, child1.getTextRange(), node2)
        }
        else if (elementType === SquirrelTokenTypes.ENUM_DECLARATION && type2 === SquirrelTokenTypes.LBRACE) {
            return setBraceSpace(cmSettings.SPACE_BEFORE_CLASS_LBRACE, cmSettings.BRACE_STYLE, child1.getTextRange(), node2)
        }
        else if ((SquirrelTokenTypesSets.FUNCTION_DEFINITION.contains(elementType) || elementType === SquirrelTokenTypes.FUNCTION_EXPRESSION) && type2 === SquirrelTokenTypes.FUNCTION_BODY) {
            return setBraceSpace(cmSettings.SPACE_BEFORE_METHOD_LBRACE, cmSettings.METHOD_BRACE_STYLE, child1.getTextRange(), node2)
        }
        else if (elementType === SquirrelTokenTypes.LAMBDA_FUNCTION_EXPRESSION && type1 === SquirrelTokenTypes.PARAMETERS) {
            return setBraceSpace(cmSettings.SPACE_BEFORE_METHOD_LBRACE, cmSettings.METHOD_BRACE_STYLE, child1.getTextRange(), node2)
        }
        else if (elementType === SquirrelTokenTypes.IF_STATEMENT && type1 === SquirrelTokenTypes.RPAREN) {
            return setBraceSpace(cmSettings.SPACE_BEFORE_IF_LBRACE, cmSettings.BRACE_STYLE, child1.getTextRange(), node2)
        }
        else if (elementType === SquirrelTokenTypes.IF_STATEMENT && type1 === SquirrelTokenTypes.ELSE) {
            if (type2 === SquirrelTokenTypes.IF_STATEMENT) {
                return Spacing.createSpacing(1, 1, if (cmSettings.SPECIAL_ELSE_IF_TREATMENT) 0 else 1, false, cmSettings.KEEP_BLANK_LINES_IN_CODE)
            }
            return setBraceSpace(cmSettings.SPACE_BEFORE_ELSE_LBRACE, cmSettings.BRACE_STYLE, child1.getTextRange(), node2)
        }
        else if (elementType === SquirrelTokenTypes.SWITCH_STATEMENT && type1 === SquirrelTokenTypes.RPAREN) {
            return setBraceSpace(cmSettings.SPACE_BEFORE_SWITCH_LBRACE, cmSettings.BRACE_STYLE, child1.getTextRange(), node2)
        }
        else if (elementType === SquirrelTokenTypes.WHILE_STATEMENT && type1 === SquirrelTokenTypes.RPAREN) {
            return setBraceSpace(cmSettings.SPACE_BEFORE_WHILE_LBRACE, cmSettings.BRACE_STYLE, child1.getTextRange(), node2)
        }
        else if (elementType === SquirrelTokenTypes.DO_WHILE_STATEMENT && type1 === SquirrelTokenTypes.DO) {
            return setBraceSpace(cmSettings.SPACE_BEFORE_DO_LBRACE, cmSettings.BRACE_STYLE, child1.getTextRange(), node2)
        }
        else if (elementType === SquirrelTokenTypes.FOR_STATEMENT && type1 === SquirrelTokenTypes.RPAREN) {
            return setBraceSpace(cmSettings.SPACE_BEFORE_FOR_LBRACE, cmSettings.BRACE_STYLE, child1.getTextRange(), node2)
        }
        else if (elementType === SquirrelTokenTypes.FOREACH_STATEMENT && type1 === SquirrelTokenTypes.RPAREN) {
            return setBraceSpace(sqSettings.SPACE_BEFORE_FOREACH_LBRACE, cmSettings.BRACE_STYLE, child1.getTextRange(), node2)
        }
        else if (elementType === SquirrelTokenTypes.TRY_STATEMENT && type1 === SquirrelTokenTypes.TRY) {
            return setBraceSpace(cmSettings.SPACE_BEFORE_TRY_LBRACE, cmSettings.BRACE_STYLE, child1.getTextRange(), node2)
        }
        else if (elementType === SquirrelTokenTypes.CATCH_PART && type1 === SquirrelTokenTypes.RPAREN) {
            return setBraceSpace(cmSettings.SPACE_BEFORE_CATCH_LBRACE, cmSettings.BRACE_STYLE, child1.getTextRange(), node2)
        }
        
                // Spacing Before Keywords
        // -----------------------
        if (type2 === SquirrelTokenTypes.ELSE) {
            return addSingleSpaceIf(cmSettings.SPACE_BEFORE_ELSE_KEYWORD, cmSettings.ELSE_ON_NEW_LINE, SquirrelFormatterUtil.isSimpleStatement(node1))
        }
        else if (type2 === SquirrelTokenTypes.WHILE) {
            return addSingleSpaceIf(cmSettings.SPACE_BEFORE_WHILE_KEYWORD, cmSettings.WHILE_ON_NEW_LINE, SquirrelFormatterUtil.isSimpleStatement(node1))
        }
        else if (type2 === SquirrelTokenTypes.CATCH_PART) {
            return addSingleSpaceIf(cmSettings.SPACE_BEFORE_CATCH_KEYWORD, cmSettings.CATCH_ON_NEW_LINE, SquirrelFormatterUtil.isSimpleStatement(node1))
        }
        
                // Spacing Within
        // --------------
        if (type1 === SquirrelTokenTypes.LBRACKET || type2 === SquirrelTokenTypes.RBRACKET) {
            return if (type1 === SquirrelTokenTypes.LBRACKET && type2 === SquirrelTokenTypes.RBRACKET) {
	            addSingleSpaceIf(sqSettings.SPACE_WITHIN_EMPTY_BRACKETS)
            }
            else {
	            addSingleSpaceIf(cmSettings.SPACE_WITHIN_BRACKETS)
            }
        }
        else if (type1 === SquirrelTokenTypes.CLASS_ATTR_START || type2 === SquirrelTokenTypes.CLASS_ATTR_END) {
            return if (type1 === SquirrelTokenTypes.CLASS_ATTR_START && type2 === SquirrelTokenTypes.CLASS_ATTR_END) {
	            addSingleSpaceIf(sqSettings.SPACE_WITHIN_EMPTY_BRACES)
            }
            else {
	            oneSpace()
            }
        }
        else if (type1 === SquirrelTokenTypes.LPAREN || type2 === SquirrelTokenTypes.RPAREN) {
            if (elementType === SquirrelTokenTypes.IF_STATEMENT) {
                return addSingleSpaceIf(cmSettings.SPACE_WITHIN_IF_PARENTHESES)
            }
            else if (elementType === SquirrelTokenTypes.FOR_STATEMENT) {
                return addSingleSpaceIf(cmSettings.SPACE_WITHIN_FOR_PARENTHESES)
            }
            else if (elementType === SquirrelTokenTypes.FOREACH_STATEMENT) {
                return addSingleSpaceIf(sqSettings.SPACE_WITHIN_FOREACH_PARENTHESES)
            }
            else if (elementType === SquirrelTokenTypes.WHILE_STATEMENT || elementType === SquirrelTokenTypes.DO_WHILE_STATEMENT) {
                return addSingleSpaceIf(cmSettings.SPACE_WITHIN_WHILE_PARENTHESES)
            }
            else if (elementType === SquirrelTokenTypes.SWITCH_STATEMENT) {
                return addSingleSpaceIf(cmSettings.SPACE_WITHIN_SWITCH_PARENTHESES)
            }
            else if (elementType === SquirrelTokenTypes.CATCH_PART) {
                return addSingleSpaceIf(cmSettings.SPACE_WITHIN_CATCH_PARENTHESES)
            }
            else if (elementType === SquirrelTokenTypes.PARAMETERS) {
                if (type1 === SquirrelTokenTypes.LPAREN && type2 === SquirrelTokenTypes.RPAREN) {
                    return addSingleSpaceIf(cmSettings.SPACE_WITHIN_EMPTY_METHOD_PARENTHESES)
                }
                else {
                    val newLineNeeded = if (type1 === SquirrelTokenTypes.LPAREN) cmSettings
                    .METHOD_PARAMETERS_LPAREN_ON_NEXT_LINE else cmSettings
                    .METHOD_PARAMETERS_RPAREN_ON_NEXT_LINE
                    
                    if (newLineNeeded || cmSettings.SPACE_WITHIN_METHOD_PARENTHESES) {
                        return addSingleSpaceIf(cmSettings.SPACE_WITHIN_METHOD_PARENTHESES, newLineNeeded)
                    }
                    return Spacing.createSpacing(0, 0, 0, false, 0)
                }
            }
            else if (elementType === SquirrelTokenTypes.ARGUMENTS) {
                if (type1 === SquirrelTokenTypes.LPAREN && type2 === SquirrelTokenTypes.RPAREN) {
                    return addSingleSpaceIf(cmSettings.SPACE_WITHIN_EMPTY_METHOD_CALL_PARENTHESES)
                }
                else {
                    val newLineNeeded = 
                    if (type1 === SquirrelTokenTypes.LPAREN) cmSettings.CALL_PARAMETERS_LPAREN_ON_NEXT_LINE else cmSettings
                    .CALL_PARAMETERS_RPAREN_ON_NEXT_LINE
                    
                    return addSingleSpaceIf(cmSettings.SPACE_WITHIN_METHOD_CALL_PARENTHESES, newLineNeeded)
                }
            }
            else if (elementType === SquirrelTokenTypes.PARENTHESIZED_EXPRESSION) {
                val newLineNeeded = 
                if (type1 === SquirrelTokenTypes.LPAREN) cmSettings.PARENTHESES_EXPRESSION_LPAREN_WRAP else cmSettings
                .PARENTHESES_EXPRESSION_RPAREN_WRAP
                return addSingleSpaceIf(cmSettings.SPACE_WITHIN_PARENTHESES, newLineNeeded)
            }
        }
        
                // Spacing In Ternary Operator
        // ---------------------------
        if (elementType === SquirrelTokenTypes.TERNARY_EXPRESSION) {
            if (type2 === SquirrelTokenTypes.QUESTION) {
                return addSingleSpaceIf(cmSettings.SPACE_BEFORE_QUEST)
            }
            else if (type2 === SquirrelTokenTypes.COLON) {
                return addSingleSpaceIf(cmSettings.SPACE_BEFORE_COLON)
            }
            else if (type1 === SquirrelTokenTypes.QUESTION) {
                return addSingleSpaceIf(cmSettings.SPACE_AFTER_QUEST)
            }
            else if (type1 === SquirrelTokenTypes.COLON) {
                return addSingleSpaceIf(cmSettings.SPACE_AFTER_COLON)
            }
        }
        
                // Spacing Other
        // -------------

        // COMMAS
        if (type2 === SquirrelTokenTypes.COMMA) {
            return addSingleSpaceIf(cmSettings.SPACE_BEFORE_COMMA)
        }
        else if (type1 === SquirrelTokenTypes.COMMA) {
            return addSingleSpaceIf(cmSettings.SPACE_AFTER_COMMA)
        }
        else if ((elementType === SquirrelTokenTypes.FOR_LOOP_PARTS || elementType === SquirrelTokenTypes.CLASS_BODY) && type2 === SquirrelTokenTypes.SEMICOLON) {
            return addSingleSpaceIf(cmSettings.SPACE_BEFORE_SEMICOLON)
        }
        else if ((elementType === SquirrelTokenTypes.FOR_LOOP_PARTS || elementType === SquirrelTokenTypes.CLASS_BODY) && type1 === SquirrelTokenTypes.SEMICOLON) {
            return addSingleSpaceIf(cmSettings.SPACE_AFTER_SEMICOLON)
        }
        else if (type2 === SquirrelTokenTypes.SEMICOLON) {
            return Spacing.createSpacing(0, 0, 0, true, cmSettings.KEEP_BLANK_LINES_IN_CODE)
        }
        else if (type1 === SquirrelTokenTypes.TABLE_ITEM && type2 === SquirrelTokenTypes.TABLE_ITEM) {
            return oneSpace()
        }
        else if (type1 === SquirrelTokenTypes.CLASS_MEMBER && type2 === SquirrelTokenTypes.CLASS_MEMBER) {
            return oneSpace()
        }
        else if ((type1 === SquirrelTokenTypes.RETURN || type1 === SquirrelTokenTypes.YIELD || type1 === SquirrelTokenTypes.THROW) && !SquirrelTokenTypesSets.SEMICOLONS.contains(type2)) {
            return addSingleSpaceIf(true)
        }
        
        
                // Spacing Multiline
        // -----------------
        if (SquirrelTokenTypesSets.STATEMENTS.contains(elementType) && (parentType === SquirrelTokenTypes.SWITCH_CASE || parentType === SquirrelTokenTypes.DEFAULT_CASE)) {
            return Spacing.createSpacing(0, 0, 1, false, cmSettings.KEEP_BLANK_LINES_IN_CODE)
        }
                // No blank line before closing brace in switch statement.
        if (type2 === SquirrelTokenTypes.RBRACE && (type1 === SquirrelTokenTypes.SWITCH_CASE || type1 === SquirrelTokenTypes.DEFAULT_CASE)) {
            return Spacing.createSpacing(0, 0, 1, false, 0)
        }
                // No blank line before first statement of a case.
        if (type1 === SquirrelTokenTypes.COLON && (elementType === SquirrelTokenTypes.SWITCH_CASE || elementType === SquirrelTokenTypes.DEFAULT_CASE)) {
            return Spacing.createSpacing(0, 0, 1, false, 0)
        }
                // No blank line before first case of a switch.
        if (elementType === SquirrelTokenTypes.SWITCH_STATEMENT && type1 === SquirrelTokenTypes.LBRACE) {
            return Spacing.createSpacing(0, 0, 1, false, 0)
        }
        
        
                // @formatter:on
			return null
		}

	fun addLineBreak(): Spacing {
		return Spacing.createSpacing(0, 0, 1, false, cmSettings.KEEP_BLANK_LINES_IN_CODE)
	}

	fun addLineBreak(number: Int): Spacing {
		return Spacing.createSpacing(0, 0, number, false, cmSettings.KEEP_BLANK_LINES_IN_CODE)
	}

	@JvmOverloads
	fun addSingleSpaceIf(condition: Boolean, linesFeed: Boolean = false): Spacing {
		val spaces = if (condition) 1 else 0
		val lines = if (linesFeed) 1 else 0
		return Spacing.createSpacing(
			spaces, spaces, lines, cmSettings.KEEP_LINE_BREAKS, cmSettings
				.KEEP_BLANK_LINES_IN_CODE
		)
	}

	fun addSingleSpaceIf(condition: Boolean, linesFeed: Boolean, keepLineBreaks: Boolean): Spacing {
		val spaces = if (condition) 1 else 0
		val lines = if (linesFeed) 1 else 0
		return Spacing.createSpacing(
			spaces, spaces, lines, keepLineBreaks, if (keepLineBreaks) cmSettings
				.KEEP_BLANK_LINES_IN_CODE else 0
		)
	}

	fun noSpace(): Spacing {
		return Spacing.createSpacing(0, 0, 0, cmSettings.KEEP_LINE_BREAKS, 0)
	}

	fun oneSpace(): Spacing {
		return Spacing.createSpacing(1, 1, 0, cmSettings.KEEP_LINE_BREAKS, 0)
	}

	fun setBraceSpace(
		needSpaceSetting: Boolean,
		@BraceStyleConstant braceStyleSetting: Int,
		textRange: TextRange?, child2: ASTNode
	): Spacing {
		val spaces = if (needSpaceSetting) 1 else 0
		val parentType = child2.treeParent.elementType

		// Don't wrap simple block event if line breaks set to NEXT_LINE (unless there are manual line breaks)
		if (braceStyleSetting == CommonCodeStyleSettings.NEXT_LINE && SquirrelFormatterUtil.isSimpleBlock(child2) &&
			((parentType === SquirrelTokenTypes.FUNCTION_BODY && cmSettings.KEEP_SIMPLE_METHODS_IN_ONE_LINE) ||
					(parentType === SquirrelTokenTypes.CLASS_DECLARATION && cmSettings.KEEP_SIMPLE_CLASSES_IN_ONE_LINE) ||
					(parentType !== SquirrelTokenTypes.FUNCTION_BODY && parentType !== SquirrelTokenTypes.CLASS_DECLARATION && cmSettings.KEEP_SIMPLE_BLOCKS_IN_ONE_LINE))
		) {
			return Spacing.createSpacing(
				spaces, spaces, 0, cmSettings.KEEP_LINE_BREAKS,
				cmSettings.KEEP_BLANK_LINES_IN_CODE
			)
		} else if (braceStyleSetting == CommonCodeStyleSettings.NEXT_LINE_IF_WRAPPED && textRange != null) {
			return Spacing.createDependentLFSpacing(
				spaces, spaces, textRange, cmSettings.KEEP_LINE_BREAKS,
				cmSettings.KEEP_BLANK_LINES_IN_CODE
			)
		} else {
			val lineBreaks = if (braceStyleSetting == CommonCodeStyleSettings.END_OF_LINE || braceStyleSetting == CommonCodeStyleSettings.NEXT_LINE_IF_WRAPPED) 0 else 1
			return Spacing.createSpacing(spaces, spaces, lineBreaks, false, 0)
		}
	}

	fun getBraceSettingsForElement(element: ASTNode?): Int {
		if (element == null) return cmSettings.BRACE_STYLE
		val elementType = element.elementType
		return if (elementType === SquirrelTokenTypes.FUNCTION_BODY) cmSettings.METHOD_BRACE_STYLE
		else if (elementType === SquirrelTokenTypes.CLASS_DECLARATION) cmSettings.CLASS_BRACE_STYLE
		else cmSettings.BRACE_STYLE
	}

	fun getKeepSimpleInOneLineSettingsForElement(element: ASTNode?): Boolean {
		if (element == null) return false
		val elementType = element.elementType
		return if (elementType === SquirrelTokenTypes.FUNCTION_BODY) cmSettings.KEEP_SIMPLE_METHODS_IN_ONE_LINE
		else if (elementType === SquirrelTokenTypes.CLASS_DECLARATION) cmSettings.KEEP_SIMPLE_CLASSES_IN_ONE_LINE
		else cmSettings.KEEP_SIMPLE_BLOCKS_IN_ONE_LINE
	}
}
