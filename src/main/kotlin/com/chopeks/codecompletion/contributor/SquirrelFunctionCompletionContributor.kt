package com.chopeks.codecompletion.contributor

import com.chopeks.SquirrelTokenTypes
import com.chopeks.codecompletion.BBClassFunctionCompletionProvider
import com.chopeks.codecompletion.BBmTableCompletionProvider
import com.chopeks.psi.SquirrelArgumentList
import com.chopeks.psi.SquirrelFunctionBody
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionInitializationContext
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parents
import com.intellij.util.ProcessingContext


class SquirrelFunctionCompletionContributor : CompletionContributor() {
	init {
		extend(
			CompletionType.BASIC,
			psiElement(SquirrelTokenTypes.IDENTIFIER)
				.with(InsideFunctionPattern()),
			BBmTableCompletionProvider()
		)
		extend(
			CompletionType.BASIC,
			psiElement().withElementType(SquirrelTokenTypes.IDENTIFIER)
				.with(InsideFunctionPattern()),
			BBClassFunctionCompletionProvider()
		)
	}

	override fun beforeCompletion(context: CompletionInitializationContext) {
		super.beforeCompletion(context)
		context.dummyIdentifier = CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED + ";"
	}

	class InsideFunctionPattern : PatternCondition<PsiElement>("function-pattern") {
		override fun accepts(psi: PsiElement, context: ProcessingContext?): Boolean {
			val parent = psi.parents(false).firstOrNull { it is SquirrelArgumentList || it is SquirrelFunctionBody }
			if (parent is SquirrelArgumentList)
				return false
			if (parent !is SquirrelFunctionBody)
				return false
			psi.node
				?: return false
			return true
		}
	}
}


