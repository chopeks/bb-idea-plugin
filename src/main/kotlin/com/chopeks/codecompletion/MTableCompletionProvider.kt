package com.chopeks.codecompletion

import com.chopeks.psi.SquirrelCallExpression
import com.chopeks.psi.SquirrelReferenceExpression
import com.chopeks.psi.SquirrelStdIdentifier
import com.chopeks.psi.isBBClass
import com.chopeks.psi.reference.BBClassPsiStorage
import com.chopeks.psi.reference.BBModdingHooksPsiStorage
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.util.ProcessingContext

class MTableCompletionProvider : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
		val element = parameters.position
		if (element.parent is SquirrelStdIdentifier) {
			val referenceExpr = PsiTreeUtil.getParentOfType(element, SquirrelReferenceExpression::class.java)
				?: return
			val text = referenceExpr.childrenOfType<SquirrelReferenceExpression>().firstOrNull()?.text
				?: return
			if (text == "this.m" || text == "m") {
				if (element.containingFile.isBBClass) {
					BBClassPsiStorage(element.containingFile).getMTableFields().forEach {
						result.addElement(LookupElementBuilder.create(it.text).withIcon(AllIcons.Nodes.Field))
					}
					return
				}
				val hookContainer = PsiTreeUtil.getTopmostParentOfType(element, SquirrelCallExpression::class.java)
				BBModdingHooksPsiStorage(hookContainer).getMTableFields().forEach {
					result.addElement(LookupElementBuilder.create(it.text).withIcon(AllIcons.Nodes.Field))
				}
			}
		}
	}
}