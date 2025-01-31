package com.chopeks

import com.intellij.lang.CodeDocumentationAwareCommenter
import com.intellij.psi.PsiComment

class SquirrelCommenter : CodeDocumentationAwareCommenter {
	override fun getLineCommentPrefix() = "//"
	override fun getBlockCommentPrefix() = "/*"
	override fun getBlockCommentSuffix() = "*/"
	override fun getCommentedBlockCommentPrefix() = null
	override fun getCommentedBlockCommentSuffix() = null
	override fun getLineCommentTokenType() = SquirrelTokenTypes.SINGLE_LINE_COMMENT!!
	override fun getBlockCommentTokenType() = SquirrelTokenTypes.MULTI_LINE_COMMENT!!
	override fun getDocumentationCommentPrefix() = "/**"
	override fun getDocumentationCommentLinePrefix() = "*"
	override fun getDocumentationCommentSuffix() = "*/"
	override fun isDocumentationComment(element: PsiComment) = element.tokenType === SquirrelTokenTypesSets.MULTI_LINE_DOC_COMMENT
	override fun getDocumentationCommentTokenType() = SquirrelTokenTypesSets.MULTI_LINE_DOC_COMMENT!!
}