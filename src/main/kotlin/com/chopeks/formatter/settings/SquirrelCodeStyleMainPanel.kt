package com.chopeks.formatter.settings

import com.chopeks.SquirrelLanguage
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.CodeStyleSettings

open class SquirrelCodeStyleMainPanel(currentSettings: CodeStyleSettings?, settings: CodeStyleSettings) : TabbedLanguageCodeStylePanel(SquirrelLanguage, currentSettings, settings)
