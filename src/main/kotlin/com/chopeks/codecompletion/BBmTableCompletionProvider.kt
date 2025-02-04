package com.chopeks.codecompletion

import com.chopeks.psi.*
import com.chopeks.psi.reference.BBClassPsiInheritanceStorage
import com.chopeks.util.hooks
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parents
import com.intellij.psi.util.siblings
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
				} else {
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
			// function parameters
			element.parents(false).filterIsInstance<SquirrelFunctionDeclaration>().firstOrNull()
				?.let { it.parameters?.parameterList?.parameterList?.mapNotNull { it.id.stdIdentifier?.identifier?.text } }
				?.forEach {
					if (referenceName in it && referenceName != it) {
						result.addElement(LookupElementBuilder.create(it).withIcon(AllIcons.Nodes.Parameter))
					}
				}
			// local variables
			element.parents(false).firstOrNull { it is SquirrelExpressionStatement && it.parent is SquirrelBlock }?.let {
				it.siblings(false).filterIsInstance<SquirrelLocalDeclaration>().map {
					it.varDeclarationList?.varItemList?.mapNotNull { it.id.stdIdentifier?.identifier?.text }
				}
			}?.forEach { variables ->
				variables?.forEach { variable ->
					if (referenceName in variable && referenceName != variable) {
						result.addElement(LookupElementBuilder.create(variable).withIcon(AllIcons.Nodes.Variable))
					}
				}
			}
		}
	}
}
