package com.chopeks.inspection

import com.chopeks.psi.*
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementVisitor

@Suppress("InspectionDescriptionNotFoundInspection")
class BBClassInspection : LocalInspectionTool() {
	override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor> {
		val problemDescriptors = mutableListOf<ProblemDescriptor>()
		checkDuplicatedFunctions(file, problemDescriptors, manager, isOnTheFly)
		checkClassName(file, problemDescriptors, manager, isOnTheFly)
		return problemDescriptors.toTypedArray()
	}

	private fun checkDuplicatedFunctions(file: PsiFile, problemDescriptors: MutableList<ProblemDescriptor>, manager: InspectionManager, isOnTheFly: Boolean) {
		val seenFunctions = mutableSetOf<String>()
		file.accept(object : PsiRecursiveElementVisitor() {
			override fun visitElement(element: PsiElement) {
				if (element is SquirrelFunctionDeclaration) {
					val functionName = element.functionName?.text ?: ""
					if (!seenFunctions.add(functionName)) {
						problemDescriptors.add(manager.error(element.functionName!!, "Duplicated function '$functionName' definition.", isOnTheFly))
					}
				}
				super.visitElement(element)
			}
		})
	}

	private fun checkClassName(file: PsiFile, problemDescriptors: MutableList<ProblemDescriptor>, manager: InspectionManager, isOnTheFly: Boolean) {
		if (file.children.isEmpty())
			return
		val expression = file.children.firstOrNull()
		if (expression !is SquirrelExpressionStatement)
			return
		val assign = expression.children.firstOrNull()
		if (assign !is SquirrelAssignExpression)
			return
		if (assign.children.size != 3)
			return // not what we are looking for
		if (assign.children[0] !is SquirrelReferenceExpression)
			return // reference should be first
		if (assign.children[1] !is SquirrelAssignmentOperator)
			return // assignment should be second
		val className = assign.children[0].children.lastOrNull() ?: return
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
}
