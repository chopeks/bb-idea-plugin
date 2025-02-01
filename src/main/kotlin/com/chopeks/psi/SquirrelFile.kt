package com.chopeks.psi

import com.chopeks.SquirrelFileType
import com.chopeks.SquirrelLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider

class SquirrelFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, SquirrelLanguage) {
	override fun getFileType() = SquirrelFileType.INSTANCE
	override fun toString() = "Squirrel File"
}