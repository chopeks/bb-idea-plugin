package com.chopeks

import com.intellij.openapi.fileTypes.LanguageFileType

class SquirrelFileType private constructor() : LanguageFileType(SquirrelLanguage) {
	override fun getName() = "Squirrel file"
	override fun getDescription() = "Squirrel language file"
	override fun getDefaultExtension() = EXTENSION
	override fun getIcon() = SquirrelIcons.NUT_FILE

	companion object {
		val INSTANCE: SquirrelFileType = SquirrelFileType()

		const val EXTENSION: String = "nut"
	}
}