package com.chopeks.psi.impl

import com.chopeks.psi.SquirrelClass
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SquirrelClassImpl(node: ASTNode) : ASTWrapperPsiElement(node), SquirrelClass