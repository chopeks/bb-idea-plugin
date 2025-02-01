package com.chopeks.psi.reference

import com.chopeks.psi.SquirrelStdIdentifier
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult

class SquirrelFieldReference(
	element: SquirrelStdIdentifier,
	private val file: PsiFile,
) : PsiPolyVariantReferenceBase<SquirrelStdIdentifier>(element) {
	override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
		val array = mutableListOf<ResolveResult>()

		return array.toTypedArray()
	}
}