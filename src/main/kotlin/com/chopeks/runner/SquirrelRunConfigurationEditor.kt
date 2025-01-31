package com.chopeks.runner

import com.intellij.openapi.module.Module
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SettingsEditor
import javax.swing.JComponent

class SquirrelRunConfigurationEditor(module: Module?) : SettingsEditor<SquirrelRunConfiguration>() {
	private var form: SquirrelConfigForm?

	init {
		this.form = SquirrelConfigForm()
		form!!.setModuleContext(module)
	}

	override fun resetEditorFrom(runConfiguration: SquirrelRunConfiguration) {
		form!!.reset(runConfiguration)
		form!!.resetSquirrel(runConfiguration)
	}

	@Throws(ConfigurationException::class)
	override fun applyEditorTo(runConfiguration: SquirrelRunConfiguration) {
		form!!.applyTo(runConfiguration)
		form!!.applySquirrelTo(runConfiguration)
	}

	override fun createEditor(): JComponent {
		return form!!
	}

	override fun disposeEditor() {
		form = null
	}
}