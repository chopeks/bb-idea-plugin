package com.chopeks.highlighting

import com.intellij.codeHighlighting.TextEditorHighlightingPassFactoryRegistrar
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.openapi.project.Project

class SquirrelHighlightingPassFactoryRegistrar : TextEditorHighlightingPassFactoryRegistrar {
	override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project) {
		registrar.registerTextEditorHighlightingPass(SquirrelHighlightingPassFunctionCalls.Factory(), null, null, false, -1)
		registrar.registerTextEditorHighlightingPass(SquirrelHighlightingPassStringReferences.Factory(), null, null, false, -1)
	}
}