package com.chopeks.psi.reference

import com.chopeks.psi.*
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil

class BBClassPsiStorage(
	file: PsiFile
) {
	private val superClass: BBClassPsiStorage?
	val mTableIds: HashMap<String, SquirrelStdIdentifier> = hashMapOf()
	val functionIds: HashMap<String, SquirrelStdIdentifier> = hashMapOf()

	init {
		superClass = setupInheritance(file)
		// query first table, that's our class body
		val classTable = PsiTreeUtil.findChildOfType(file, SquirrelTableExpression::class.java)
		// mTable is first table in class body
		val mTable = classTable?.let {
			PsiTreeUtil.findChildOfType(it, SquirrelTableExpression::class.java)
		}
		if (mTable != null) {
			mTableIds.clear()
			for (item in PsiTreeUtil.findChildrenOfType(mTable, SquirrelTableItem::class.java)) {
				val key = item.key?.stdIdentifier ?: continue
				if (item.expression == null)
					continue // skip if something doesn't exist
				mTableIds[key.text] = key
			}
		}
		functionIds.clear()
		for (function in PsiTreeUtil.findChildrenOfType(file, SquirrelFunctionDeclaration::class.java)) {
			val id = function.functionName?.stdIdentifier ?: continue
			val name = id.text
			if (name.isNullOrEmpty())
				continue
			functionIds[name] = id
		}
	}

	fun getMTableRef(id: SquirrelStdIdentifier): SquirrelStdIdentifier? {

		return mTableIds[id.text] ?: superClass?.getMTableRef(id)
	}

	fun getFunctionRef(id: SquirrelStdIdentifier): SquirrelStdIdentifier? {
		return functionIds[id.text] ?: superClass?.getFunctionRef(id)
	}

	fun getMTableFields(): List<SquirrelStdIdentifier> {
		return mutableListOf<SquirrelStdIdentifier>().apply {
			addAll(mTableIds.toList().map { it.second })
			if (superClass != null)
				addAll(superClass.getMTableFields())
		}
	}

	private fun setupInheritance(file: PsiFile): BBClassPsiStorage? {
		val scriptReference: SquirrelFile = PsiTreeUtil.findChildOfType(file, SquirrelCallExpression::class.java)?.let {
			PsiTreeUtil.findChildOfType(it, SquirrelArgumentList::class.java)
		}?.let {
			PsiTreeUtil.findChildOfType(it, SquirrelStringLiteral::class.java)
		}?.let {
			it.reference?.resolve() as? SquirrelFile
		} ?: return null
		return BBClassPsiStorage(scriptReference)
	}
}
