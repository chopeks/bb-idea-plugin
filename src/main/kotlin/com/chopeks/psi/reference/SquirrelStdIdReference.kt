package com.chopeks.psi.reference

import com.chopeks.psi.SquirrelStdIdentifier
import com.chopeks.psi.impl.SquirrelReferenceExpressionImpl
import com.chopeks.psi.isBBClass
import com.chopeks.util.hooks
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.util.PsiTreeUtil

class SquirrelStdIdReference(
	element: SquirrelStdIdentifier
) : PsiPolyVariantReferenceBase<SquirrelStdIdentifier>(element) {
	override fun getVariants() = emptyArray<Any>()
	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		val results = mutableListOf<ResolveResult>()

		resolveForBBClass()?.also {
			results.add(PsiElementResolveResult(it))
		}
		resolveForModdingHooks().forEach {
			results.add(PsiElementResolveResult(it))
		}
		return results.toTypedArray()
	}

	override fun getRangeInElement() = TextRange.allOf(element.text)

	private val qualifiedName: String?
		get() = PsiTreeUtil.getTopmostParentOfType(element, SquirrelReferenceExpressionImpl::class.java)?.text

	private fun resolveForBBClass(): PsiElement? {
		if (!element.containingFile.isBBClass)
			return null
		val file = BBClassPsiInheritanceStorage(element.containingFile)
		val fullName = qualifiedName
			?: return null

		if (fullName.startsWith("this.m") || fullName.startsWith("m.")) {
			return file.findMField(element)
		}
		return null
	}

	private fun resolveForModdingHooks(): List<PsiElement> {
		val results = mutableListOf<PsiElement>()
		for (it in element.containingFile.hooks.hookDefinitions) {
			if (PsiTreeUtil.isAncestor(it.second, element, true)) {
				// todo hooks, hmm...
//				val file = BBModdingHooksPsiStorage(it.second)
//				val fullName = qualifiedName
//					?: continue

//				if (fullName.startsWith("this.m") || fullName.startsWith("m."))
//					elements.add(file.findMField(element))
			}
		}
//		val hookContainer = PsiTreeUtil.getTopmostParentOfType(element, SquirrelCallExpression::class.java)
//			?: return null
//		val file = BBModdingHooksPsiStorage(hookContainer)
//		val fullName = qualifiedName
//			?: return null
//
//		if (fullName.startsWith("this.m") || fullName.startsWith("m."))
//			return file.getMTableRef(element)
		return results
	}
}
