package com.chopeks.psi.reference

import com.chopeks.psi.*
import com.intellij.psi.util.PsiTreeUtil

class BBModdingHooksPsiStorage(
	wrapper: SquirrelCallExpression?
) {
	val mTableIds: HashMap<String, SquirrelStdIdentifier> = hashMapOf()
	val functionIds: HashMap<String, SquirrelStdIdentifier> = hashMapOf()

	init {
		if (wrapper != null) {
			setupFields(wrapper)
		}
	}


	fun findMField(element: SquirrelStdIdentifier): SquirrelStdIdentifier? {
		return mTableIds[element.identifier.text]
	}

	fun findFunction(element: SquirrelStdIdentifier): SquirrelStdIdentifier? {
		return functionIds[element.identifier.text]
	}

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

}
