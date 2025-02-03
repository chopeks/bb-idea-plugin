package com.chopeks.psi.reference

import com.chopeks.psi.*
import com.chopeks.psi.index.BBIndexes
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.PsiTreeUtil

class BBClassPsiInheritanceStorage(
	private val file: PsiFile
) {
	private val superClass: BBClassPsiInheritanceStorage?
	private val symbols: List<String>
	private val files: List<PsiFile>

	init {
		superClass = setupInheritance(file)
		symbols = BBIndexes.querySymbols(file)
		files = BBIndexes.querySymbolFiles(file)
	}

	fun findMField(element: SquirrelStdIdentifier): PsiNameIdentifierOwner? {
		val name = "m_" + element.identifier.text
		if (name !in symbols)
			return superClass?.findMField(element)
		return PsiTreeUtil.findChildrenOfType(file, SquirrelTableItem::class.java)
			.firstOrNull { "m_${it.key?.text}" == name }?.key?.stdIdentifier
	}

	private val prettySymbols
		get() = symbols.filter { it.length > 2 }.map { it.substring(2) }

	val allSymbols: List<String>
		get() = if (superClass == null) prettySymbols else prettySymbols + superClass.allSymbols

	private fun setupInheritance(file: PsiFile): BBClassPsiInheritanceStorage? {
		val scriptReference: SquirrelFile = PsiTreeUtil.findChildOfType(file, SquirrelCallExpression::class.java)?.let {
			PsiTreeUtil.findChildOfType(it, SquirrelArgumentList::class.java)
		}?.let {
			PsiTreeUtil.findChildOfType(it, SquirrelStringLiteral::class.java)
		}?.let {
			it.reference?.resolve() as? SquirrelFile
		} ?: return null
		return BBClassPsiInheritanceStorage(scriptReference)
	}
}
