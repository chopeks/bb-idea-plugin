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
import com.intellij.psi.util.childrenOfType
import java.util.concurrent.ConcurrentHashMap


internal val LOG by lazy(LazyThreadSafetyMode.PUBLICATION) {
	logger<SquirrelPsiImplUtil>()
}

object SquirrelPsiImplUtil {
	private val lookupScripts = ConcurrentHashMap<SquirrelStringLiteral, SquirrelScriptReference>()

	@JvmStatic
	fun getReference(element: SquirrelStringLiteral): PsiReference? {
		val text = element.string.text.trim('"')
		val looksLikeFile = "/" in text && " " !in text
		if (!looksLikeFile)
			return null

		if (BBIndexes.resourceFiles.any(text::endsWith))
			return BBResourceReference(element)

		if (text.startsWith("scripts"))
			return lookupScripts[element] ?: element.containingFile.getFile(("$text.nut").toNioPathOrNull())
				?.let { SquirrelScriptReference(it, element) }
				?.also {
					if (!lookupScripts.containsKey(element))
						lookupScripts.putIfAbsent(element, it)
				}

		return lookupScripts[element] ?: element.containingFile.getFile(("scripts/$text.nut").toNioPathOrNull())
			?.let { SquirrelScriptReference(it, element) }
			?.also {
				if (!lookupScripts.containsKey(element))
					lookupScripts.putIfAbsent(element, it)
			}
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
	fun flatten(element: SquirrelTableExpression): List<String> {
		return element.childrenOfType<SquirrelTableItem>().flatMap { it.flatten() }
	}


}
