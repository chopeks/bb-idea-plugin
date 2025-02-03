package com.chopeks.codecompletion

import com.chopeks.psi.SquirrelReferenceExpression
import com.chopeks.psi.SquirrelStdIdentifier
import com.chopeks.psi.SquirrelStringLiteral
import com.chopeks.psi.impl.LOG
import com.chopeks.psi.isBBClass
import com.chopeks.psi.reference.BBClassPsiInheritanceStorage
import com.chopeks.util.hooks
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext

class BBmTableCompletionProvider : CompletionProvider<CompletionParameters>() {
	private val dummyRef = "IntellijIdeaRulezzz"

	override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
		val file = parameters.originalFile
		val element = parameters.position
		if (element.parent is SquirrelStdIdentifier) {
			val referenceExpr = PsiTreeUtil.getParentOfType(element, SquirrelReferenceExpression::class.java)
				?: return
			val referenceName = referenceExpr.text.replace(dummyRef, "").trim('.')

			LOG.warn("referenceNam2e=`$referenceName`")

			if (referenceName == "this.m" || referenceName == "m") {
				if (file.isBBClass) {
					BBClassPsiInheritanceStorage(file).allSymbols.filter { it.startsWith("m_") }.forEach {
						result.addElement(LookupElementBuilder.create(it.substring(it.indexOf('_') + 1)).withIcon(AllIcons.Nodes.Field))
					}
					return
				}
				element.containingFile.hooks.hookDefinitions.forEach {
					if (PsiTreeUtil.isAncestor(it.second, element, true)) {
						PsiTreeUtil.findChildOfType(it.second, SquirrelStringLiteral::class.java)?.reference?.resolve()?.also { element ->
							BBClassPsiInheritanceStorage(element.containingFile).allSymbols.filter { it.startsWith("m_") }.forEach {
								result.addElement(LookupElementBuilder.create(it.substring(it.indexOf('_') + 1)).withIcon(AllIcons.Nodes.Field))
							}
						}
					}
				}
			}
		}
	}
}