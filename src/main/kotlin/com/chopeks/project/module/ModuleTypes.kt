package com.chopeks.project.module

import com.chopeks.SquirrelIcons
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ui.configuration.ModulesProvider

object ModuleTypes {

	fun mod() = Mod()
	fun vanilla() = Vanilla()

	class Mod : ModuleType<ModModuleBuilder>("mod-module") {
		override fun createModuleBuilder() = ModModuleBuilder()
		override fun getName() = "BB Mod Module"
		override fun getDescription() = "BB mod module"
		override fun getNodeIcon(isOpened: Boolean) = SquirrelIcons.SQUIRREL
	}

	class Vanilla : ModuleType<VanillaModuleBuilder>("vanilla-module") {
		override fun createModuleBuilder() = VanillaModuleBuilder()
		override fun getName() = "Vanilla Module"
		override fun getDescription() = "BB vanilla module"
		override fun getNodeIcon(isOpened: Boolean) = SquirrelIcons.SQUIRREL
		override fun createWizardSteps(wizardContext: WizardContext, moduleBuilder: VanillaModuleBuilder, modulesProvider: ModulesProvider): Array<ModuleWizardStep> {
			return moduleBuilder.createWizardSteps(wizardContext, modulesProvider)
		}
	}
}
