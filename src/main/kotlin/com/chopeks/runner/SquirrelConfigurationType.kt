package com.chopeks.runner

import com.chopeks.SquirrelIcons
import com.intellij.compiler.options.CompileStepBeforeRun
import com.intellij.execution.BeforeRunTask
import com.intellij.execution.RunManagerEx
import com.intellij.execution.configuration.ConfigurationFactoryEx
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil.findConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationModule
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import org.jetbrains.annotations.NonNls

/**
 * Squirrel run configuration type.
 *
 * @author jansorg
 */
class SquirrelConfigurationType : ConfigurationTypeBase("SquirrelConfigurationType", "Squirrel", "Squirrel run configuration", SquirrelIcons.NUT_FILE) {
	init {
		addFactory(SquirrelConfigurationFactory(this))
	}

	private class SquirrelConfigurationFactory(configurationType: SquirrelConfigurationType) : ConfigurationFactoryEx<RunConfiguration>(configurationType) {
		override fun getId(): @NonNls String {
			return "SquirrelConfigurationType"
		}

		override fun onNewConfigurationCreated(configuration: RunConfiguration) {
			//the last param has to be false because we do not want a fallback to the template (we're creating it
			// right now) (avoiding a SOE)
			RunManagerEx.getInstanceEx(configuration.project).setBeforeRunTasks(configuration, emptyList())
		}

		override fun createTemplateConfiguration(project: Project): RunConfiguration {
			return SquirrelRunConfiguration(RunConfigurationModule(project), this, "")
		}

		override fun configureBeforeRunTaskDefaults(providerID: Key<out BeforeRunTask<*>?>, task: BeforeRunTask<*>) {
			super.configureBeforeRunTaskDefaults(providerID, task)
			if (providerID === CompileStepBeforeRun.ID) {
				task.isEnabled = false
			}
		}
	}

	companion object {
		val instance: SquirrelConfigurationType
			get() = findConfigurationType(SquirrelConfigurationType::class.java)
	}
}
