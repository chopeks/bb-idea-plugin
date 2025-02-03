package com.chopeks.psi.reference

import com.chopeks.psi.SquirrelAssignExpression
import com.chopeks.psi.SquirrelFunctionBody
import com.chopeks.psi.SquirrelReferenceExpression
import com.chopeks.psi.SquirrelStdIdentifier
import com.chopeks.psi.impl.LOG
import com.intellij.psi.util.PsiTreeUtil

class BBModdingHooksPsiStorage(
	wrapper: SquirrelFunctionBody?,
	private val hookedObjectRef: String
) {
	val mTableIds: HashMap<String, SquirrelStdIdentifier> = hashMapOf()
	val functionIds: HashMap<String, SquirrelStdIdentifier> = hashMapOf()

	init {
		LOG.warn("eh? \"$hookedObjectRef.\"")
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

	private fun setupFields(hookBody: SquirrelFunctionBody) {
		val expressions = PsiTreeUtil.findChildrenOfType(hookBody, SquirrelAssignExpression::class.java)
		for (expression in expressions) {
			val leftExpr = expression.expressionList.firstOrNull()
				?: continue
			if (leftExpr !is SquirrelReferenceExpression)
				continue
			if (leftExpr.text.startsWith("$hookedObjectRef.")) {
				if (leftExpr.text.startsWith("$hookedObjectRef.m.")) {
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
			} else {
				LOG.warn("didn't find function expression in \"$hookedObjectRef.\"")
			}
		}
	}

}
