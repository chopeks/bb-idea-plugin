package com.chopeks.psi.reference

import com.chopeks.psi.SquirrelCallExpression
import com.chopeks.psi.SquirrelStdIdentifier
import com.chopeks.psi.impl.SquirrelReferenceExpressionImpl
import com.chopeks.psi.isBBClass
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil

class SquirrelStdIdReference(
	element: SquirrelStdIdentifier
) : PsiReferenceBase<SquirrelStdIdentifier>(element) {
	override fun resolve(): PsiElement? {
		if (element.containingFile.isBBClass)
			return resolveForBBClass()
		return resolveForModdingHooks()
	}

	override fun getVariants() = emptyArray<Any>()
	override fun getRangeInElement() = TextRange.allOf(element.text)

	private val qualifiedName: String?
		get() = PsiTreeUtil.getTopmostParentOfType(element, SquirrelReferenceExpressionImpl::class.java)?.text

	private fun resolveForBBClass(): PsiElement? {
		val file = BBClassPsiStorage(element.containingFile)
		val fullName = qualifiedName
			?: return null

		if (fullName.startsWith("this.m") || fullName.startsWith("m."))
			return file.getMTableRef(element)
		return null
	}

	private fun resolveForModdingHooks(): PsiElement? {
		val hookContainer = PsiTreeUtil.getTopmostParentOfType(element, SquirrelCallExpression::class.java)
			?: return null
		val file = BBModdingHooksPsiStorage(hookContainer)
		val fullName = qualifiedName
			?: return null

		if (fullName.startsWith("this.m") || fullName.startsWith("m."))
			return file.getMTableRef(element)
		return null
	}
}
