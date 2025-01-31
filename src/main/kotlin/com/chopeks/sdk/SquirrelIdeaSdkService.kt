package com.chopeks.sdk

import com.chopeks.SquirrelModuleType
import com.intellij.ProjectTopics
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.*
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.ObjectUtils

class SquirrelIdeaSdkService(project: Project) : SquirrelSdkService(project) {
	init {
		myProject.messageBus.connect(project).subscribe(ProjectTopics.PROJECT_ROOTS, object : ModuleRootAdapter() {
			override fun rootsChanged(event: ModuleRootEvent) {
				incModificationCount()
			}
		})
	}

	override fun getSdkHomePath(module: Module?): String? {
		if (isSquirrelModule(module)) {
			val holder = ObjectUtils.notNull(module, myProject)
			return CachedValuesManager.getManager(myProject).getCachedValue(holder) {
				val sdk = getSquirrelSdk(module)
				CachedValueProvider.Result.create(sdk?.homePath, this@SquirrelIdeaSdkService)
			}
		} else {
			return super.getSdkHomePath(module)
		}
	}

	override fun getSdkVersion(module: Module?): String? {
		if (isSquirrelModule(module)) {
			val holder = ObjectUtils.notNull(module, myProject)
			return CachedValuesManager.getManager(myProject).getCachedValue(holder) {
				val sdk = getSquirrelSdk(module)
				CachedValueProvider.Result.create(sdk?.versionString, this@SquirrelIdeaSdkService)
			}
		} else {
			return super.getSdkVersion(module)
		}
	}

	override fun chooseAndSetSdk(module: Module?) {
		if (isSquirrelModule(module)) {
			val projectSdk = ProjectSettingsService.getInstance(myProject).chooseAndSetSdk()
			if (projectSdk == null && module != null) {
				ApplicationManager.getApplication().runWriteAction {
					if (!module.isDisposed) {
						ModuleRootModificationUtil.setSdkInherited(module)
					}
				}
			}
		} else {
			super.chooseAndSetSdk(module)
		}
	}

	override fun isSquirrelModule(module: Module?): Boolean {
		return module != null && ModuleUtil.getModuleType(module) === SquirrelModuleType.getInstance()
	}

	private fun getSquirrelSdk(module: Module?): Sdk? {
		if (module != null) {
			val sdk = ModuleRootManager.getInstance(module).sdk
			if (sdk != null && sdk.sdkType is SquirrelSdkType) {
				return sdk
			}
		}
		val sdk = ProjectRootManager.getInstance(myProject).projectSdk
		return if (sdk != null && sdk.sdkType is SquirrelSdkType) sdk else null
	}
}
