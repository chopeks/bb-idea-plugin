package com.chopeks

import com.chopeks.lexer.SquirrelDocLexer
import com.chopeks.psi.SquirrelElementType
import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilderFactory
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.ILazyParseableElementType
import com.intellij.psi.tree.TokenSet

object SquirrelTokenTypesSets {
	class SquirrelDocCommentElementType : ILazyParseableElementType("MULTI_LINE_DOC_COMMENT", SquirrelLanguage) {
		override fun parseContents(chameleon: ASTNode): ASTNode {
			val builder = PsiBuilderFactory.getInstance().createBuilder(
				chameleon.treeParent.psi.project,
				chameleon,
				SquirrelDocLexer(),
				language,
				chameleon.chars
			)
			doParse(builder)
			return builder.treeBuilt.firstChildNode
		}

		private fun doParse(builder: PsiBuilder) {
			val root = builder.mark()

			while (!builder.eof()) {
				builder.advanceLexer()
			}

			root.done(this)
		}
	}

	val SQUIRREL_FILE: IFileElementType = IFileElementType("SQUIRRELFILE", SquirrelLanguage)

	val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE
	val BAD_CHARACTER: IElementType = TokenType.BAD_CHARACTER

	// SquirrelLexer returns multiline comments as a single MULTI_LINE_COMMENT or MULTI_LINE_DOC_COMMENT
	// SquirrelDocLexer splits MULTI_LINE_DOC_COMMENT in tokens
	// can't appear in PSI because merged into MULTI_LINE_COMMENT
	@JvmField
	val MULTI_LINE_COMMENT_START: IElementType = SquirrelElementType("MULTI_LINE_COMMENT_START")
	@JvmField
	val MULTI_LINE_DOC_COMMENT_START: IElementType = SquirrelElementType("MULTI_LINE_DOC_COMMENT_START")
	@JvmField
	val MULTI_LINE_COMMENT_BODY: IElementType = SquirrelElementType("MULTI_LINE_COMMENT_BODY")
	@JvmField
	val DOC_COMMENT_LEADING_ASTERISK: IElementType = SquirrelElementType("DOC_COMMENT_LEADING_ASTERISK")
	@JvmField
	val MULTI_LINE_COMMENT_END: IElementType = SquirrelElementType("MULTI_LINE_COMMENT_END")

	val MULTI_LINE_DOC_COMMENT: IElementType = SquirrelDocCommentElementType()
	val WHITE_SPACES: TokenSet = TokenSet.create(WHITE_SPACE, SquirrelTokenTypes.WS, SquirrelTokenTypes.NL)

	val COMMENTS: TokenSet = TokenSet.create(SquirrelTokenTypes.SINGLE_LINE_COMMENT, SquirrelTokenTypes.MULTI_LINE_COMMENT, MULTI_LINE_DOC_COMMENT)
	val SEMICOLONS: TokenSet = TokenSet.create(SquirrelTokenTypes.SEMICOLON, SquirrelTokenTypes.SEMICOLON_SYNTHETIC)

	val NUMBERS: TokenSet = TokenSet.create(SquirrelTokenTypes.INT, SquirrelTokenTypes.FLOAT)
	val STRING_LITERALS: TokenSet = TokenSet.create(SquirrelTokenTypes.STRING)

	val KEYWORDS: TokenSet = TokenSet.create(
		SquirrelTokenTypes.CONST,
		SquirrelTokenTypes.ENUM,
		SquirrelTokenTypes.LOCAL,
		SquirrelTokenTypes.FUNCTION,
		SquirrelTokenTypes.CONSTRUCTOR,
		SquirrelTokenTypes.CLASS,
		SquirrelTokenTypes.EXTENDS,
		SquirrelTokenTypes.STATIC,
		SquirrelTokenTypes.BREAK,
		SquirrelTokenTypes.CONTINUE,
		SquirrelTokenTypes.RETURN,
		SquirrelTokenTypes.YIELD,
		SquirrelTokenTypes.THROW,
		SquirrelTokenTypes.FOR,
		SquirrelTokenTypes.FOREACH,
		SquirrelTokenTypes.IN,
		SquirrelTokenTypes.WHILE,
		SquirrelTokenTypes.DO,
		SquirrelTokenTypes.IF,
		SquirrelTokenTypes.ELSE,
		SquirrelTokenTypes.SWITCH,
		SquirrelTokenTypes.CASE,
		SquirrelTokenTypes.DEFAULT,
		SquirrelTokenTypes.TRY,
		SquirrelTokenTypes.CATCH,
		SquirrelTokenTypes.TYPEOF,
		SquirrelTokenTypes.CLONE,
		SquirrelTokenTypes.DELETE,
		SquirrelTokenTypes.RESUME,
		SquirrelTokenTypes.INSTANCEOF,
		SquirrelTokenTypes.TRUE,
		SquirrelTokenTypes.FALSE,
		SquirrelTokenTypes.NULL
	)
	val OPERATORS: TokenSet = TokenSet.create(
		SquirrelTokenTypes.RBRACE,
		SquirrelTokenTypes.RBRACKET,
		SquirrelTokenTypes.RPAREN,
		SquirrelTokenTypes.PLUS_PLUS,
		SquirrelTokenTypes.MINUS_MINUS,
		SquirrelTokenTypes.LBRACE,
		SquirrelTokenTypes.LBRACKET,
		SquirrelTokenTypes.LPAREN,
		SquirrelTokenTypes.DOUBLE_COLON,
		SquirrelTokenTypes.COLON,
		SquirrelTokenTypes.SEMICOLON,
		SquirrelTokenTypes.COMMA,
		SquirrelTokenTypes.MULTI_ARGS,
		SquirrelTokenTypes.CLASS_ATTR_START,
		SquirrelTokenTypes.CLASS_ATTR_END,
		SquirrelTokenTypes.SHIFT_LEFT,
		SquirrelTokenTypes.SHIFT_RIGHT,
		SquirrelTokenTypes.UNSIGNED_SHIFT_RIGHT,
		SquirrelTokenTypes.CMP,
		SquirrelTokenTypes.EQ_EQ,
		SquirrelTokenTypes.NOT_EQ,
		SquirrelTokenTypes.LESS_OR_EQ,
		SquirrelTokenTypes.GREATER_OR_EQ,
		SquirrelTokenTypes.SEND_CHANNEL,
		SquirrelTokenTypes.PLUS_EQ,
		SquirrelTokenTypes.MINUS_EQ,
		SquirrelTokenTypes.MUL_EQ,
		SquirrelTokenTypes.DIV_EQ,
		SquirrelTokenTypes.REMAINDER_EQ,
		SquirrelTokenTypes.OR_OR,
		SquirrelTokenTypes.AND_AND,
		SquirrelTokenTypes.EQ,
		SquirrelTokenTypes.NOT,
		SquirrelTokenTypes.BIT_NOT,
		SquirrelTokenTypes.BIT_OR,
		SquirrelTokenTypes.BIT_XOR,
		SquirrelTokenTypes.BIT_AND,
		SquirrelTokenTypes.LESS,
		SquirrelTokenTypes.GREATER,
		SquirrelTokenTypes.PLUS,
		SquirrelTokenTypes.MINUS,
		SquirrelTokenTypes.MUL,
		SquirrelTokenTypes.DIV,
		SquirrelTokenTypes.REMAINDER,
		SquirrelTokenTypes.QUESTION,
		SquirrelTokenTypes.AT,
		SquirrelTokenTypes.DOT
	)

	val EXPRESSIONS: TokenSet = TokenSet
		.create(
			SquirrelTokenTypes.ADDITIVE_EXPRESSION,
			SquirrelTokenTypes.ARRAY_EXPRESSION,
			SquirrelTokenTypes.ARRAY_ITEM_EXPRESSION,
			SquirrelTokenTypes.ASSIGN_EXPRESSION,
			SquirrelTokenTypes.BITWISE_AND_EXPRESSION,
			SquirrelTokenTypes.BITWISE_OR_EXPRESSION,
			SquirrelTokenTypes.BITWISE_XOR_EXPRESSION,
			SquirrelTokenTypes.CALL_EXPRESSION,
			SquirrelTokenTypes.CLASS_EXPRESSION,
			SquirrelTokenTypes.COMMA_EXPRESSION,
			SquirrelTokenTypes.COMPARE_EXPRESSION,
			SquirrelTokenTypes.FUNCTION_EXPRESSION,
			SquirrelTokenTypes.INSTANCE_OF_EXPRESSION,
			SquirrelTokenTypes.IN_EXPRESSION,
			SquirrelTokenTypes.LAMBDA_FUNCTION_EXPRESSION,
			SquirrelTokenTypes.LITERAL_EXPRESSION,
			SquirrelTokenTypes.LOGIC_AND_EXPRESSION,
			SquirrelTokenTypes.LOGIC_OR_EXPRESSION,
			SquirrelTokenTypes.MULTIPLICATIVE_EXPRESSION,
			SquirrelTokenTypes.PARENTHESIZED_EXPRESSION,
			SquirrelTokenTypes.PREFIX_EXPRESSION,
			SquirrelTokenTypes.REFERENCE_EXPRESSION,
			SquirrelTokenTypes.RELATIONAL_EXPRESSION,
			SquirrelTokenTypes.SHIFT_EXPRESSION,
			SquirrelTokenTypes.TABLE_EXPRESSION,
			SquirrelTokenTypes.TERNARY_EXPRESSION,
			SquirrelTokenTypes.UNARY_EXPRESSION
		)

	val BINARY_EXPRESSIONS: TokenSet = TokenSet.create(
		SquirrelTokenTypes.COMMA_EXPRESSION,
		SquirrelTokenTypes.ASSIGN_EXPRESSION,
		SquirrelTokenTypes.LOGIC_OR_EXPRESSION,
		SquirrelTokenTypes.LOGIC_AND_EXPRESSION,
		SquirrelTokenTypes.IN_EXPRESSION,
		SquirrelTokenTypes.BITWISE_AND_EXPRESSION,
		SquirrelTokenTypes.BITWISE_OR_EXPRESSION,
		SquirrelTokenTypes.BITWISE_XOR_EXPRESSION,
		SquirrelTokenTypes.COMPARE_EXPRESSION,
		SquirrelTokenTypes.RELATIONAL_EXPRESSION,
		SquirrelTokenTypes.SHIFT_EXPRESSION,
		SquirrelTokenTypes.ADDITIVE_EXPRESSION,
		SquirrelTokenTypes.MULTIPLICATIVE_EXPRESSION
	)

	val LOGIC_OPERATORS: TokenSet = TokenSet.create(
		SquirrelTokenTypes.AND_AND, SquirrelTokenTypes.OR_OR
	)

	val BITWISE_OPERATORS: TokenSet = TokenSet.create(
		SquirrelTokenTypes.BIT_AND, SquirrelTokenTypes.BIT_OR, SquirrelTokenTypes.BIT_XOR, SquirrelTokenTypes.BIT_NOT
	)

	val STATEMENTS: TokenSet = TokenSet.create(
		SquirrelTokenTypes.BLOCK,
		SquirrelTokenTypes.EXPRESSION_STATEMENT,
		SquirrelTokenTypes.CONST_DECLARATION,
		SquirrelTokenTypes.ENUM_DECLARATION,
		SquirrelTokenTypes.LOCAL_DECLARATION,
		SquirrelTokenTypes.FUNCTION_DECLARATION,
		SquirrelTokenTypes.CLASS_DECLARATION,
		SquirrelTokenTypes.FOR_STATEMENT,
		SquirrelTokenTypes.FOREACH_STATEMENT,
		SquirrelTokenTypes.WHILE_STATEMENT,
		SquirrelTokenTypes.DO_WHILE_STATEMENT,
		SquirrelTokenTypes.IF_STATEMENT,
		SquirrelTokenTypes.SWITCH_STATEMENT,
		SquirrelTokenTypes.TRY_STATEMENT,
		SquirrelTokenTypes.RETURN_STATEMENT,
		SquirrelTokenTypes.BREAK_STATEMENT,
		SquirrelTokenTypes.CONTINUE_STATEMENT,
		SquirrelTokenTypes.YIELD_STATEMENT,
		SquirrelTokenTypes.THROW_STATEMENT,
	)


	val DECLARATIONS: TokenSet = TokenSet.create(
		SquirrelTokenTypes.CLASS_DECLARATION,
		SquirrelTokenTypes.FUNCTION_DECLARATION,
		SquirrelTokenTypes.CONSTRUCTOR_DECLARATION,
		SquirrelTokenTypes.METHOD_DECLARATION,
		SquirrelTokenTypes.VAR_DECLARATION_LIST
	)

	val FUNCTION_DEFINITION: TokenSet = TokenSet.create(
		SquirrelTokenTypes.FUNCTION_DECLARATION,
		SquirrelTokenTypes.CONSTRUCTOR_DECLARATION,
		SquirrelTokenTypes.METHOD_DECLARATION
	)

	val BLOCKS: TokenSet = TokenSet.create(
		SquirrelTokenTypes.BLOCK,
		SquirrelTokenTypes.CLASS_BODY,
		SQUIRREL_FILE
	)
	val BLOCKS_WITH_BRACES: TokenSet = TokenSet.create(
		SquirrelTokenTypes.BLOCK,
		SquirrelTokenTypes.CLASS_BODY,
		SquirrelTokenTypes.SWITCH_STATEMENT,
		SquirrelTokenTypes.ENUM_DECLARATION,
		SquirrelTokenTypes.TABLE_EXPRESSION
	)

	val DOC_COMMENT_CONTENTS: TokenSet = TokenSet.create(MULTI_LINE_DOC_COMMENT_START, MULTI_LINE_COMMENT_BODY, DOC_COMMENT_LEADING_ASTERISK, MULTI_LINE_COMMENT_END)
}
