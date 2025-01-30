package com.chopeks.actions

import com.chopeks.SquirrelFileType
import com.chopeks.SquirrelIcons
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.DefaultCreateFromTemplateHandler
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.util.NlsContexts
import com.intellij.psi.PsiDirectory
import javax.swing.Icon

object NewSquirrelFileNameValidator : InputValidatorEx {
	override fun getErrorText(inputString: String): String? {
		if (inputString.trim().isEmpty())
			return "Empty file name"
		return null
	}

	override fun checkInput(inputString: String): Boolean = true

	override fun canClose(inputString: String): Boolean = getErrorText(inputString) == null
}

class NewSquirrelFileAction : CreateFileFromTemplateAction("Squirrel File", "Creates new Squirrel files", SquirrelIcons.NUT_FILE) {
	override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?) = "Squirrel File"

	override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
		builder.setTitle("New Squirrel File")
		SquirrelFileTemplate.entries.forEach {
			builder.addKind(it)
		}
		builder.setValidator(NewSquirrelFileNameValidator)
	}

	override fun startInWriteAction(): Boolean = false

	override fun hashCode(): Int = 0

	override fun equals(other: Any?): Boolean = other is NewSquirrelFileAction

	private fun CreateFileFromTemplateDialog.Builder.addKind(it: SquirrelFileTemplate) =
		addKind(it.title, it.icon, it.fileName)

	enum class SquirrelFileTemplate(@NlsContexts.ListItem val title: String, val icon: Icon, val fileName: String) {
		Empty("Empty .nut File", SquirrelIcons.NUT_FILE, "empty"),
		Class("New BB Class", SquirrelIcons.NUT_FILE, "class"),
		ClassInherit("New BB Class with inheritance", SquirrelIcons.NUT_FILE, "class_inherit"),
	}
}

class NewSquirrelCreateFromTemplateHandler : DefaultCreateFromTemplateHandler() {
	override fun prepareProperties(props: MutableMap<String, Any>) {
		val dirPath = props[FileTemplate.ATTRIBUTE_DIR_PATH] as? String
		if (!dirPath.isNullOrEmpty()) {
			// let's add current directory to template variables
			props["SCRIPT_PATH"] = dirPath.split('/').let {
				if ("scripts" in it) {
					it.slice(it.indexOf("scripts")..<it.size)
				} else {
					it
				}
			}.joinToString("/")
		}
	}

	override fun handlesTemplate(template: FileTemplate): Boolean =
		template.isTemplateOfType(SquirrelFileType.INSTANCE)
}