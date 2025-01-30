package com.chopeks.psi.impl;

import com.chopeks.psi.SquirrelClass;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public abstract class SquirrelClassImpl extends ASTWrapperPsiElement implements SquirrelClass {
    public SquirrelClassImpl(@NotNull ASTNode node) {
        super(node);
    }
}