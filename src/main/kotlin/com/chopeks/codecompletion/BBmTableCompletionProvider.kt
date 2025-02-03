package com.chopeks.codecompletion

import com.chopeks.psi.SquirrelReferenceExpression
import com.chopeks.psi.SquirrelStdIdentifier
import com.chopeks.psi.isBBClass
import com.chopeks.psi.reference.BBClassPsiInheritanceStorage
import com.chopeks.util.hooks
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext

class BBmTableCompletionProvider : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
		val file = parameters.originalFile
		val element = parameters.position
		if (element.parent is SquirrelStdIdentifier) {
			val referenceExpr = PsiTreeUtil.getParentOfType(element, SquirrelReferenceExpression::class.java)
				?: return
			val referenceName = referenceExpr.text.replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "").trim('.')

			if (referenceName.startsWith("this.m")) {
				if (file.isBBClass) {
					BBClassPsiInheritanceStorage(file).allSymbols.filter { it.startsWith("m_") }.forEach {
						result.addElement(LookupElementBuilder.create(it.substring(it.indexOf('_') + 1)).withIcon(AllIcons.Nodes.Field))
					}
					return
				}
				element.containingFile.hooks.hookDefinitions.forEach {
					if (PsiTreeUtil.isAncestor(it.hookContainer, element, true)) {
						it.scriptRef?.resolve()?.also { element ->
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
