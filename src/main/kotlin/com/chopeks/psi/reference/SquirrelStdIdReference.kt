package com.chopeks.psi.reference

import com.chopeks.psi.SquirrelStdIdentifier
import com.chopeks.psi.impl.SquirrelReferenceExpressionImpl
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil

class SquirrelStdIdReference(
	element: SquirrelStdIdentifier
) : PsiReferenceBase<SquirrelStdIdentifier>(element) {
	override fun resolve(): PsiElement? {
		val file = BBClassPsiStorage(element.containingFile)
		val fullName = qualifiedName
			?: return null

		if (fullName.startsWith("this.m") || fullName.startsWith("m."))
			return file.getMTableRef(element)
		return null
	}

	override fun getVariants() = emptyArray<Any>()
	override fun getRangeInElement() = TextRange.allOf(element.text)

	val qualifiedName: String?
		get() = PsiTreeUtil.getTopmostParentOfType(element, SquirrelReferenceExpressionImpl::class.java)?.text
}
