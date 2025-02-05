package com.chopeks.project.module

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopeks.util.initializeComposeMainDispatcherChecker
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.roots.ModifiableRootModel
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import javax.swing.JComponent


class VanillaModuleBuilder : ModuleBuilder() {
	override fun getModuleType() = ModuleTypes.vanilla()
	override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {}

	/** Replace default screen */
	@OptIn(ExperimentalJewelApi::class)
	override fun getCustomOptionsStep(context: WizardContext?, parentDisposable: Disposable?) = object : ModuleWizardStep() {
		override fun getComponent(): JComponent {
			initializeComposeMainDispatcherChecker()
			enableNewSwingCompositing()
			return JewelComposePanel {
				Column(Modifier.padding(16.dp)) {
					Text("Special purpose container for BB Vanilla", fontSize = 20.sp, style = JewelTheme.editorTextStyle)
					Text("Creating this module will add additional actions for vanilla.", fontSize = 12.sp, style = JewelTheme.editorTextStyle)
					Text("Do not forget to setup path to BBuilder in plugin settings.", fontSize = 12.sp, style = JewelTheme.editorTextStyle)
				}
			}
		}

		override fun updateDataModel() {

		}
	}
}
