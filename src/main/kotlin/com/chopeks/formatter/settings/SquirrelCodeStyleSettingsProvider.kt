package com.chopeks.formatter.settings

import com.chopeks.SquirrelBundle.message
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider

class SquirrelCodeStyleSettingsProvider : CodeStyleSettingsProvider() {
	override fun getConfigurableDisplayName() = message("squirrel.title")
	override fun createCustomSettings(settings: CodeStyleSettings) = SquirrelCodeStyleSettings(settings)
	override fun createSettingsPage(settings: CodeStyleSettings, originalSettings: CodeStyleSettings) =
		SquirrelCodeStyleConfigurable(settings, originalSettings)
}
