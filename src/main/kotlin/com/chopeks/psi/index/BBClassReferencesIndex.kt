package com.chopeks.psi.index

import com.chopeks.SquirrelFileType
import com.chopeks.psi.isBBClass
import com.chopeks.util.hooks
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.isFile
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ScalarIndexExtension
import com.intellij.util.io.EnumeratorStringDescriptor


/**
 * This thing allows to query files containing references
 */
class BBClassReferencesIndex : ScalarIndexExtension<String>() {
	override fun getName() = BBIndexes.BBClassReferences
	override fun getVersion() = 3
	override fun dependsOnFileContent() = true
	override fun getKeyDescriptor() = EnumeratorStringDescriptor.INSTANCE!!
	override fun getInputFilter() = FileBasedIndex.InputFilter { file ->
		file.isFile && file.name.endsWith(SquirrelFileType.EXTENSION)
	}

	override fun getIndexer() = object : DataIndexer<String, Void?, FileContent> {
		override fun map(inputData: FileContent): MutableMap<String, Void?> {
//			LOG.warn("BBClassReferencesIndex ${inputData.file.toNioPathOrNull()}")
			if (inputData.file.isDirectory)
				return mutableMapOf()
			val classFields = mutableMapOf<String, Void?>()
			ReadAction.run<Exception> {
				findFields(inputData.psiFile).forEach {
//					LOG.warn("BBClassReferencesIndex ${it}")
					classFields[it] = null
				}
			}
			return classFields
		}


		private fun findFields(file: PsiFile): List<String> {
			val projectPath = ProjectRootManager.getInstance(file.project).contentSourceRoots
				.firstOrNull { VfsUtilCore.isAncestor(it, file.virtualFile, false) }?.toNioPathOrNull()
				?: return emptyList()
			val path = file.virtualFile.toNioPathOrNull()
				?: return emptyList()
			if (file.isBBClass)
				return listOf(projectPath.relativize(path).toString())
			val list = mutableListOf<String>()
			file.hooks.hookDefinitions.forEach {
				list.add("${it.script}.nut".replace("/", "\\"))
			}
			return list
		}
	}
}