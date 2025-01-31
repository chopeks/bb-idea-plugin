package com.chopeks.util

import com.chopeks.SquirrelTokenTypes
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Condition
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Function
import java.util.*
import kotlin.math.min

object UsefulPsiTreeUtil {
	fun findChildrenRange(elements: Array<ASTNode>, startOffset: Int, endOffset: Int): Array<ASTNode> {
		var i = findChildIndex(elements, startOffset)
		var j = findChildIndex(elements, endOffset)
		i = if (i == -1) 0 else i
		j = if (j == -1) elements.size else j
		// trim
		while (0 < j && j < elements.size && elements[j].elementType === TokenType.WHITE_SPACE) {
			--j
		}
		var to = j
		if (j < elements.size && !isSemicolon(elements[j])) {
			// try eat until ';'
			while (j + 1 < elements.size && (isSemicolon(elements[j + 1]) ||
						elements[j + 1].elementType === TokenType.WHITE_SPACE)
			) {
				++j
				if (isSemicolon(elements[j])) {
					to = j
				}
			}
		}
		to = min(elements.size.toDouble(), (to + 1).toDouble()).toInt()
		if (to < i) {
			return ASTNode.EMPTY_ARRAY
		}
		return Arrays.copyOfRange(elements, i, to)
	}

	fun isSemicolon(element: ASTNode): Boolean {
		return element.elementType === SquirrelTokenTypes.SEMICOLON || element.elementType === SquirrelTokenTypes.SEMICOLON_SYNTHETIC
	}

	private fun findChildIndex(children: Array<ASTNode>, offset: Int): Int {
		var i = 0
		val length = children.size
		while (i < length) {
			val child = children[i]
			if (child.textRange.contains(offset)) {
				return i
			}
			i++
		}

		return -1
	}

	fun isWhitespaceOrComment(element: PsiElement?): Boolean {
		return element is PsiWhiteSpace || element is PsiComment
	}

	fun getPathToParentOfType(
		element: PsiElement?,
		aClass: Class<out PsiElement?>
	): List<PsiElement>? {
		var element = element ?: return null
		val result: MutableList<PsiElement> = ArrayList()
		while (element != null) {
			result.add(element)
			if (aClass.isInstance(element)) {
				return result
			}
			if (element is PsiFile) return null
			element = element.parent
		}

		return null
	}

	fun getNextSiblingSkippingWhiteSpacesAndComments(sibling: PsiElement?): PsiElement? {
		return getSiblingSkippingCondition(
			sibling,
			Function { element -> element.nextSibling }, Condition { element -> isWhitespaceOrComment(element) }, true
		)
	}

	fun getPrevSiblingSkipWhiteSpacesAndComments(sibling: PsiElement?, strictly: Boolean): PsiElement? {
		return getPrevSiblingSkippingCondition(sibling, Condition { element -> isWhitespaceOrComment(element) }, strictly)
	}

	fun getPrevSiblingSkipWhiteSpacesAndComments(sibling: ASTNode?): ASTNode? {
		if (sibling == null) return null
		var result = sibling.treePrev
		while (result != null && isWhitespaceOrComment(result.psi)) {
			result = result.treePrev
		}
		return result
	}

	fun getPrevSiblingSkipWhiteSpaces(sibling: PsiElement?, strictly: Boolean): PsiElement? {
		return getPrevSiblingSkippingCondition(sibling, Condition { element -> element is PsiWhiteSpace }, strictly)
	}

	private fun getPrevSiblingSkippingCondition(
		sibling: PsiElement?,
		condition: Condition<PsiElement>,
		strictly: Boolean
	): PsiElement? {
		return getSiblingSkippingCondition(sibling, Function { element -> element.prevSibling }, condition, strictly)
	}

	private fun getSiblingSkippingCondition(
		sibling: PsiElement?,
		nextSibling: Function<PsiElement, PsiElement>,
		condition: Condition<PsiElement>,
		strictly: Boolean
	): PsiElement? {
		if (sibling == null) return null
		if (sibling is PsiFile) return sibling
		var result = if (strictly) nextSibling.`fun`(sibling) else sibling
		while (result != null && (result !is PsiFile) && condition.value(result)) {
			result = nextSibling.`fun`(result)
		}
		return result
	}

	fun isAncestor(element: PsiElement, children: List<PsiElement?>, strict: Boolean): Boolean {
		for (child in children) {
			if (child != null && !PsiTreeUtil.isAncestor(element, child, strict)) {
				return false
			}
		}
		return true
	}
}