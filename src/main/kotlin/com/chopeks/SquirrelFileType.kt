package com.chopeks

import com.intellij.openapi.fileTypes.LanguageFileType

class SquirrelFileType : LanguageFileType(SquirrelLanguage) {
	companion object {
		const val EXTENSION: String = "nut"
	}

	override fun getName() = "Squirrel file"
	override fun getDescription() = "Squirrel language file"
	override fun getDefaultExtension() = EXTENSION
	override fun getIcon() = SquirrelIcons.NUT_FILE
}