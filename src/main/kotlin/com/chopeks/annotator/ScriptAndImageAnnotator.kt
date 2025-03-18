package com.chopeks.annotator

import com.chopeks.psi.SquirrelStringLiteral
import com.chopeks.psi.impl.SquirrelPsiImplUtil
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

/**
 * This class is responsible for underlining warning if file validation fails
 *
 * Currently checks for file existence in project
 */
class ScriptAndImageAnnotator : Annotator {
	override fun annotate(element: PsiElement, holder: AnnotationHolder) {
		if (element !is SquirrelStringLiteral)
			return

		SquirrelPsiImplUtil.pathRegex.find(element.string.text.trim('"'))
			?: return

		if (element.references.all { it.resolve()?.containingFile?.virtualFile == null }) {
			holder.newAnnotation(HighlightSeverity.WARNING, "It looks like defined file, but can't be found in current project.")
				.highlightType(ProblemHighlightType.WARNING)
				.range(element)
				.needsUpdateOnTyping()
				.create()
		}
	}
}