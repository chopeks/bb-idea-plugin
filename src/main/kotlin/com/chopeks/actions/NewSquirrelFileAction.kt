package com.chopeks.actions

import com.chopeks.SquirrelBundle
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

class NewSquirrelFileAction : CreateFileFromTemplateAction(SquirrelBundle.message("newfile.command.name"), SquirrelBundle.message("newfile.dialog.prompt"), SquirrelIcons.NUT_FILE) {
	override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?) =
		SquirrelBundle.message("newfile.command.name")

	@OptIn(ExperimentalStdlibApi::class)
	override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
		builder.setTitle(SquirrelBundle.message("newfile.dialog.title"))
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
		Empty(SquirrelBundle.message("newfile.list.empty"), SquirrelIcons.NUT_FILE, "empty"),
		Class(SquirrelBundle.message("newfile.list.class"), SquirrelIcons.NUT_FILE, "class"),
		ClassInherit(SquirrelBundle.message("newfile.list.class_inherit"), SquirrelIcons.NUT_FILE, "class_inherit"),
	}
}

class NewSquirrelCreateFromTemplateHandler : DefaultCreateFromTemplateHandler() {
	@OptIn(ExperimentalStdlibApi::class)
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