package com.chopeks

import com.chopeks.sdk.SquirrelSdkType
import com.chopeks.util.SquirrelConstants
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.ProjectJdkForModuleStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.roots.ui.configuration.ModulesProvider

class SquirrelModuleType : ModuleType<SquirrelModuleBuilder>(MODULE_TYPE_ID) {
	override fun createModuleBuilder() = SquirrelModuleBuilder()
	override fun getName(): String = "Squirrel Module"
	override fun getDescription() = "Squirrel modules are used for developing <b>Squirrel</b> applications."
	override fun getNodeIcon(isOpened: Boolean) = SquirrelIcons.NUT_FILE

	override fun createWizardSteps(
		wizardContext: WizardContext,
		moduleBuilder: SquirrelModuleBuilder,
		modulesProvider: ModulesProvider
	): Array<ModuleWizardStep> {
		return arrayOf(object : ProjectJdkForModuleStep(wizardContext, SquirrelSdkType.instance) {
			override fun updateDataModel() {
				super.updateDataModel()
				moduleBuilder.moduleJdk = jdk
			}
		})
	}

	companion object {
		const val MODULE_TYPE_ID: String = SquirrelConstants.MODULE_TYPE_ID

		@JvmStatic
		val instance: SquirrelModuleType
			get() = ModuleTypeManager.getInstance().findByID(MODULE_TYPE_ID) as SquirrelModuleType
	}
}