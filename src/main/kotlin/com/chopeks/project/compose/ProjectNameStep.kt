package com.chopeks.project.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.jewel.ui.component.Text

@Composable
fun HelloCompose() {
	Text("Hello Compose!", color = Color.Red)
}