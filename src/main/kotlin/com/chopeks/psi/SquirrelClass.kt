package com.chopeks.psi

import com.intellij.psi.PsiElement

interface SquirrelClass : PsiElement {
	val classBody: SquirrelClassBody?
}