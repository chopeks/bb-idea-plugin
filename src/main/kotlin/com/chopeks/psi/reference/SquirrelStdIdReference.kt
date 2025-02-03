package com.chopeks.psi.reference

import com.chopeks.psi.SquirrelStdIdentifier
import com.chopeks.psi.impl.SquirrelReferenceExpressionImpl
import com.chopeks.psi.index.BBIndexes
import com.chopeks.psi.isBBClass
import com.chopeks.util.hooks
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

class SquirrelStdIdReference(
	element: SquirrelStdIdentifier
) : PsiPolyVariantReferenceBase<SquirrelStdIdentifier>(element) {
	override fun getVariants() = emptyArray<Any>()
	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		val results = mutableListOf<ResolveResult>()

		val fullName = qualifiedName
			?: return results.toTypedArray()

		if (fullName.startsWith("this.m") || fullName.startsWith("m.")) {
			resolveMField(results)
		}

		return results.toTypedArray()
	}

	override fun getRangeInElement() = TextRange.allOf(element.text)

	private val qualifiedName: String?
		get() = PsiTreeUtil.getTopmostParentOfType(element, SquirrelReferenceExpressionImpl::class.java)?.text

	private fun resolveMField(results: MutableList<ResolveResult>) {
		val file = if (element.containingFile.isBBClass)
			BBClassPsiInheritanceStorage(element.containingFile)
		else
			element.containingFile.hooks.findHookClass(element)
				?.let(::BBClassPsiInheritanceStorage)
				?: return

		val allFiles = mutableListOf<PsiFile>()
		var currentClass: BBClassPsiInheritanceStorage? = file
		do {
			currentClass?.file?.let { allFiles.addAll(BBIndexes.querySymbolFiles(it)) }
			currentClass = currentClass?.superClass
		} while (currentClass != null)

		results.addAll(resolveForFiles(allFiles).map(::PsiElementResolveResult))
	}

	private fun resolveForFiles(files: List<PsiFile>): List<PsiElement> {
		val results = mutableListOf<PsiElement>()
		for (file in files) {
			if (file.isBBClass) {
				BBClassPsiInheritanceStorage(file).findMField(element)
					?.also(results::add)
			} else {
				file.hooks.hookDefinitions.forEach {
					BBModdingHooksPsiStorage(it.second).findMField(element)
						?.also(results::add)
				}
			}
		}
		return results.toSet().toList()
	}
}
