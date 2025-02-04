package com.chopeks.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

fun initializeComposeMainDispatcherChecker() {
	object : LifecycleOwner {
		override val lifecycle = LifecycleRegistry(this)

		init {
			lifecycle.currentState = Lifecycle.State.STARTED
		}
	}
}