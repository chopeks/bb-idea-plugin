package com.chopeks.codecompletion

import com.chopeks.psi.SquirrelReferenceExpression
import com.chopeks.psi.index.BBIndexes
import com.chopeks.psi.isBBClass
import com.chopeks.psi.reference.BBClassPsiInheritanceStorage
import com.chopeks.util.hooks
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.TextRange
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext

class BBClassFunctionCompletionProvider : CompletionProvider<CompletionParameters>() {
	override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
		val file = parameters.originalFile
		val element = parameters.position
		val referenceExpr = PsiTreeUtil.getParentOfType(element, SquirrelReferenceExpression::class.java)
			?: return
		val referenceName = referenceExpr.text.replace(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED, "").trim('.')

		if ("this".startsWith(referenceName) && "this" !in referenceName)
			result.addElement(LookupElementBuilder.create("this").withIcon(AllIcons.Nodes.Function))
		if ("::".startsWith(referenceName) && "::" !in referenceName)
			result.addElement(LookupElementBuilder.create("::").withIcon(AllIcons.Nodes.Function))

		if (referenceName == "this") {
			if (file.isBBClass) {
				result.addElement(LookupElementBuilder.create("m").withIcon(AllIcons.Nodes.Field))
				BBClassPsiInheritanceStorage(file).allSymbols.filter { it.startsWith("fn_") }.forEach {
					result.addElement(makeMethodHandler(it.substring(it.indexOf('_') + 1)))
				}
			} else {
				element.containingFile.hooks.hookDefinitions.forEach {
					if (PsiTreeUtil.isAncestor(it.hookContainer, element, true)) {
						it.scriptRef?.resolve()?.also { element ->
							BBClassPsiInheritanceStorage(element.containingFile).allSymbols.filter { it.startsWith("fn_") }.forEach {
								result.addElement(makeMethodHandler(it.substring(it.indexOf('_') + 1)))
							}
						}
					}
				}
			}
		}

		if (referenceName.startsWith("::"))
			BBIndexes.queryGlobalSymbols(file, referenceName).forEach {
				if (it != "<eof>")
					result.addElement(LookupElementBuilder.create(it).withIcon(AllIcons.Nodes.Constant))
			}
	}

	private fun makeMethodHandler(name: String) = LookupElementBuilder.create(name)
		.withIcon(AllIcons.Nodes.Method)
		.withInsertHandler { context, item ->
			val offset = context.tailOffset
			val textAfter = context.document.getText(TextRange(offset, offset + 2))
			if ('(' !in textAfter)
				context.document.insertString(offset, "()")
			context.editor.caretModel.moveToOffset(offset + 1)
		}
}
