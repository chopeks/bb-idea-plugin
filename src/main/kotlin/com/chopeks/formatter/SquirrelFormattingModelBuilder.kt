package com.chopeks.formatter

import com.chopeks.psi.SquirrelFile
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.DocumentBasedFormattingModel

class SquirrelFormattingModelBuilder : FormattingModelBuilder {
	override fun createModel(element: PsiElement, settings: CodeStyleSettings): FormattingModel {
		val psiFile = element.containingFile
		val rootNode = if (psiFile is SquirrelFile) psiFile.node else element.node
		val rootBlock = SquirrelBlock(rootNode, null, null, settings)
		return DocumentBasedFormattingModel(rootBlock, element.project, settings, psiFile.fileType, psiFile)
	}

	override fun getRangeAffectingIndent(file: PsiFile, offset: Int, elementAtOffset: ASTNode): TextRange? {
		return null
	}
}