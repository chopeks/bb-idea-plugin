package com.chopeks.psi.reference

import com.chopeks.psi.*
import com.chopeks.psi.impl.LOG
import com.intellij.psi.util.PsiTreeUtil

class BBModdingHooksPsiStorage(
	wrapper: SquirrelCallExpression
) {
	private val superClass: BBClassPsiStorage?
	private val mTableIds: HashMap<String, SquirrelStdIdentifier> = hashMapOf()
	private val functionIds: HashMap<String, SquirrelStdIdentifier> = hashMapOf()

	init {
		LOG.warn("created for this ${wrapper.containingFile.virtualFile.path}")
		superClass = setupInheritance(wrapper)
		setupFields(wrapper)
	}

	fun getMTableRef(id: SquirrelStdIdentifier): SquirrelStdIdentifier? {
		return mTableIds[id.text] ?: superClass?.getMTableRef(id)
	}

	fun getFunctionRef(id: SquirrelStdIdentifier): SquirrelStdIdentifier? {
		return functionIds[id.text] ?: superClass?.getFunctionRef(id)
	}

	private fun setupFields(wrapper: SquirrelCallExpression) {
		// query first table, that's our class body
		val hookBody = PsiTreeUtil.findChildOfType(wrapper, SquirrelFunctionBody::class.java)
			?: return

		val expressions = PsiTreeUtil.findChildrenOfType(hookBody, SquirrelAssignExpression::class.java)
		LOG.warn("${expressions.size} expressions found")
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
