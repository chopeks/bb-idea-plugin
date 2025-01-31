package com.chopeks.inspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.util.InspectionMessage
import com.intellij.psi.PsiElement

fun InspectionManager.error(psiElement: PsiElement, @InspectionMessage descriptionTemplate: String, isOnTheFly: Boolean) =
	createProblemDescriptor(psiElement, descriptionTemplate, true, ProblemHighlightType.GENERIC_ERROR, isOnTheFly)

fun InspectionManager.warn(psiElement: PsiElement, @InspectionMessage descriptionTemplate: String, isOnTheFly: Boolean) =
	createProblemDescriptor(psiElement, descriptionTemplate, true, ProblemHighlightType.WARNING, isOnTheFly)