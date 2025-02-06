package com.chopeks.inspection

import ai.grazie.utils.isLowercase
import com.chopeks.psi.SquirrelTableExpression
import com.chopeks.psi.SquirrelTableItem
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementVisitor

class TableInspection : LocalInspectionTool() {
	override fun getStaticDescription() = "Table Inspection"

	override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor> {
		val problemDescriptors = mutableListOf<ProblemDescriptor>()
		file.accept(object : PsiRecursiveElementVisitor() {
			override fun visitElement(element: PsiElement) {
				if (element is SquirrelTableExpression) {
					checkForDuplicates(element, manager, isOnTheFly, problemDescriptors)
				}
				super.visitElement(element)
			}
		})
		return problemDescriptors.toTypedArray()
	}

	private fun checkForDuplicates(
		table: SquirrelTableExpression,
		manager: InspectionManager,
		isOnTheFly: Boolean,
		problemDescriptors: MutableList<ProblemDescriptor>
	) {
		val parent = table.parent
		val isMtable = parent is SquirrelTableItem && parent.key?.text == "m"

		val seenVariables = mutableSetOf<String>()
		table.tableItemList.forEach { tableItem ->
			val variableName = tableItem.key?.text
			if (variableName != null) {
				if (!seenVariables.add(variableName)) {
					problemDescriptors.add(manager.error(tableItem, "Duplicated variable '$variableName' found in table", isOnTheFly))
				}
				if (variableName.isLowercase() && isMtable) {
					problemDescriptors.add(manager.warn(tableItem.children.first(), "By convention table key '$variableName' should be uppercase.", isOnTheFly))
				}
			}
		}
	}
}
