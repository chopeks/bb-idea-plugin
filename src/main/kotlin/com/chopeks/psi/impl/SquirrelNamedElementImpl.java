package com.chopeks.psi.impl;

import com.chopeks.psi.SquirrelNamedElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public abstract class SquirrelNamedElementImpl extends ASTWrapperPsiElement implements SquirrelNamedElement {
    public SquirrelNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}