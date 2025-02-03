package com.chopeks.psi.index

import com.chopeks.SquirrelFileType
import com.chopeks.psi.inheritanceScript
import com.chopeks.psi.isBBClass
import com.intellij.openapi.util.io.toNioPathOrNull
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import java.io.DataInput
import java.io.DataOutput

class SquirrelInheritanceIndex : FileBasedIndexExtension<String, String>() {
	override fun getName() = BBIndexes.Inheritance
	override fun getVersion() = 1
	override fun dependsOnFileContent() = true
	override fun getKeyDescriptor() = EnumeratorStringDescriptor.INSTANCE!!

	override fun getInputFilter() = FileBasedIndex.InputFilter { file ->
		file.name.endsWith(SquirrelFileType.EXTENSION)
	}

	override fun getValueExternalizer() = object : DataExternalizer<String> {
		override fun save(out: DataOutput, value: String) {
			out.writeUTF(value)
		}

		override fun read(`in`: DataInput): String {
			return `in`.readUTF()
		}
	}

	override fun getIndexer() = object : DataIndexer<String, String, FileContent> {
		override fun map(inputData: FileContent): MutableMap<String, String> {
			val file = inputData.psiFile
			if (!file.isBBClass)
				return mutableMapOf()
			val inheritance = file.inheritanceScript?.text
				?: return mutableMapOf()
			val projectPath = file.project.basePath?.toNioPathOrNull()
				?: return mutableMapOf()
			val path = file.virtualFile.toNioPathOrNull()
				?: return mutableMapOf()
			val x = projectPath.relativize(path).toString()
			return mutableMapOf(x to inheritance)
		}
	}
}