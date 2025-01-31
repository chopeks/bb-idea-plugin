package com.chopeks.runner

import com.intellij.execution.ui.CommonProgramParametersPanel
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.MacroAwareTextBrowseFolderListener
import java.awt.BorderLayout
import javax.swing.JComponent

class SquirrelConfigForm : CommonProgramParametersPanel() {
	private var scriptNameComponent: LabeledComponent<JComponent>? = null
	private var scriptNameField: TextFieldWithBrowseButton? = null

	protected fun initOwnComponents() {
		val chooseScriptDescriptor = FileChooserDescriptorFactory.createSingleLocalFileDescriptor()
		scriptNameField = TextFieldWithBrowseButton()
		scriptNameField!!.addBrowseFolderListener(
			MacroAwareTextBrowseFolderListener(
				chooseScriptDescriptor,
				project
			)
		)

		scriptNameComponent = LabeledComponent.create(createComponentWithMacroBrowse(scriptNameField!!), "Script:")
		scriptNameComponent!!.labelLocation = BorderLayout.WEST
	}


	override fun addComponents() {
		initOwnComponents()

		add(scriptNameComponent)

		super.addComponents()

		setProgramParametersLabel("Compiler options:")
	}

	fun resetSquirrel(configuration: SquirrelRunConfiguration) {
		scriptNameField!!.text = configuration.scriptName ?: ""
	}

	fun applySquirrelTo(configuration: SquirrelRunConfiguration) {
		configuration.scriptName = scriptNameField!!.text
	}
}
