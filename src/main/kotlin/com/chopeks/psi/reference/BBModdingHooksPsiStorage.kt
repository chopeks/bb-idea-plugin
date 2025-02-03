package com.chopeks.psi.reference

import com.chopeks.psi.*
import com.intellij.psi.util.PsiTreeUtil

class BBModdingHooksPsiStorage(
	wrapper: SquirrelCallExpression?
) {
	private val superClass: BBClassPsiStorage?
	val mTableIds: HashMap<String, SquirrelStdIdentifier> = hashMapOf()
	private val functionIds: HashMap<String, SquirrelStdIdentifier> = hashMapOf()

	init {
		if (wrapper != null) {
//			superClass = setupInheritance(wrapper)
			setupFields(wrapper)
		}
			superClass = null
//		}
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

//	fun findMField(element: SquirrelStdIdentifier): PsiNameIdentifierOwner? {
//		val name = "m_" + element.identifier.text
//		if (name !in superClass.symbols)
//			return superClass?.findMField(element)
//		return PsiTreeUtil.findChildrenOfType(file, SquirrelTableItem::class.java)
//			.firstOrNull { "m_${it.key?.text}" == name }?.key?.stdIdentifier
//	}


	private fun setupFields(wrapper: SquirrelCallExpression) {
		val hookBody = PsiTreeUtil.findChildOfType(wrapper, SquirrelFunctionBody::class.java)
			?: return

		val expressions = PsiTreeUtil.findChildrenOfType(hookBody, SquirrelAssignExpression::class.java)
		for (expression in expressions) {
			val leftExpr = expression.expressionList.firstOrNull()
				?: continue
			if (leftExpr !is SquirrelReferenceExpression)
				continue

			if (leftExpr.text.startsWith("o.")) {
				if (leftExpr.text.startsWith("o.m.")) {
					// m table
					val item = PsiTreeUtil.findChildrenOfType(leftExpr, SquirrelStdIdentifier::class.java).lastOrNull()
						?: continue
					mTableIds[item.text] = item
				} else {
					// functions
					val item = PsiTreeUtil.findChildrenOfType(leftExpr, SquirrelStdIdentifier::class.java).lastOrNull()
						?: continue
					functionIds[item.text] = item
				}
			}
		}
	}

	private fun setupInheritance(wrapper: SquirrelCallExpression): BBClassPsiStorage? {
		val scriptReference: SquirrelFile = PsiTreeUtil.findChildOfType(wrapper, SquirrelArgumentList::class.java)?.let {
			PsiTreeUtil.findChildOfType(it, SquirrelStringLiteral::class.java)
		}?.let {
			it.reference?.resolve() as? SquirrelFile
		} ?: return null
		return BBClassPsiStorage(scriptReference)
	}
}
