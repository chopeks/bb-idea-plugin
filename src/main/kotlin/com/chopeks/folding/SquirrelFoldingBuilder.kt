package com.chopeks.folding

import com.chopeks.SquirrelTokenTypes
import com.chopeks.SquirrelTokenTypesSets
import com.chopeks.psi.*
import com.intellij.codeInsight.folding.CodeFoldingSettings
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.UnfairTextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil

class SquirrelFoldingBuilder : CustomFoldingBuilder(), DumbAware {
	override fun buildLanguageFoldRegions(
		descriptors: MutableList<FoldingDescriptor>,
		root: PsiElement,
		document: Document,
		quick: Boolean
	) {
		val dartFile = root as SquirrelFile
		val fileHeaderRange = foldFileHeader(descriptors, dartFile, document) // 1. File header
		val psiElements = PsiTreeUtil.collectElementsOfType(root, *arrayOf(PsiComment::class.java))
		foldComments(descriptors, psiElements, fileHeaderRange)
		foldClassBodies(descriptors, dartFile)
		foldFunctionBodies(descriptors, dartFile)
	}

	override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange): String? {
		val elementType = node.elementType
		val psiElement = node.psi

		if (psiElement is SquirrelFile) return "/.../"
		if (elementType === SquirrelTokenTypesSets.MULTI_LINE_DOC_COMMENT) return "/**...*/"
		if (elementType === SquirrelTokenTypes.MULTI_LINE_COMMENT) return "/*...*/"
		if (elementType === SquirrelTokenTypes.SINGLE_LINE_COMMENT) return "//..."
		if (psiElement is SquirrelClassBody) return "{...}"
		if (psiElement is SquirrelFunctionBody) return "{...}"
		return "..."
	}

	override fun isRegionCollapsedByDefault(node: ASTNode): Boolean {
		val elementType = node.elementType
		val psiElement = node.psi
		val settings = CodeFoldingSettings.getInstance()

		if (psiElement is SquirrelFile) return settings.COLLAPSE_FILE_HEADER

		if (elementType === SquirrelTokenTypesSets.MULTI_LINE_DOC_COMMENT)
			return settings.COLLAPSE_DOC_COMMENTS


		if (elementType === SquirrelTokenTypes.MULTI_LINE_COMMENT || elementType === SquirrelTokenTypes.SINGLE_LINE_COMMENT)
			return settings.COLLAPSE_DOC_COMMENTS

		if (psiElement is SquirrelFunctionBody)
			return settings.COLLAPSE_METHODS

		return false
	}

	companion object {
		@JvmStatic
		private fun foldFileHeader(
			descriptors: MutableList<FoldingDescriptor>,
			squirrelFile: SquirrelFile,
			document: Document
		): TextRange? {
			var firstComment = squirrelFile.firstChild
			if (firstComment is PsiWhiteSpace) {
				firstComment = firstComment.getNextSibling()
			}

			if (firstComment !is PsiComment) return null

			var containsCustomRegionMarker = false
			var nextAfterComments = firstComment
			while (nextAfterComments is PsiComment || nextAfterComments is PsiWhiteSpace) {
				containsCustomRegionMarker = containsCustomRegionMarker or isCustomRegionElement(nextAfterComments)
				nextAfterComments = nextAfterComments.nextSibling
			}

			if (nextAfterComments == null) return null

			val fileHeaderCommentsRange: TextRange = UnfairTextRange(firstComment.getTextOffset(), nextAfterComments.textOffset - 1)
			if (fileHeaderCommentsRange.length > 1 &&
				document.getLineNumber(fileHeaderCommentsRange.endOffset) >
				document.getLineNumber(fileHeaderCommentsRange.startOffset)
			) {
				if (!containsCustomRegionMarker) {
					descriptors.add(FoldingDescriptor(squirrelFile, fileHeaderCommentsRange))
				}
				return fileHeaderCommentsRange
			}

			return null
		}

		@JvmStatic
		private fun foldComments(
			descriptors: MutableList<FoldingDescriptor>,
			psiElements: Collection<PsiElement>,
			fileHeaderRange: TextRange?
		) {
			var psiElement: PsiElement
			val iter = psiElements.iterator()
			while (iter.hasNext()) {
				psiElement = iter.next()
				if (psiElement !is PsiComment)
					continue
				if (fileHeaderRange != null && fileHeaderRange.intersects(psiElement.getTextRange()))
					continue

				val elementType = psiElement.getNode().elementType
				if ((elementType === SquirrelTokenTypesSets.MULTI_LINE_DOC_COMMENT || elementType === SquirrelTokenTypes.MULTI_LINE_COMMENT) && !isCustomRegionElement(psiElement)) {
					descriptors.add(FoldingDescriptor(psiElement, psiElement.getTextRange()))
				} else if (elementType === SquirrelTokenTypes.SINGLE_LINE_COMMENT) {
					var lastCommentInSequence = psiElement
					var nextElement = psiElement
					var containsCustomRegionMarker = isCustomRegionElement(nextElement)
					while (iter.hasNext() && (nextElement.nextSibling.also { nextElement = it }) != null &&
						(nextElement is PsiWhiteSpace || nextElement.node.elementType === elementType)
					) {
						if (nextElement.node.elementType === elementType) {
							// advance iterator to skip processed comments sequence
							iter.next()
							lastCommentInSequence = nextElement
							containsCustomRegionMarker = containsCustomRegionMarker or isCustomRegionElement(nextElement)
						}
					}

					if (lastCommentInSequence !== psiElement && !containsCustomRegionMarker) {
						val range =
							TextRange.create(psiElement.getTextOffset(), lastCommentInSequence.textRange.endOffset)
						descriptors.add(FoldingDescriptor(psiElement, range))
					}
				}
			}
		}

		@JvmStatic
		private fun foldClassBodies(descriptors: MutableList<FoldingDescriptor>, squirrelFile: SquirrelFile) {
			for (squirrelClass in PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelClass::class.java)) {
				val body = squirrelClass.classBody
				if (body != null && body.textLength > 2) {
					descriptors.add(FoldingDescriptor(body, body.textRange))
				}
			}
		}

		@JvmStatic
		private fun foldFunctionBodies(descriptors: MutableList<FoldingDescriptor>, squirrelFile: SquirrelFile) {
			for (squirrelFunction in PsiTreeUtil.findChildrenOfType(squirrelFile, SquirrelFunction::class.java)) {
				val body = squirrelFunction.functionBody
				if (body != null && body.textLength > 2) {
					descriptors.add(FoldingDescriptor(body, body.textRange))
				}
			}
		}
	}
}