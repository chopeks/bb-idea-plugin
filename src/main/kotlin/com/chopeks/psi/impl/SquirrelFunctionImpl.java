package com.chopeks.psi.impl;

import com.chopeks.psi.SquirrelFunction;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public abstract class SquirrelFunctionImpl extends ASTWrapperPsiElement implements SquirrelFunction {
    public SquirrelFunctionImpl(@NotNull ASTNode node) {
        super(node);
    }
}