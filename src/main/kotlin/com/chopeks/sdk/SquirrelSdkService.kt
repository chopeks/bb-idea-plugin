package com.chopeks.sdk

import com.chopeks.configuration.SquirrelSdkConfigurable
import com.chopeks.sdk.SquirrelSdkUtil.binDirExist
import com.chopeks.sdk.SquirrelSdkUtil.canMake
import com.chopeks.sdk.SquirrelSdkUtil.compilerExist
import com.chopeks.sdk.SquirrelSdkUtil.getCompilerPath
import com.chopeks.sdk.SquirrelSdkUtil.makeSquirrelCompiler
import com.chopeks.sdk.SquirrelSdkUtil.retrieveSquirrelVersion
import com.chopeks.sdk.SquirrelSdkUtil.sourcesExist
import com.chopeks.util.SquirrelConstants
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.SimpleModificationTracker
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.ObjectUtils

open class SquirrelSdkService(@JvmField protected val myProject: Project) : SimpleModificationTracker() {
	fun getSquirrelExecutablePath(module: Module?): String? {
		return getSquirrelExecutablePath(getSdkHomePath(module))
	}

	open fun getSdkHomePath(module: Module?): String? {
		return getSdkHomeLibPath(module)
	}

	private fun getSdkHomeLibPath(module: Module?): String? {
		val holder = ObjectUtils.notNull(module, myProject)
		return CachedValuesManager.getManager(myProject).getCachedValue(holder) {
			CachedValueProvider.Result.create(ApplicationManager.getApplication().runReadAction(Computable {
				val table = LibraryTablesRegistrar.getInstance().getLibraryTable(myProject)
				for (library in table.libraries) {
					val libraryName = library.name
					if (libraryName != null && libraryName.startsWith(LIBRARY_NAME)) {
						for (root in library.getFiles(OrderRootType.CLASSES)) {
							if (isSquirrelSdkLibRoot(root)) {
								return@Computable root.canonicalPath
							}
						}
					}
				}
				null
			}), this@SquirrelSdkService)
		}
	}

	open fun getSdkVersion(module: Module?): String? {
		val holder = ObjectUtils.notNull(module, myProject)
		return CachedValuesManager.getManager(myProject).getCachedValue(holder) {
			var result: String? = null
			val sdkHomePath = getSdkHomePath(module)
			if (sdkHomePath != null) {
				result = retrieveSquirrelVersion(sdkHomePath)
			}
			CachedValueProvider.Result.create(result, this@SquirrelSdkService)
		}
	}

	open fun chooseAndSetSdk(module: Module?) {
		ShowSettingsUtil.getInstance().editConfigurable(myProject, SquirrelSdkConfigurable(myProject, true))
	}

	fun createSdkConfigurable(): Configurable? {
		return if (!myProject.isDefault) SquirrelSdkConfigurable(myProject, false) else null
	}

	open fun isSquirrelModule(module: Module?): Boolean {
		return getSdkHomeLibPath(module) != null
	}

	companion object {
		val LOG: Logger = Logger.getInstance(SquirrelSdkService::class.java)

		@JvmStatic
		fun getInstance(project: Project): SquirrelSdkService {
			return ServiceManager.getService(project, SquirrelSdkService::class.java)
		}

		fun getSquirrelExecutablePath(sdkHomePath: String?): String? {
			if (sdkHomePath == null) return null

			if (!binDirExist(sdkHomePath)) {
				LOG.debug("$sdkHomePath/bin doesn't exist, checking linux-specific paths")
				return null
			}

			if (!compilerExist(sdkHomePath) && sourcesExist(sdkHomePath)) {
				if (!canMake()) {
					LOG.debug("Can't found make in PATH.")
					return null
				}

				makeSquirrelCompiler(sdkHomePath)
			}

			if (!compilerExist(sdkHomePath)) {
				LOG.debug("Compiler doesn't exists at $sdkHomePath/bin.")
				return null
			}

			val compiler = getCompilerPath(sdkHomePath)

			LOG.debug("Yay! Squirrel executable found at $compiler")
			return compiler
		}


		const val LIBRARY_NAME: String = SquirrelConstants.SDK_TYPE_ID

		@JvmStatic
		fun isSquirrelSdkLibRoot(root: VirtualFile): Boolean {
			return root.isInLocalFileSystem &&
					root.isDirectory && VfsUtilCore.findRelativeFile(SquirrelConstants.SQUIRREL_VERSION_FILE_PATH, root) != null
		}
	}
}
