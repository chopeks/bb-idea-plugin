package com.chopeks.psi.index

import com.chopeks.SquirrelFileType
import com.chopeks.psi.isBBClass
import com.chopeks.psi.reference.BBClassPsiStorage
import com.chopeks.psi.reference.BBModdingHooksPsiStorage
import com.chopeks.util.hooks
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.isFile
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiFile
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import java.io.DataInput
import java.io.DataOutput

class BBClassSymbolsIndex : FileBasedIndexExtension<String, String>() {
	override fun getName() = BBIndexes.BBClassSymbols
	override fun getVersion() = 2
	override fun dependsOnFileContent() = true
	override fun getKeyDescriptor() = EnumeratorStringDescriptor.INSTANCE!!
	override fun getValueExternalizer(): DataExternalizer<String> {
		return object : DataExternalizer<String> {
			override fun save(out: DataOutput, value: String) {
				out.writeUTF(value)
			}

			override fun read(input: DataInput): String = input.readUTF()
		}
	}

	override fun getInputFilter() = FileBasedIndex.InputFilter { file ->
		file.isFile && file.name.endsWith(SquirrelFileType.EXTENSION)
	}

	override fun getIndexer() = object : DataIndexer<String, String, FileContent> {
		override fun map(inputData: FileContent): MutableMap<String, String> {
			if (inputData.file.isDirectory)
				return mutableMapOf()
//			LOG.warn("BBClassSymbolsIndex ${inputData.file.toNioPathOrNull()}")
			val classFields = mutableMapOf<String, String>()
			ReadAction.run<Exception> {
				findFields(inputData.psiFile)?.forEach { pair ->
					if (pair.second.isNotEmpty()) {
						classFields[pair.first] = pair.second.joinToString("$$")
					}
				}
			}
			return classFields
		}


		private fun findFields(file: PsiFile): MutableList<Pair<String, List<String>>>? {
			val projectPath = ProjectRootManager.getInstance(file.project).contentSourceRoots
				.firstOrNull { VfsUtilCore.isAncestor(it, file.virtualFile, false) }?.toNioPathOrNull()
				?: return null
			val path = file.virtualFile.toNioPathOrNull()
				?: return null
			val relative = projectPath.relativize(path).toString()
			val files = mutableListOf<Pair<String, List<String>>>()
			val fields = mutableListOf<String>()
			if (file.isBBClass) {
				BBClassPsiStorage(file).also {
					it.mTableIds.forEach { fields.add("m_${it.key}") }
					it.functionIds.forEach { fields.add("fn_${it.key}") }
				}
				files.add(relative to fields.toSet().toList())
			} else {
				file.hooks.hookDefinitions.forEach {
					fields.clear()
					BBModdingHooksPsiStorage(it.second).also {
						it.mTableIds.forEach { fields.add("m_${it.key}") }
						it.functionIds.forEach { fields.add("fn_${it.key}") }
					}
					files.add("${it.first}.nut".replace("/", "\\") to fields.toSet().toList())
				}
			}
			return files
		}
	}
}
