package com.chopeks.psi.impl.manipulator

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException

abstract class SquirrelManipulator<T : PsiElement?> : AbstractElementManipulator<T>() {
	@Throws(IncorrectOperationException::class)
	override fun handleContentChange(literal: T & Any, range: TextRange, newContent: String): T {
		throw UnsupportedOperationException()
	}

	override fun getRangeInElement(element: T & Any): TextRange {
		if (element.textLength == 0) {
			return TextRange.EMPTY_RANGE
		}
		val s = element.text
		val quote = s[0]
		val startOffset = if (isQuote(quote)) 1 else 0
		var endOffset = s.length
		if (s.length > 1) {
			val lastChar = s[s.length - 1]
			if (isQuote(quote) && lastChar == quote) {
				endOffset = s.length - 1
			}
			if (!isQuote(quote) && isQuote(lastChar)) {
				endOffset = s.length - 1
			}
		}
		return TextRange.create(startOffset, endOffset)
	}

	companion object {
		private fun isQuote(ch: Char): Boolean {
			return ch == '"' || ch == '\'' || ch == '`'
		}
	}
}
