package com.chopeks.runner

import com.chopeks.psi.SquirrelFile
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiElement

/**
 * Squirrel run config producer which looks at the current context to create a new run configuation.
 */
class SquirrelRunConfigProducer : RunConfigurationProducer<SquirrelRunConfiguration>(SquirrelConfigurationType.getInstance()) {
	override fun setupConfigurationFromContext(configuration: SquirrelRunConfiguration, context: ConfigurationContext, sourceElement: Ref<PsiElement>): Boolean {
		val location = context.location ?: return false

		val psiElement = location.psiElement
		if (!psiElement.isValid) {
			return false
		}

		val psiFile = psiElement.containingFile
		if (psiFile == null || psiFile !is SquirrelFile) {
			return false
		}

		val file = location.virtualFile ?: return false

		sourceElement.set(psiFile)

		configuration.name = location.virtualFile!!.presentableName
		configuration.scriptName = file.path

		if (file.parent != null) {
			configuration.workingDirectory = file.parent.path
		}

		val module = context.module
		if (module != null) {
			configuration.setModule(module)
		}

		return true
	}

	override fun isConfigurationFromContext(configuration: SquirrelRunConfiguration, context: ConfigurationContext): Boolean {
		val location = context.location ?: return false

		//fixme file checks needs to check the properties
		val file = location.virtualFile

		return file != null && FileUtil.pathsEqual(file.path, configuration.scriptName)
	}
}
