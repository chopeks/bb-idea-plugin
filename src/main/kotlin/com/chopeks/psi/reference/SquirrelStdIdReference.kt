package com.chopeks.psi.reference

import com.chopeks.psi.*
import com.chopeks.psi.impl.SquirrelReferenceExpressionImpl
import com.chopeks.psi.index.BBIndexes
import com.chopeks.util.hooks
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import java.nio.file.Paths

class SquirrelStdIdReference(
	element: SquirrelStdIdentifier
) : PsiPolyVariantReferenceBase<SquirrelStdIdentifier>(element) {
	override fun getVariants() = emptyArray<Any>()
	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		val results = mutableListOf<ResolveResult>()

		val refExpr = PsiTreeUtil.getTopmostParentOfType(element, SquirrelReferenceExpressionImpl::class.java)
			?: return results.toTypedArray()
		val fullName = refExpr.text
			?: return results.toTypedArray()

		if (fullName.startsWith("::") || fullName.startsWith("this.")) {
			resolveGlobal(results, fullName.replace("gt.", "::").replace("this.", "::"))
		}
		if (refExpr.parent is SquirrelCallExpression) {
			resolveFunction(results)
		} else if (refExpr.text == "this.${element.containingFile.virtualFile.nameWithoutExtension}") {
			element.containingFile.virtualFile.path.substringAfter("/scripts/").let { "scripts/$it" }
				.let { element.containingFile.getFile(it)?.findPsiFile(element.project) }
				?.let { results.add(PsiElementResolveResult(it)) }
		} else if (fullName.startsWith("this.m") || fullName.startsWith("m.")) {
			resolveMField(results)
		}

		return results.toTypedArray()
	}

	override fun getRangeInElement() = TextRange.allOf(element.text)

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

		results.addAll(resolveMFieldsForFiles(allFiles).map(::PsiElementResolveResult))
	}

	private fun resolveMFieldsForFiles(files: List<PsiFile>): List<PsiElement> {
		val results = mutableListOf<PsiElement>()
		for (file in files) {
			if (file.isBBClass) {
				BBClassPsiInheritanceStorage(file).findMField(element)
					?.also(results::add)
			} else {
				file.hooks.hookDefinitions.forEach {
					BBModdingHooksPsiStorage(it.hookContainer, it.hookedObjectRef).findMField(element)
						?.also(results::add)
				}
			}
		}
		return results.toSet().toList()
	}

	private fun resolveFunction(results: MutableList<ResolveResult>) {
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

		results.addAll(resolveFunctionsForFiles(allFiles).map(::PsiElementResolveResult))
	}

	private fun resolveFunctionsForFiles(files: List<PsiFile>): List<PsiElement> {
		val results = mutableListOf<PsiElement>()
		for (file in files) {
			if (file.isBBClass) {
				BBClassPsiInheritanceStorage(file).findFunction(element)
					?.also(results::add)
			} else {
				file.hooks.hookDefinitions.forEach {
					BBModdingHooksPsiStorage(it.hookContainer, it.hookedObjectRef).findFunction(element)
						?.also(results::add)
				}
			}
		}
		return results.toSet().toList()
	}

	private fun resolveGlobal(results: MutableList<ResolveResult>, fullName: String) {
		BBIndexes.queryGlobalSymbol(element.containingFile, fullName).forEach {
			val targetFile = VfsUtil.findFile(Paths.get(it), true)
			targetFile?.findPsiFile(element.project)?.also { file ->
				PsiTreeUtil.findChildrenOfType(file, SquirrelReferenceExpression::class.java)
					.filter { it.text.replace("gt.", "::") == fullName }
					.forEach { results.add(PsiElementResolveResult(it)) }
			}
		}
	}
}
