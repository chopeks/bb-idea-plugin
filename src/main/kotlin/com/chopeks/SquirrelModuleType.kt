package com.chopeks

import com.chopeks.util.SquirrelConstants
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager

class SquirrelModuleType : ModuleType<SquirrelModuleBuilder>(MODULE_TYPE_ID) {
	override fun createModuleBuilder() = SquirrelModuleBuilder()
	override fun getName(): String = "Squirrel Module"
	override fun getDescription() = "Squirrel modules are used for developing <b>Squirrel</b> applications."
	override fun getNodeIcon(isOpened: Boolean) = SquirrelIcons.NUT_FILE

	companion object {
		const val MODULE_TYPE_ID: String = SquirrelConstants.MODULE_TYPE_ID

		@JvmStatic
		val instance: SquirrelModuleType
			get() = ModuleTypeManager.getInstance().findByID(MODULE_TYPE_ID) as SquirrelModuleType
	}
}