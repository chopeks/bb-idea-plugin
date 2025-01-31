package com.chopeks

import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.*

object SquirrelBundle {
	private const val BUNDLE: @NonNls String = "com.chopeks.SquirrelBundle"
	private var ourBundle: ResourceBundle? = null

	@JvmStatic
	fun message(key: @PropertyKey(resourceBundle = BUNDLE) String, vararg params: Any?): String {
		return try {
			String.format(bundle!!.getString(key), *params)
		} catch (e: MissingResourceException) {
			key
		}
	}

	private val bundle: ResourceBundle?
		get() {
			if (ourBundle == null) {
				ourBundle = try {
					ResourceBundle.getBundle(BUNDLE, Locale.getDefault())
				} catch (e: MissingResourceException) {
					ResourceBundle.getBundle(BUNDLE, Locale.ENGLISH)
				}
			}
			return ourBundle
		}
}
