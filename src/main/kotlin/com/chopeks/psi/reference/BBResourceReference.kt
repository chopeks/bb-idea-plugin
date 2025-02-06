package com.chopeks.psi.reference

import com.chopeks.psi.SquirrelStringLiteral
import com.chopeks.psi.index.BBIndexes
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex

class BBResourceReference(
	element: SquirrelStringLiteral
) : PsiPolyVariantReferenceBase<SquirrelStringLiteral>(element) {
	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		if (incompleteCode)
			return emptyArray()
		val references = mutableListOf<ResolveResult>()
		// check resources index
		FileBasedIndex.getInstance().getFilesWithKey(BBIndexes.BBResources, setOf(element.text.trim('"')), {
			it.findPsiFile(element.project)?.let(::PsiElementResolveResult)?.also(references::add); true
		}, GlobalSearchScope.allScope(element.project))

		return references.toTypedArray()
	}
}