package com.chopeks.project.module

import com.chopeks.project.compose.HelloCompose
import com.chopeks.util.initializeComposeMainDispatcherChecker
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import javax.swing.JComponent


class ModWizardStep : ModuleBuilder() {
	override fun getModuleType() = ModuleType.EMPTY!!
	override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {

	}

	@OptIn(ExperimentalJewelApi::class)
	override fun createWizardSteps(wizardContext: WizardContext, modulesProvider: ModulesProvider): Array<ModuleWizardStep> {
		return arrayOf(object : ModuleWizardStep() {
			override fun getComponent(): JComponent {
				initializeComposeMainDispatcherChecker()
				enableNewSwingCompositing()
				return JewelComposePanel { HelloCompose() }
			}

			override fun updateDataModel() {

			}
		})
	}
}