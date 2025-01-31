package com.chopeks

import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory

class SquirrelFileTypeFactory : FileTypeFactory() {
	override fun createFileTypes(fileTypeConsumer: FileTypeConsumer) {
		fileTypeConsumer.consume(SquirrelFileType.INSTANCE, SquirrelFileType.EXTENSION)
	}
}