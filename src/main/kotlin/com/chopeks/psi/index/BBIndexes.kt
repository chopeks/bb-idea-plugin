package com.chopeks.psi.index

import com.chopeks.psi.impl.LOG
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.ID

object BBIndexes {
	val Inheritance = ID.create<String, String>("${javaClass.packageName}.InheritanceIndex")
	val BBClassSymbols = ID.create<String, String>("${javaClass.packageName}.BBClassSymbols")
	val BBClassReferences = ID.create<String, Void?>("${javaClass.packageName}.BBClassReferences")

	fun querySymbols(file: PsiFile): List<String> {
		val projectPath = ProjectRootManager.getInstance(file.project).contentSourceRoots
			.firstOrNull { VfsUtilCore.isAncestor(it, file.virtualFile, false) }?.toNioPathOrNull()
			?: return emptyList<String>().also { LOG.error("Cannot find project") }
		val path = file.virtualFile.toNioPathOrNull()
			?: return emptyList<String>().also { LOG.error("Cannot find virtualFile") }
		val relative = projectPath.relativize(path).toString()

		return FileBasedIndex.getInstance().getValues(BBClassSymbols, relative, GlobalSearchScope.allScope(file.project)).joinToString("$$")
			.split("$$")
	}

	fun querySymbolFiles(file: PsiFile): List<PsiFile> {
		val projectPath = ProjectRootManager.getInstance(file.project).contentSourceRoots
			.firstOrNull { VfsUtilCore.isAncestor(it, file.virtualFile, false) }?.toNioPathOrNull()
			?: return emptyList()
		val path = file.virtualFile.toNioPathOrNull()
			?: return emptyList()
		val relative = projectPath.relativize(path).toString()
		val files = mutableListOf<PsiFile>()
		FileBasedIndex.getInstance().getFilesWithKey(BBClassReferences, setOf(relative), {
			it.findPsiFile(file.project)?.let(files::add); true
		}, GlobalSearchScope.allScope(file.project))
		return files.also { LOG.warn("queryClass files: ${it.joinToString { it.virtualFile.toString() }}") }
	}

}