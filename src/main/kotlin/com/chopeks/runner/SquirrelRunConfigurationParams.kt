package com.chopeks.runner

import com.intellij.execution.CommonProgramRunConfigurationParameters

interface SquirrelRunConfigurationParams : CommonProgramRunConfigurationParameters {
	var scriptName: String?
}