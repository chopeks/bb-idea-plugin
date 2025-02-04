package com.chopeks.actions

import com.chopeks.project.compose.HelloCompose
import com.chopeks.util.initializeComposeMainDispatcherChecker
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.enableNewSwingCompositing
import javax.swing.JComponent

// just some tests to figure out compose
class NewProjectAction : DumbAwareAction() {
	override fun actionPerformed(event: AnActionEvent) {
		val customDialog = CustomDialog(event.project)
		customDialog.showAndGet() // Show the dialog
	}
}

class CustomDialog(project: Project?) : DialogWrapper(project) {
	init {
		init() // Initialize the DialogWrapper
		title = "Custom Dialog" // Dialog title
	}

	@OptIn(ExperimentalJewelApi::class)
	override fun createCenterPanel(): JComponent {
		initializeComposeMainDispatcherChecker()
		enableNewSwingCompositing()
		return JewelComposePanel {
			HelloCompose()
		}
	}
}