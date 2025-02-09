package com.chopeks.highlighting

import com.chopeks.psi.SquirrelArguments
import com.chopeks.psi.SquirrelFile
import com.chopeks.psi.SquirrelId
import com.chopeks.psi.SquirrelReferenceExpression
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactoryRegistrar
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder
import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.siblings

class SquirrelHighlightingPassFactory : TextEditorHighlightingPassFactory, TextEditorHighlightingPassFactoryRegistrar {
	override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
		registrar.registerTextEditorHighlightingPass(this, null, null, false, -1)
	}

	override fun createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass? {
		if (file !is SquirrelFile)
			return null
		return SquirrelHighlightingPass(file, editor)
	}

}

class SquirrelHighlightingPass(
	private val file: PsiFile,
	private val editor: Editor
) : TextEditorHighlightingPass(file.project, editor.document) {
	private val holder: HighlightInfoHolder = HighlightInfoHolder(file)

	override fun doCollectInformation(progress: ProgressIndicator) {
		PsiTreeUtil.findChildrenOfType(file, SquirrelId::class.java).forEach { id ->
			if (id.parent is SquirrelReferenceExpression) {
				val siblings = id.parent.siblings(forward = true, withSelf = false)
				if (siblings.lastOrNull() is SquirrelArguments) {
					if (id.stdIdentifier != null) {
						val range = id.stdIdentifier!!.textRange
						holder.add(
							HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION)
								.severity(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
								.range(range.startOffset, range.endOffset)
								.textAttributes(SquirrelSyntaxHighlightingColors.FUNCTION_CALL)
								.createUnconditionally()
						)
					}
				}
			}
		}
	}

	override fun doApplyInformationToEditor() {
		val list: MutableCollection<RangeHighlighter>? = null
		val manager = HighlightManager.getInstance(file.project)
		(0 until holder.size()).map { holder[it] }.forEach {
			manager.addRangeHighlight(editor, it.startOffset, it.endOffset, it.forcedTextAttributesKey, false, list)
		}
	}
}