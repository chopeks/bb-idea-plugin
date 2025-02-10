package com.chopeks.psi.impl

import com.chopeks.psi.*
import com.chopeks.psi.index.BBIndexes
import com.chopeks.psi.reference.BBResourceReference
import com.chopeks.psi.reference.SquirrelScriptReference
import com.chopeks.psi.reference.SquirrelStdIdPresentation
import com.chopeks.psi.reference.SquirrelStdIdReference
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.io.toNioPathOrNull
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType


internal val LOG by lazy(LazyThreadSafetyMode.PUBLICATION) {
	logger<SquirrelPsiImplUtil>()
}

object SquirrelPsiImplUtil {
	@JvmStatic
	fun getReference(element: SquirrelStringLiteral): PsiReference? {
		val text = element.string.text.trim('"')
		val looksLikeFile = "/" in text && " " !in text
		if (!looksLikeFile)
			return null

		if (BBIndexes.resourceFiles.any(text::endsWith))
			return BBResourceReference(element)

		if (text.startsWith("scripts"))
			return element.containingFile.getFile(("$text.nut").toNioPathOrNull())
				?.let { SquirrelScriptReference(it, element) }

		return element.containingFile.getFile(("scripts/$text.nut").toNioPathOrNull())
			?.let { SquirrelScriptReference(it, element) }
	}

	@JvmStatic
	fun getReference(element: SquirrelStdIdentifier) = SquirrelStdIdReference(element)

	@JvmStatic
	fun getPresentation(element: SquirrelStdIdentifier) = SquirrelStdIdPresentation(element)

	@JvmStatic
	fun getNameIdentifier(element: SquirrelStdIdentifier): PsiElement? {
		return element.identifier.node?.psi
	}

	@JvmStatic
	fun getName(element: SquirrelStdIdentifier?): String? {
		return element?.identifier?.node?.text
	}

	@JvmStatic
	fun getQualifiedName(element: SquirrelStdIdentifier?): String? {
		val parent = element?.parent?.parent
		if (parent is SquirrelTableItem) {
			return "${parent.qualifiedName}.${element.text}".replace("gt.", "::").replace("this.", "::")
		}
		if (parent is SquirrelReferenceExpression) {
			return parent.text.replace("gt.", "::").replace("this.", "::")
		}
//		LOG.warn("unknown name for parent, $parent")
		return element?.identifier?.text?.replace("gt.", "::")
	}

	@JvmStatic
	fun setName(element: SquirrelStdIdentifier, newName: String): PsiElement {
		return element // todo, i have no damn clue how to do that, calling element.setName causes recursion here
	}

	@JvmStatic
	fun isTable(element: SquirrelTableItem) = element.expression is SquirrelTableExpression

	@JvmStatic
	fun flatten(element: SquirrelTableItem): List<String> {
		return if (!element.isTable())
			listOf(element.key?.text ?: "")
		else {
			val ret = listOf(element.key?.text ?: "")
			ret + ((element.expression as? SquirrelTableExpression)?.flatten()
				?.map { "${element.key?.text}.$it" } ?: emptyList())
		}
	}

	@JvmStatic
	fun getQualifiedName(element: SquirrelTableItem?): String? {
		val tableExpression = element?.parent as? SquirrelTableExpression?
			?: return null
		val tableParent = tableExpression.parent
		if (tableParent is SquirrelTableItem)
			return "${tableParent.qualifiedName}.${tableParent.key?.text}"
		if (tableParent is SquirrelAssignExpression)
			return tableParent.expressionList.first().text
//		LOG.warn("wat is dat?, $element")
		if (element.parent?.parent is SquirrelTableItem) {
			val reference = PsiTreeUtil.getParentOfType(element.parent, SquirrelAssignExpression::class.java)
				?.expressionList?.firstOrNull()
			return "${reference?.text}.${element.parent?.text}"
		}
		return element.text
	}

	@JvmStatic
	fun flatten(element: SquirrelTableExpression): List<String> {
		return element.childrenOfType<SquirrelTableItem>().flatMap { it.flatten() }
	}
}
