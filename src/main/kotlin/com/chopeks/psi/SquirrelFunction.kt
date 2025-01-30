package com.chopeks.psi

import com.intellij.psi.PsiElement

interface SquirrelFunction : PsiElement {
	val functionBody: SquirrelFunctionBody?
}