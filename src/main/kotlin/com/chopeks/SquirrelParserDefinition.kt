package com.chopeks

import com.chopeks.lexer.SquirrelLexer
import com.chopeks.psi.SquirrelFile
import com.chopeks.psi.impl.SquirrelDocCommentImpl
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement

class SquirrelParserDefinition : ParserDefinition {
	override fun createLexer(project: Project) = SquirrelLexer()
	override fun createParser(project: Project) = SquirrelParser()
	override fun getFileNodeType() = SquirrelTokenTypesSets.SQUIRREL_FILE
	override fun getWhitespaceTokens() = SquirrelTokenTypesSets.WHITE_SPACES
	override fun getCommentTokens() = SquirrelTokenTypesSets.COMMENTS
	override fun getStringLiteralElements() = SquirrelTokenTypesSets.STRING_LITERALS
	override fun createFile(viewProvider: FileViewProvider) = SquirrelFile(viewProvider)
	override fun spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode) = ParserDefinition.SpaceRequirements.MAY

	override fun createElement(node: ASTNode): PsiElement {
		if (node.elementType === SquirrelTokenTypesSets.MULTI_LINE_DOC_COMMENT) {
			return SquirrelDocCommentImpl(node)
		}
		return SquirrelTokenTypes.Factory.createElement(node)
	}
}