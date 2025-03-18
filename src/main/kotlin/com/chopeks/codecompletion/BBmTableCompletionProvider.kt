package com.chopeks.codecompletion

import com.chopeks.psi.*
import com.chopeks.psi.impl.LOG
import com.chopeks.psi.reference.BBClassPsiInheritanceStorage
import com.chopeks.util.hooks
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parents
import com.intellij.util.ProcessingContext

class BBmTableCompletionProvider : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
		val file = parameters.originalFile
		val element = parameters.position
		if (element.parent is SquirrelStdIdentifier) {
			val referenceExpr = PsiTreeUtil.getParentOfType(element, SquirrelReferenceExpression::class.java)
				?: return
			val referenceName = referenceExpr.text.replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "").trim('.').trim()

			LOG.warn("referenceName $referenceName")

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
			// local variables
			element.parents(false).toList().reversed()
				.let {
					val value = it.firstOrNull { it is SquirrelFunctionDeclaration }
						?: return@let emptyList()
					it.subList(it.indexOf(value), it.size - 1)
				}
				.also {
					it.filterIsInstance<SquirrelFunctionDeclaration>().map { it.parameters?.parameterList?.parameterList }.mapNotNull { it?.mapNotNull { it.id.stdIdentifier?.identifier?.text } }.flatten().forEach {
						if (referenceName.isEmpty() || referenceName.startsWith(it))
							result.addElement(LookupElementBuilder.create(it).withIcon(AllIcons.Nodes.Variable))
					}
					addControlsVars(element, it, referenceName, result)
				}
			element.parents(false).toList().reversed()
				.let {
					val value = it.firstOrNull { it is SquirrelFunctionExpression }
						?: return@let emptyList()
					it.subList(it.indexOf(value), it.size - 1)
				}
				.also {
					it.filterIsInstance<SquirrelFunctionExpression>().map { it.parameters.parameterList?.parameterList }.mapNotNull { it?.mapNotNull { it.id.stdIdentifier?.identifier?.text } }.flatten().forEach {
						if (referenceName.isEmpty() || referenceName.startsWith(it))
							result.addElement(LookupElementBuilder.create(it).withIcon(AllIcons.Nodes.Variable))
					}
					addControlsVars(element, it, referenceName, result)
				}
		}
	}

	private fun addControlsVars(element: PsiElement, it: List<PsiElement>, referenceName: String, result: CompletionResultSet) {
		it.filterIsInstance<SquirrelForeachStatement>().map { it.idList }.map { it.mapNotNull { it.stdIdentifier?.identifier?.text } }.flatten().forEach {
			if (referenceName.isEmpty() || referenceName.startsWith(it))
				result.addElement(LookupElementBuilder.create(it).withIcon(AllIcons.Nodes.Variable))
		}
		it.filterIsInstance<SquirrelForStatement>().forEach { loop ->
			loop.forLoopParts?.localDeclaration?.varDeclarationList?.varItemList?.mapNotNull { it.id.stdIdentifier?.identifier?.text }?.forEach {
				if (referenceName.isEmpty() || referenceName.startsWith(it))
					result.addElement(LookupElementBuilder.create(it).withIcon(AllIcons.Nodes.Variable))
			}
		}
		it.filterIsInstance<SquirrelBlock>().forEach { block ->
			block.children.filterIsInstance<SquirrelLocalDeclaration>().forEach { local ->
				if (PsiTreeUtil.findCommonParent(local, element) != null && local.textOffset < element.textOffset)
					local.varDeclarationList?.varItemList?.mapNotNull { it.id.stdIdentifier?.identifier?.text }?.forEach {
						if (referenceName.isEmpty() || referenceName.startsWith(it))
							result.addElement(LookupElementBuilder.create(it).withIcon(AllIcons.Nodes.Variable))
					}
			}
		}
	}
}
