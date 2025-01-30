package com.chopeks.psi.impl

import com.chopeks.psi.SquirrelNamedElement
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class SquirrelNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), SquirrelNamedElement