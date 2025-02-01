package com.chopeks.linemarker

import com.chopeks.psi.SquirrelStringLiteral
import com.chopeks.psi.checkIfFileExists
import com.chopeks.psi.getFile
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import java.awt.Image
import javax.swing.ImageIcon


class GfxLineMarkerProvider : LineMarkerProvider {
	override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
		if (element !is SquirrelStringLiteral)
			return null

		var text = element.string.text.trim('"')
		if (!text.startsWith("ui/"))
			return null

		if (text.startsWith("ui/"))
			text = "gfx/$text"

		if (element.containingFile.checkIfFileExists(text)) {
			return createGfxMarker(element, element.containingFile.getFile(text)!!)
		}
		return null
	}

	private fun createGfxMarker(literal: SquirrelStringLiteral, file: VirtualFile): LineMarkerInfo<*> {
		val icon = ImageIcon(file.inputStream.readAllBytes())
		return LineMarkerInfo(
			literal,
			literal.textRange,
			resizedIcon(icon, 24, 24),
			{ literal.string.text.trim('"') },
			null,
			GutterIconRenderer.Alignment.RIGHT,
			{ "IconMarker" }
		)
	}

	private fun resizedIcon(icon: ImageIcon, width: Int, height: Int): ImageIcon {
		val image: Image = icon.image
		return ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH))
	}
}
