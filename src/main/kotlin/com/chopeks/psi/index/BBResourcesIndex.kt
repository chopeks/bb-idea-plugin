package com.chopeks.psi.index

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.isFile
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ScalarIndexExtension
import com.intellij.util.io.EnumeratorStringDescriptor

/**
 * This index returns resource files for relative path used
 */
class BBResourcesIndex : ScalarIndexExtension<String>() {
	override fun getName() = BBIndexes.BBResources
	override fun getVersion() = 3
	override fun dependsOnFileContent() = false
	override fun getKeyDescriptor() = EnumeratorStringDescriptor.INSTANCE!!
	override fun getInputFilter() = FileBasedIndex.InputFilter { file ->
		file.isFile && file.extension?.lowercase() in BBIndexes.resourceFiles
	}

	override fun getIndexer() = object : DataIndexer<String, Void?, FileContent> {
		override fun map(inputData: FileContent): MutableMap<String, Void?> {
			if (inputData.file.isDirectory)
				return mutableMapOf()
			return mutableMapOf<String, Void?>().also {
				mapFile(it, inputData.file)
			}
		}
	}

	private fun mapFile(classFields: MutableMap<String, Void?>, file: VirtualFile) {
		when (file.extension) {
			"png" -> mapImage(classFields, file)
			"ogg", "wav" -> mapSound(classFields, file)
		}
	}

	private fun mapImage(classFields: MutableMap<String, Void?>, file: VirtualFile) {
		when {
			"gfx/skills/" in file.path ->
				classFields["skills/${file.path.substringAfter("gfx/skills/")}"] = null

			"gfx/ui/items/" in file.path ->
				classFields[file.path.substringAfter("gfx/ui/items/")] = null

			"gfx/ui/" in file.path ->
				classFields["ui/${file.path.substringAfter("gfx/ui/")}"] = null
		}
	}

	private fun mapSound(classFields: MutableMap<String, Void?>, file: VirtualFile) {
		if ("sounds/" in file.path) {
			classFields["sounds/${file.path.substringAfter("sounds/")}"] = null
		}
		if ("music/" in file.path) {
			classFields["music/${file.path.substringAfter("music/")}"] = null
		}
	}
}