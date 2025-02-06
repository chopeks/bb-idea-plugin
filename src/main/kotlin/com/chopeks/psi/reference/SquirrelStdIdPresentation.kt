package com.chopeks.psi.reference

import com.chopeks.psi.SquirrelFunctionName
import com.chopeks.psi.SquirrelStdIdentifier
import com.chopeks.psi.SquirrelTableItem
import com.intellij.icons.AllIcons
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.vfs.toNioPathOrNull
import javax.swing.Icon

class SquirrelStdIdPresentation(
	private val element: SquirrelStdIdentifier
) : ItemPresentation {
	override fun getPresentableText(): String? {
		return element.text
	}

	override fun getLocationString(): String? {
		return element.containingFile?.virtualFile?.toNioPathOrNull()?.toString()
	}

	// this thing seems not working at all, idk why, not called by intellij even, though it's override
	override fun getIcon(unused: Boolean): Icon {
		if (element.parent is SquirrelTableItem)
			return AllIcons.Nodes.Field
		if (element.parent.parent is SquirrelFunctionName)
			return AllIcons.Nodes.Method
		return AllIcons.Nodes.Method
	}

}
