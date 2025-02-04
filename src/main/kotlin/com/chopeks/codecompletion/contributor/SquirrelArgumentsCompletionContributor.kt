package com.chopeks.codecompletion.contributor

import com.chopeks.SquirrelTokenTypes
import com.chopeks.codecompletion.BBClassFunctionCompletionProvider
import com.chopeks.codecompletion.BBmTableCompletionProvider
import com.chopeks.psi.SquirrelArgumentList
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parents
import com.intellij.util.ProcessingContext

class SquirrelArgumentsCompletionContributor : CompletionContributor() {
	init {
		extend(
			CompletionType.BASIC,
			psiElement().withElementType(SquirrelTokenTypes.IDENTIFIER)
				.with(InsideArgumentListPattern()),
			BBmTableCompletionProvider()
		)
		extend(
			CompletionType.BASIC,
			psiElement().withElementType(SquirrelTokenTypes.IDENTIFIER)
				.with(InsideArgumentListPattern()),
			BBClassFunctionCompletionProvider()
		)
	}

	class InsideArgumentListPattern : PatternCondition<PsiElement>("arguments-pattern") {
		override fun accepts(psi: PsiElement, context: ProcessingContext?): Boolean {
			psi.node
				?: return false
			return psi.parents(false).firstOrNull { it is SquirrelArgumentList } != null
		}
	}
}
