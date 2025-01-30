package com.chopeks

import com.intellij.lang.Language

object SquirrelLanguage : Language("Squirrel") {
  fun readResolve(): Any = SquirrelLanguage
}