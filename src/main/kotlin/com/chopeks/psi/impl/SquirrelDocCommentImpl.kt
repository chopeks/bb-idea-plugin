package com.chopeks.psi.impl

import com.chopeks.psi.SquirrelDocComment
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

class SquirrelDocCommentImpl(node: ASTNode) : ASTWrapperPsiElement(node), SquirrelDocComment {
	override fun getOwner(): PsiElement? = null
	override fun getTokenType() = node.elementType
}