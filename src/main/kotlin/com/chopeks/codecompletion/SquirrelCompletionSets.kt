package com.chopeks.codecompletion

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.codeStyle.CodeStyleManager

object SquirrelCompletionSets {
	val LOOPS: Array<LookupElement>
		get() = arrayOf(
			LookupElementBuilder.create("for").withPresentableText("for (...)").withInsertHandler { context, _ ->
				val offset = context.tailOffset
				val textAfter = context.document.getText(TextRange(offset, offset + 3))
				if ('(' !in textAfter)
					context.document.insertString(offset, " ()")
				context.editor.caretModel.moveToOffset(offset + 2)
			},
			LookupElementBuilder.create("for").withPresentableText("for (...) {...}").withInsertHandler { context, _ ->
				val offset = context.tailOffset
				val size = context.document.textLength
				val textAfter = context.document.getText(TextRange(offset, offset + 3))
				if ('(' !in textAfter) {
					context.document.insertString(offset, " () {\n\n}")
					CodeStyleManager.getInstance(context.project)
						.reformatText(context.file, offset, offset + context.document.textLength - size + 1)
				}
				context.editor.caretModel.moveToOffset(offset + 2)
			}
		)

}