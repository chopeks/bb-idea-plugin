package com.chopeks.util

import com.chopeks.psi.SquirrelCallExpression
import com.chopeks.psi.SquirrelStdIdentifier
import com.chopeks.psi.SquirrelStringLiteral
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil

val PsiFile.hooks
	get() = BBHookUtils(this)

class BBHookUtils(private val file: PsiFile) {
	val hookDefinitions: List<Pair<String, SquirrelCallExpression>>
		get() {
			val functionContainers = PsiTreeUtil.findChildrenOfType(file, SquirrelCallExpression::class.java).filter {
				PsiTreeUtil.findChildOfType(it, SquirrelStdIdentifier::class.java)?.let {
					it.text.startsWith("mods_hook")
				} == true
			}.mapNotNull {
				val script = PsiTreeUtil.findChildOfType(it, SquirrelStringLiteral::class.java)
					?: return@mapNotNull null
				"scripts/${script.text.trim('"')}" to it
			}
			// todo add modern hooks too
			return functionContainers
		}
}