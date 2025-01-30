package com.chopeks.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface SquirrelClass extends PsiElement {

    @Nullable
    SquirrelClassBody getClassBody();

}