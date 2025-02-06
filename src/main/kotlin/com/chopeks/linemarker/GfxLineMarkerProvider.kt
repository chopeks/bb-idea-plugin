package com.chopeks.linemarker

import com.chopeks.psi.SquirrelStringLiteral
import com.chopeks.psi.reference.BBResourceReference
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

		val reference = element.reference
		if (reference !is BBResourceReference)
			return null

		val ref = reference.resolve()?.containingFile
			?: return null

		if (ref.virtualFile.extension?.lowercase() != "png")
			return null

		return createGfxMarker(element, ref.virtualFile)
	}

	private fun createGfxMarker(literal: SquirrelStringLiteral, file: VirtualFile): LineMarkerInfo<*> {
		return LineMarkerInfo(
			literal.string,
			literal.textRange,
			resizedIcon(file.inputStream.readAllBytes()),
			{ literal.string.text.trim('"') },
			null,
			GutterIconRenderer.Alignment.RIGHT,
			{ "IconMarker" }
		)
	}

	private fun resizedIcon(icon: ByteArray): ImageIcon {
		return ImageIcon(ImageIcon(icon).image.getScaledInstance(24, 24, Image.SCALE_SMOOTH))
	}
}
