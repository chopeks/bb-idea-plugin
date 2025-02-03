package com.chopeks.codecompletion

import com.chopeks.SquirrelTokenTypes
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns.psiElement

class SquirrelCompletionContributor : CompletionContributor() {
	init {
		extend(
			CompletionType.BASIC,
			psiElement()
				.withElementType(SquirrelTokenTypes.IDENTIFIER)
				.inside(psiElement().withElementType(SquirrelTokenTypes.REFERENCE_EXPRESSION))
				.inside(psiElement().withElementType(SquirrelTokenTypes.FUNCTION_BODY)),
			BBmTableCompletionProvider()
		)
		extend(
			CompletionType.BASIC,
			psiElement()
				.withElementType(SquirrelTokenTypes.IDENTIFIER)
				.inside(psiElement().withElementType(SquirrelTokenTypes.REFERENCE_EXPRESSION))
				.inside(psiElement().withElementType(SquirrelTokenTypes.FUNCTION_BODY)),
			BBClassFunctionCompletionProvider()
		)
	}
}