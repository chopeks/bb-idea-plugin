package com.chopeks.configuration

import com.chopeks.SquirrelBundle.message
import com.chopeks.sdk.SquirrelSdkService
import com.chopeks.util.SquirrelConstants
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurableProvider
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project

class SquirrelConfigurableProvider(
	private val myProject: Project
) : ConfigurableProvider() {
	override fun createConfigurable(): Configurable {
		val sdkConfigurable = SquirrelSdkService.getInstance(myProject).createSdkConfigurable()
		return SquirrelCompositeConfigurable(sdkConfigurable!!)
	}

	private class SquirrelCompositeConfigurable(vararg configurables: Configurable) : SearchableConfigurable.Parent.Abstract() {
		private var myConfigurables: Array<out Configurable>?

		init {
			myConfigurables = configurables
		}

		override fun buildConfigurables() = myConfigurables
		override fun getId() = SquirrelConstants.SQUIRREL
		override fun getDisplayName() = message("squirrel.title")
		override fun getHelpTopic() = null

		override fun disposeUIResources() {
			super.disposeUIResources()
			myConfigurables = null
		}
	}
}
