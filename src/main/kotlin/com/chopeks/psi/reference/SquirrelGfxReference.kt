package com.chopeks.psi.reference

import com.chopeks.psi.SquirrelStringLiteral
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiReferenceBase

class SquirrelGfxReference(
	private val file: VirtualFile,
	element: SquirrelStringLiteral
) : PsiReferenceBase<SquirrelStringLiteral>(element) {
	override fun resolve() = file.findPsiFile(element.project)
	override fun getVariants() = emptyArray<Any>()
	override fun getRangeInElement() = TextRange.allOf(element.string.text)
}