package com.chopeks.runner

import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.ConsoleView

class SquirrelRunningState(private val runConfig: SquirrelRunConfiguration, environment: ExecutionEnvironment?) : CommandLineState(environment) {
	@Throws(ExecutionException::class)
	override fun startProcess(): ProcessHandler {
		val commandLine = command
		return OSProcessHandler(commandLine.createProcess(), commandLine.commandLineString)
	}

	@get:Throws(ExecutionException::class)
	private val command: GeneralCommandLine
		get() {
			val commandLine = GeneralCommandLine()
			setExePath(commandLine)
			setWorkDirectory(commandLine)
			setParameters(commandLine)
			setScript(commandLine)
			val consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(runConfig.project)
			setConsoleBuilder(consoleBuilder)
			return commandLine
		}

	@Throws(ExecutionException::class)
	fun setExePath(commandLine: GeneralCommandLine) {
		commandLine.exePath = runConfig.compilerPath
	}

	fun setWorkDirectory(commandLine: GeneralCommandLine) {
		commandLine.withWorkDirectory(runConfig.workingDirectory)
	}

	@Throws(ExecutionException::class)
	fun setParameters(commandLine: GeneralCommandLine) {
		val parameters = runConfig.programParameters
		if (parameters != null && parameters !== "") {
			commandLine.withParameters(parameters)
		}
	}

	@Throws(ExecutionException::class)
	fun setScript(commandLine: GeneralCommandLine) {
		commandLine.withParameters(runConfig.scriptName ?: "")
	}

	fun createConsoleView(executor: Executor?): ConsoleView {
		val consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(runConfig.project)
		return consoleBuilder.console
	}
}