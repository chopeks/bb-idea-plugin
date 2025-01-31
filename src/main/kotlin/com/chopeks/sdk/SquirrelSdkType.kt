package com.chopeks.sdk

import com.chopeks.SquirrelBundle.message
import com.chopeks.SquirrelIcons
import com.chopeks.sdk.SquirrelSdkUtil.adjustSdkPath
import com.chopeks.sdk.SquirrelSdkUtil.getSdkDirectoriesToAttach
import com.chopeks.sdk.SquirrelSdkUtil.retrieveSquirrelVersion
import com.chopeks.sdk.SquirrelSdkUtil.suggestSdkDirectory
import com.chopeks.util.SquirrelConstants
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.roots.OrderRootType
import org.jdom.Element
import java.io.File

class SquirrelSdkType : SdkType(SquirrelConstants.SDK_TYPE_ID) {
	override fun getIcon() = SquirrelIcons.SQUIRREL
	override fun getIconForAddAction() = icon

	override fun suggestHomePath(): String? {
		val suggestSdkDirectory = suggestSdkDirectory()
		return suggestSdkDirectory?.path
	}

	override fun isValidSdkHome(path: String): Boolean {
		SquirrelSdkService.LOG.debug("Validating sdk path: $path")
		val executablePath = SquirrelSdkService.getSquirrelExecutablePath(path)
		if (executablePath == null) {
			SquirrelSdkService.LOG.debug("Squirrel executable is not found.")
			return false
		}
		if (!File(executablePath).canExecute()) {
			SquirrelSdkService.LOG.debug("Squirrel binary cannot be executed: $path")
			return false
		}
		if (getVersionString(path) != null) {
			SquirrelSdkService.LOG.debug("Found valid sdk: $path")
			return true
		}
		return false
	}

	override fun adjustSelectedSdkHome(homePath: String): String {
		return adjustSdkPath(homePath)
	}

	override fun suggestSdkName(currentSdkName: String?, sdkHome: String): String {
		val version = getVersionString(sdkHome) ?: return message("squirrel.sdk.unknown.version.at", sdkHome)
		return message("squirrel.version", version)
	}

	override fun getVersionString(sdkHome: String): String? {
		return retrieveSquirrelVersion(sdkHome)
	}

	override fun getDefaultDocumentationUrl(sdk: Sdk) = "http://squirrel-lang.org/doc/squirrel3.html"


	override fun createAdditionalDataConfigurable(sdkModel: SdkModel, sdkModificator: SdkModificator) = null


	override fun saveAdditionalData(additionalData: SdkAdditionalData, additional: Element) {
	}

	override fun getPresentableName() = message("squirrel.sdk")


	override fun setupSdkPaths(sdk: Sdk) {
		val versionString = sdk.versionString ?: throw RuntimeException(message("squirrel.sdk.version.undefined"))

		val modificator = sdk.sdkModificator
		val path = sdk.homePath ?: return
		modificator.homePath = path

		for (file in getSdkDirectoriesToAttach(path, versionString)) {
			modificator.addRoot(file, OrderRootType.CLASSES)
			modificator.addRoot(file, OrderRootType.SOURCES)
		}
		modificator.commitChanges()
	}

	companion object {
		@JvmStatic
		val instance: SquirrelSdkType
			get() = findInstance(SquirrelSdkType::class.java)

	}
}
