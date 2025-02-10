package com.chopeks.highlighting

import com.chopeks.psi.SquirrelArguments
import com.chopeks.psi.SquirrelFile
import com.chopeks.psi.SquirrelId
import com.chopeks.psi.SquirrelReferenceExpression
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.siblings


class SquirrelHighlightingPassFunctionCalls(
	private val file: PsiFile,
	private val editor: Editor
) : TextEditorHighlightingPass(file.project, editor.document) {
	class Factory : TextEditorHighlightingPassFactory {
		override fun createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass? {
			if (file !is SquirrelFile)
				return null
			return SquirrelHighlightingPassFunctionCalls(file, editor)
		}
	}

	private val holder: HighlightInfoHolder = HighlightInfoHolder(file)
	private val list = mutableListOf<RangeHighlighter>()

	override fun doCollectInformation(progress: ProgressIndicator) {
		holder.clear()
		PsiTreeUtil.findChildrenOfType(file, SquirrelId::class.java).forEach { id ->
			if (id.parent is SquirrelReferenceExpression) {
				val siblings = id.parent.siblings(forward = true, withSelf = false)
				val attributes = if (siblings.lastOrNull() is SquirrelArguments)
					SquirrelSyntaxHighlightingColors.FUNCTION_CALL
				else
					SquirrelSyntaxHighlightingColors.IDENTIFIER
				val range = id?.stdIdentifier?.textRange
					?: return@forEach
				HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION)
					.severity(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
					.range(range.startOffset, range.endOffset)
					.textAttributes(attributes)
					.create()?.also(holder::add)
			}
		}
	}

	override fun doApplyInformationToEditor() {
		val manager = HighlightManager.getInstance(file.project)
		list.forEach { manager.removeSegmentHighlighter(editor, it) }
		list.clear()
		(0 until holder.size()).map { holder[it] }.forEach {
			manager.addRangeHighlight(editor, it.startOffset, it.endOffset, it.forcedTextAttributesKey, false, list)
		}
	}
}