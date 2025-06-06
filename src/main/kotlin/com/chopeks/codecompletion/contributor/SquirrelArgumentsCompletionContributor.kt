package com.chopeks.codecompletion.contributor

import com.chopeks.SquirrelTokenTypes
import com.chopeks.codecompletion.BBClassFunctionCompletionProvider
import com.chopeks.codecompletion.BBmTableCompletionProvider
import com.chopeks.psi.SquirrelArgumentList
import com.chopeks.psi.SquirrelArrayExpression
import com.chopeks.psi.SquirrelTableItem
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
//		extend(
//			CompletionType.BASIC,
//			psiElement().inside(SquirrelStringLiteral::class.java),
//			BBStringCompletionProvider()
//		)
	}

	class InsideArgumentListPattern : PatternCondition<PsiElement>("arguments-pattern") {
		override fun accepts(psi: PsiElement, context: ProcessingContext?): Boolean {
			psi.node
				?: return false
			return psi.parents(false).firstOrNull { it is SquirrelArgumentList } != null || // argument list
					psi.parents(false).firstOrNull { it is SquirrelTableItem } != null || // table
					psi.parents(false).firstOrNull { it is SquirrelArrayExpression } != null // inside array
		}
	}
}
