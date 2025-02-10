package com.chopeks.highlighting

import com.chopeks.psi.SquirrelFile
import com.chopeks.psi.SquirrelStringLiteral
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


class SquirrelHighlightingPassStringReferences(
	private val file: PsiFile,
	private val editor: Editor
) : TextEditorHighlightingPass(file.project, editor.document) {
	class Factory : TextEditorHighlightingPassFactory {
		override fun createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass? {
			if (file !is SquirrelFile)
				return null
			return SquirrelHighlightingPassStringReferences(file, editor)
		}
	}

	private val holder: HighlightInfoHolder = HighlightInfoHolder(file)
	private val list = mutableListOf<RangeHighlighter>()

	override fun doCollectInformation(progress: ProgressIndicator) {
		holder.clear()
		PsiTreeUtil.findChildrenOfType(file, SquirrelStringLiteral::class.java).forEach { string ->
			val range = string.textRange
			val attributes = if (string.references.isNotEmpty())
				SquirrelSyntaxHighlightingColors.STRING_LINK
			else
				SquirrelSyntaxHighlightingColors.STRING
			HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION)
				.severity(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
				.range(range.startOffset, range.endOffset)
				.textAttributes(attributes)
				.create()?.also(holder::add)
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