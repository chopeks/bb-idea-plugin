package com.chopeks.runner

import com.chopeks.SquirrelBundle.message
import com.chopeks.sdk.SquirrelSdkService
import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configuration.AbstractRunConfiguration
import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.util.ProgramParametersUtil
import com.intellij.openapi.components.PathMacroManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.util.DefaultJDOMExternalizer
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.intellij.openapi.util.WriteExternalException
import com.intellij.openapi.util.text.StringUtil
import org.jdom.Element
import java.io.File
import java.util.*

/**
 * This code is based on the intellij-batch plugin.
 *
 * @author wibotwi, jansorg
 */
class SquirrelRunConfiguration(runConfigurationModule: RunConfigurationModule, configurationFactory: ConfigurationFactory, name: String?) :
	AbstractRunConfiguration(name, runConfigurationModule, configurationFactory), SquirrelRunConfigurationParams, RunConfigurationWithSuppressedDefaultDebugAction {
	private val interpreterOptions = ""
	private var workingDirectory: String? = ""
	override var scriptName: String? = null
	private var programsParameters: String? = null

	override fun getValidModules() =
		listOf(*ModuleManager.getInstance(project).modules)

	override fun excludeCompileBeforeLaunchOption() = false

	override fun getConfigurationEditor() =
		SquirrelRunConfigurationEditor(configurationModule.module)


	@Throws(ExecutionException::class)
	override fun getState(executor: Executor, env: ExecutionEnvironment) =
		SquirrelRunningState(this, env)

	@Throws(RuntimeConfigurationException::class)
	override fun checkConfiguration() {
		super.checkConfiguration()

		val module = configurationModule.module
		val project = project

		ProgramParametersUtil.checkWorkingDirectoryExist(this, getProject(), module)

		val executable = SquirrelSdkService.getInstance(project).getSquirrelExecutablePath(module)

		if (StringUtil.isEmptyOrSpaces(executable)) {
			throw RuntimeConfigurationException(message("squirrel.sdk.not.configured"))
		}

		if (StringUtil.isEmptyOrSpaces(scriptName)) {
			throw RuntimeConfigurationException(message("script.name.not.given"))
		}
	}

	override fun suggestedName(): String? {
		val name = (File(scriptName)).name

		val ind = name.lastIndexOf('.')
		if (ind != -1) {
			return name.substring(0, ind)
		}
		return name
	}

	@Throws(InvalidDataException::class)
	override fun readExternal(element: Element) {
		PathMacroManager.getInstance(project).expandPaths(element)
		super.readExternal(element)

		DefaultJDOMExternalizer.readExternal(this, element)
		readModule(element)
		EnvironmentVariablesComponent.readExternal(element, envs)

		// common config
		workingDirectory = JDOMExternalizerUtil.readField(element, "WORKING_DIRECTORY")

		val parentEnvValue = JDOMExternalizerUtil.readField(element, "PARENT_ENVS")
		if (parentEnvValue != null) {
			isPassParentEnvs = parentEnvValue.toBoolean()
		}

		// run config
		scriptName = JDOMExternalizerUtil.readField(element, "SCRIPT_NAME")
		programParameters = JDOMExternalizerUtil.readField(element, "PARAMETERS")
	}

	@Throws(WriteExternalException::class)
	override fun writeExternal(element: Element) {
		super.writeExternal(element)

		// common config
		JDOMExternalizerUtil.writeField(element, "WORKING_DIRECTORY", workingDirectory)
		JDOMExternalizerUtil.writeField(element, "PARENT_ENVS", isPassParentEnvs.toString())

		// run config
		JDOMExternalizerUtil.writeField(element, "SCRIPT_NAME", scriptName)
		JDOMExternalizerUtil.writeField(element, "PARAMETERS", programParameters)

		//JavaRunConfigurationExtensionManager.getInstance().writeExternal(this, element);
		DefaultJDOMExternalizer.writeExternal(this, element)
		writeModule(element)
		EnvironmentVariablesComponent.writeExternal(element, envs)

		PathMacroManager.getInstance(project).collapsePathsRecursively(element)
	}

	override fun getWorkingDirectory() = workingDirectory

	override fun setWorkingDirectory(workingDirectory: String?) {
		this.workingDirectory = workingDirectory
	}

	override fun getProgramParameters() = programsParameters

	override fun setProgramParameters(programParameters: String?) {
		this.programsParameters = programParameters
	}

	@get:Throws(ExecutionException::class)
	val compilerPath: String
		get() {
			val compilerPath = SquirrelSdkService.getInstance(project).getSquirrelExecutablePath(configurationModule.module) ?: throw ExecutionException(message("squirrel.sdk.not.configured"))
			return compilerPath
		}
}