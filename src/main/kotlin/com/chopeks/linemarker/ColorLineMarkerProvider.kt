package com.chopeks.linemarker

import com.chopeks.psi.SquirrelStringLiteral
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon

class ColorLineMarkerProvider : LineMarkerProvider {
	override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
		if (element !is SquirrelStringLiteral)
			return null
		val color = element.string.text.trim('"')
		if (isColorString(color)) {
			return createColorMarker(element, color)
		}
		return null
	}

	private fun isColorString(text: String) = text.matches(Regex("#?([0-9a-fA-F]{6}|[0-9a-fA-F]{8})"))

	private fun createColorMarker(literal: SquirrelStringLiteral, color: String): LineMarkerInfo<*> {
		var targetColor = color
		if (!color.startsWith("#"))
			targetColor = "#$color"

		return LineMarkerInfo(
			literal.string,
			literal.textRange,
			ColorIcon(10, parseColor(targetColor)),
			{ targetColor },
			null,
			GutterIconRenderer.Alignment.RIGHT,
			{ "ColorMarker" }
		)
	}

	fun parseColor(colorString: String) = try {
		when {
			colorString.startsWith("#") && (colorString.length == 7 || colorString.length == 9) -> {
				// RGB (6 digits) or RGBA (8 digits)
				val hex = colorString.substring(1)
				val r = Integer.parseInt(hex.substring(0, 2), 16)
				val g = Integer.parseInt(hex.substring(2, 4), 16)
				val b = Integer.parseInt(hex.substring(4, 6), 16)
				val a = if (hex.length == 8) Integer.parseInt(hex.substring(6, 8), 16) else 255 // Default alpha is 255 (opaque)
				Color(r, g, b, a)
			}

			else -> Color.BLACK
		}
	} catch (e: NumberFormatException) {
		Color.BLACK
	}

	class ColorIcon(private val size: Int, private val color: Color) : Icon {
		override fun getIconWidth(): Int = size
		override fun getIconHeight(): Int = size
		override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
			g.color = color
			g.fillRect(x, y, size, size)  // Draw the color as a square
		}
	}
}

