package com.chopeks.psi

import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findFile
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.rd.framework.base.deepClonePolymorphic
import okio.Path.Companion.toPath
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

/**
 * Checks if given file is either of:
 * 1. this.name <- {}
 * 2. this.name <- ::inherit("scripts/.../superclass.nut", {})
 * 3. this.name <- this.inherit("scripts/.../superclass.nut", {})
 */
val PsiFile.isBBClass: Boolean
	get() {
		// BB classes start with expression, but there can be comment as far I'm aware
		val expressions = children.filterIsInstance<SquirrelExpressionStatement>().deepClonePolymorphic()
		if (expressions.size > 1)
			return false
		val assign = expressions.firstOrNull()?.children?.firstOrNull()
		if (assign !is SquirrelAssignExpression)
			return false
		if (assign.children.size != 3)
			return false// not what we are looking for
		if (assign.children[0] !is SquirrelReferenceExpression)
			return false// reference should be first
		if (assign.children[1] !is SquirrelAssignmentOperator)
			return false// assignment should be second
		return true
	}

val PsiFile.inheritanceScript: SquirrelStringLiteral?
	get() {
		if (!isBBClass)
			return null
		val call = PsiTreeUtil.findChildOfType(this, SquirrelCallExpression::class.java).let {
			PsiTreeUtil.findChildOfType(it, SquirrelStdIdentifier::class.java)
		}
			?: return null
		if (call.text != "inherit")
			return null
		return PsiTreeUtil.findChildOfType(this, SquirrelStringLiteral::class.java)
	}

val PsiFile.sourceDirs: List<String>
	get() {
		val basePath = project.basePath!!.toPath(true)
		return ProjectRootManager.getInstance(project).contentSourceRoots.mapNotNull {
			it.path.toPath(true).relativeTo(basePath).toString()
		}
	}

fun PsiFile.checkIfFileExists(relativePath: String?) = try {
	sourceDirs
		.map { Paths.get(it, relativePath) }
		.any { project.guessProjectDir()?.toNioPathOrNull()?.resolve(it)?.exists() == true }
} catch (e: Exception) {
	false
}


fun PsiFile.checkIfFileExists(relativePath: Path?) =
	checkIfFileExists(relativePath?.toString())

fun PsiFile.getFile(relativePath: String?): VirtualFile? = try {
//	LOG.warn("relative path: $relativePath")
	sourceDirs
		.map { Paths.get(it, relativePath) }
		.firstOrNull { project.guessProjectDir()?.toNioPathOrNull()?.resolve(it)?.exists() == true }
		?.let { project.guessProjectDir()?.findFile(it.toString()) }
} catch (e: Exception) {
	null
}

fun PsiFile.getFile(relativePath: Path?) =
	getFile(relativePath?.toString())