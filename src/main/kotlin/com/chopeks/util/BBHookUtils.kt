package com.chopeks.util

import com.chopeks.psi.*
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil

val PsiFile.hooks
	get() = BBHookUtils(this)

class BBHookUtils(private val file: PsiFile) {
	val hookDefinitions: List<HookContainer>
		get() {
			val functionContainers = PsiTreeUtil.findChildrenOfType(file, SquirrelCallExpression::class.java).filter {
				it.expression.text.let { ref ->
					ref.startsWith("::mods_hook")
							// and modern hooks
							|| ref.endsWith(".hook")
							|| ref.endsWith(".rawHook")
							|| ref.endsWith(".hookTree") // todo this will be wonky af i think...
				}
			}.mapNotNull {
				HookContainer(it)
			}
			return functionContainers
		}

	fun findHookClass(element: SquirrelStdIdentifier): PsiFile? {
		return hookDefinitions.firstOrNull { PsiTreeUtil.isAncestor(it.hookContainer, element, true) }
			?.let { it.scriptRef?.resolve()?.containingFile }
	}
}

class HookContainer(
	private val container: SquirrelCallExpression
) {
	/**
	 * Normalized script name
	 */
	val script: String?
		get() = PsiTreeUtil.findChildOfType(container.arguments.argumentList, SquirrelStringLiteral::class.java)?.text?.trim('"')?.let {
			if (it.startsWith("scripts")) it else "scripts/$it"
		}

	/**
	 * Hooked script file reference
	 */
	val scriptRef: PsiReference?
		get() = PsiTreeUtil.findChildOfType(container.arguments.argumentList, SquirrelStringLiteral::class.java)?.reference

	/**
	 * Stores script function parameter, so what by convention should be 'o', or 'q'
	 */
	val hookedObjectRef: String = PsiTreeUtil.findChildOfType(container.arguments.argumentList, SquirrelParameterList::class.java)?.let {
		PsiTreeUtil.findChildOfType(it, SquirrelStdIdentifier::class.java)?.text
	}.toString()


	/**
	 * Shortcut to hook function
	 */
	val hookContainer: SquirrelFunctionBody?
		get() = PsiTreeUtil.findChildOfType(container.arguments.argumentList, SquirrelFunctionBody::class.java)
//			.also { LOG.warn("HookContainer found $it") }
}