package com.chopeks.inspection

import com.chopeks.SquirrelFileType
import com.chopeks.psi.*
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil

class BBClassInspection : LocalInspectionTool() {
	override fun getStaticDescription() = "BB Class Inspection"

	override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor> {
		val problemDescriptors = mutableListOf<ProblemDescriptor>()
		checkDuplicatedFunctions(file, problemDescriptors, manager, isOnTheFly)
		checkClassName(file, problemDescriptors, manager, isOnTheFly)
		return problemDescriptors.toTypedArray()
	}

	private fun checkDuplicatedFunctions(file: PsiFile, problemDescriptors: MutableList<ProblemDescriptor>, manager: InspectionManager, isOnTheFly: Boolean) {
		if (!file.isBBClass)
			return
		val seenFunctions = mutableSetOf<String>()
		val table = PsiTreeUtil.findChildOfType(file, SquirrelTableExpression::class.java)
			?: return
		val functions = table.tableItemList.mapNotNull { it.functionDeclaration }

		for (function in functions) {
			val functionName = function.functionName?.text ?: ""
			if (!seenFunctions.add(functionName)) {
				problemDescriptors.add(manager.error(function.functionName!!, "Duplicated function '$functionName' definition.", isOnTheFly))
			}
		}
	}

	private fun checkClassName(file: PsiFile, problemDescriptors: MutableList<ProblemDescriptor>, manager: InspectionManager, isOnTheFly: Boolean) {
		if (!file.isBBClass)
			return
		checkInheritance(file, problemDescriptors, manager, isOnTheFly)

		val assign = PsiTreeUtil.findChildOfType(file, SquirrelExpressionStatement::class.java)?.children?.firstOrNull()
			?: return

		val className = assign.children[0].children.lastOrNull()?.children?.firstOrNull { it is SquirrelStdIdentifier }
			?: return

		val assigned = assign.children[2]
		if (assigned is SquirrelTableExpression) { // new object case
			if (className.text != file.name.split(".").first()) {
				problemDescriptors.add(manager.error(className, "'${className.text}' is different than file name.", isOnTheFly))
			}
		} else if (assigned is SquirrelCallExpression) { // inheritance case
			if (assigned.children[0] !is SquirrelReferenceExpression)
				return
			val reference = assigned.children[0].children.lastOrNull() ?: return
			if (reference.text.endsWith("inherit") && className.text != file.name.split(".").first()) {
				problemDescriptors.add(manager.error(className, "'${className.text}' is different than file name.", isOnTheFly))
			}
		} else return
	}

	private fun checkInheritance(file: PsiFile, problemDescriptors: MutableList<ProblemDescriptor>, manager: InspectionManager, isOnTheFly: Boolean) {
		val script = file.inheritanceScript
			?: return

		val path = script.string.text.trim('\"')
		if (path.endsWith(SquirrelFileType.EXTENSION, true)) {
			problemDescriptors.add(manager.error(script, "Script path should not have .${SquirrelFileType.EXTENSION} extension", isOnTheFly))
		}
		if (path.startsWith("/") || path.startsWith("\\")) {
			problemDescriptors.add(manager.error(script, "Script path should not start with / or \\", isOnTheFly))
		}
		if (!file.checkIfFileExists(script.string.text.trim('"') + ".nut")) {
			problemDescriptors.add(manager.warn(script, "Script doesn't exist in current project", isOnTheFly))
		}
	}
}
