package com.chopeks.psi.impl

import com.chopeks.psi.SquirrelFunction
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SquirrelFunctionImpl(node: ASTNode) : ASTWrapperPsiElement(node), SquirrelFunction