package com.chopeks.psi;

import com.chopeks.SquirrelLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class SquirrelTokenType extends IElementType {
    public SquirrelTokenType(@NotNull @NonNls String debugName) {
        super(debugName, SquirrelLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}