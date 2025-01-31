package com.chopeks.runner

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.DefaultProgramRunner

class SquirrelRunner : DefaultProgramRunner() {
	override fun getRunnerId() = SQUIRREL_RUNNER_ID

	override fun canRun(executorId: String, profile: RunProfile) =
		DefaultRunExecutor.EXECUTOR_ID == executorId && profile is SquirrelRunConfiguration

	companion object {
		const val SQUIRREL_RUNNER_ID: String = "SquirrelRunner"
	}
}