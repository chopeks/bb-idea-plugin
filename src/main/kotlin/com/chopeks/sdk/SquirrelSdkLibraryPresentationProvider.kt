package com.chopeks.sdk

import com.chopeks.SquirrelIcons
import com.chopeks.sdk.SquirrelSdkService.Companion.isSquirrelSdkLibRoot
import com.chopeks.util.SquirrelConstants
import com.intellij.openapi.roots.libraries.DummyLibraryProperties
import com.intellij.openapi.roots.libraries.LibraryKind
import com.intellij.openapi.roots.libraries.LibraryPresentationProvider
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

class SquirrelSdkLibraryPresentationProvider : LibraryPresentationProvider<DummyLibraryProperties?>(KIND) {
	override fun getIcon(): Icon? {
		return SquirrelIcons.NUT_FILE
	}

	override fun detect(classesRoots: List<VirtualFile>): DummyLibraryProperties? {
		for (root in classesRoots) {
			if (isSquirrelSdkLibRoot(root)) {
				return DummyLibraryProperties.INSTANCE
			}
		}
		return null
	}

	companion object {
		private val KIND: LibraryKind = LibraryKind.create(SquirrelConstants.SQUIRREL)
	}
}
