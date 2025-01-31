package com.chopeks

import com.chopeks.SquirrelBundle.message
import com.chopeks.SquirrelModuleType.Companion.instance
import com.chopeks.sdk.SquirrelSdkType
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.vfs.VirtualFile
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

class SquirrelModuleBuilder : ModuleBuilder() {
	override fun getName() = message("squirrel.title")
	override fun getPresentableName() = message("squirrel.title")
	override fun getDescription() = message("squirrel.project.description")
	override fun getNodeIcon() = SquirrelIcons.SQUIRREL
	override fun getModuleType() = instance
	override fun getParentGroup() = SquirrelModuleType.MODULE_TYPE_ID
	override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable) = null
	override fun isSuitableSdkType(sdkType: SdkTypeId) = sdkType is SquirrelSdkType

	@Throws(ConfigurationException::class)
	override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
		val contentEntry = doAddContentEntry(modifiableRootModel)
		val baseDir = contentEntry?.file
		if (baseDir != null) {
			setupProject(modifiableRootModel, baseDir)
		}
	}

	companion object {
		fun setupProject(modifiableRootModel: ModifiableRootModel, baseDir: VirtualFile) {
			try {
				val mainFile = baseDir.createChildData(null, modifiableRootModel.module.name.lowercase() + ".nut")
				mainFile.setBinaryContent(
					("""function main() {
  ::print("Hello, World!");
}

main();""").toByteArray(Charset.forName("UTF-8"))
				)
				scheduleFilesOpeningAndPubGet(modifiableRootModel.module, Arrays.asList(mainFile))
				// TODO setup run configuration
			} catch (ignore: IOException) { /*unlucky*/
			}
		}

		private fun scheduleFilesOpeningAndPubGet(module: Module, files: Collection<VirtualFile>) {
			runWhenNonModalIfModuleNotDisposed({
				val manager = FileEditorManager.getInstance(module.project)
				for (file in files) {
					manager.openFile(file, true)
				}
			}, module)
		}

		fun runWhenNonModalIfModuleNotDisposed(runnable: Runnable, module: Module) {
			StartupManager.getInstance(module.project).runWhenProjectIsInitialized {
				if (ApplicationManager.getApplication().currentModalityState === ModalityState.NON_MODAL) {
					runnable.run()
				} else {
					ApplicationManager.getApplication().invokeLater(runnable, ModalityState.NON_MODAL) { module.isDisposed }
				}
			}
		}
	}
}
