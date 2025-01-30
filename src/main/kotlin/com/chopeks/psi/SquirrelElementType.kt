package com.chopeks.psi

import com.chopeks.SquirrelLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class SquirrelElementType(debugName: @NonNls String) : IElementType(debugName, SquirrelLanguage)