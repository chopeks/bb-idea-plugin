package com.chopeks

import com.chopeks.SquirrelBundle.message
import com.intellij.facet.ui.ValidationResult
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableModelsProvider
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.DirectoryProjectGenerator
import org.jetbrains.annotations.Nls
import javax.swing.Icon

class SquirrelProjectGenerator : DirectoryProjectGenerator<Any?> {
	override fun getName(): @Nls String {
		return message("squirrel.title")
	}

	override fun getLogo(): Icon? {
		return SquirrelIcons.SQUIRREL
	}

	override fun generateProject(project: Project, baseDir: VirtualFile, settings: Any, module: Module) {
		ApplicationManager.getApplication().runWriteAction {
			val modifiableModel = ModifiableModelsProvider.getInstance().getModuleModifiableModel(module)
			SquirrelModuleBuilder.setupProject(modifiableModel, baseDir)
			ModifiableModelsProvider.getInstance().commitModuleModifiableModel(modifiableModel)
		}
	}

	override fun validate(baseDirPath: String): ValidationResult {
		return ValidationResult.OK
	}
}
