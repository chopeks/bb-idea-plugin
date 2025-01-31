package com.chopeks.configuration

import com.chopeks.SquirrelModuleType
import com.intellij.openapi.module.ModuleConfigurationEditor
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ui.configuration.*
import javax.swing.JComponent

class SquirrelModuleEditorsProvider : ModuleConfigurationEditorProvider {
	override fun createEditors(state: ModuleConfigurationState): Array<ModuleConfigurationEditor> {
		val rootModel = state.modifiableRootModel
		val module = rootModel.module
		if (ModuleType.get(module) !is SquirrelModuleType)
			return ModuleConfigurationEditor.EMPTY

		val moduleName = module.name
		val editors: MutableList<ModuleConfigurationEditor> = ArrayList()
		editors.add(ContentEntriesEditor(moduleName, state))
		editors.add(OutputEditorEx(state))
		editors.add(ClasspathEditor(state))
		return editors.toTypedArray<ModuleConfigurationEditor>()
	}

	internal class OutputEditorEx(state: ModuleConfigurationState?) : OutputEditor(state) {
		override fun createComponentImpl(): JComponent {
			val component = super.createComponentImpl()
			component.remove(1)
			return component
		}
	}
}
