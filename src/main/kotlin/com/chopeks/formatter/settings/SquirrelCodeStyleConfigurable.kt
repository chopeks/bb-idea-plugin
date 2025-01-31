package com.chopeks.formatter.settings

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.psi.codeStyle.CodeStyleSettings

class SquirrelCodeStyleConfigurable(settings: CodeStyleSettings, cloneSettings: CodeStyleSettings) : CodeStyleAbstractConfigurable(settings, cloneSettings, "Squirrel") {
	override fun createPanel(settings: CodeStyleSettings) = SquirrelCodeStyleMainPanel(currentSettings, settings)
	override fun getHelpTopic() = "reference.settingsdialog.codestyle.squirrel"
}
