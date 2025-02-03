package com.chopeks.psi.reference

import com.chopeks.psi.*
import com.chopeks.psi.index.BBIndexes
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil

class BBClassPsiInheritanceStorage(
	val file: PsiFile
) {
	val superClass: BBClassPsiInheritanceStorage?
	private val symbols: List<String>

	init {
		superClass = setupInheritance(file)
		symbols = BBIndexes.querySymbols(file)
	}

	fun findMField(element: SquirrelStdIdentifier): SquirrelStdIdentifier? {
		val name = "m_" + element.identifier.text
		return PsiTreeUtil.findChildrenOfType(file, SquirrelTableItem::class.java)
			.firstOrNull { "m_${it.key?.text}" == name }?.key?.stdIdentifier
	}

	fun findFunction(element: SquirrelStdIdentifier): SquirrelStdIdentifier? {
		val name = "fn_" + element.identifier.text
		return PsiTreeUtil.findChildrenOfType(file, SquirrelFunctionDeclaration::class.java)
			.firstOrNull { "fn_${it.functionName?.stdIdentifier?.text}" == name }?.functionName?.stdIdentifier
	}

	val allSymbols: List<String>
		get() = if (superClass == null) symbols else symbols + superClass.allSymbols

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
