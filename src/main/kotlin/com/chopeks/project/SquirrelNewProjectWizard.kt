package com.chopeks.project

import com.chopeks.SquirrelIcons
import com.chopeks.project.compose.HelloCompose
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.language.LanguageGeneratorNewProjectWizard
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.util.UserDataHolder
import com.intellij.ui.dsl.builder.Panel
import org.jetbrains.jewel.bridge.JewelComposePanel
import javax.swing.Icon

class SquirrelNewProjectWizard : LanguageGeneratorNewProjectWizard {

	override val icon: Icon
		get() = SquirrelIcons.SQUIRREL

	override val name: String
		get() = "BB modding eh?"

	override fun createStep(parent: NewProjectWizardStep): NewProjectWizardStep {
		return object : NewProjectWizardStep {
			// Store the context (will contain project-specific data)
			override val context: WizardContext
				get() = parent.context

			// This will hold user input data, such as project configurations
			override val data: UserDataHolder
				get() = parent.data

			// Define the keywords or properties for your new project (e.g., template options)
			override val keywords: NewProjectWizardStep.Keywords
				get() = NewProjectWizardStep.Keywords()

			// Define the property graph for the wizard state
			override val propertyGraph: PropertyGraph
				get() = parent.propertyGraph

			override fun setupUI(builder: Panel) {
				builder.panel {
					JewelComposePanel {
						HelloCompose()
					}
				}
			}

		}
	}
}
