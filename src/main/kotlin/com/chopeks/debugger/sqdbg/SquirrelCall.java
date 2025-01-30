package com.chopeks.debugger.sqdbg;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SquirrelCall {
    private final String myFile;
    private final int myLine;
    private final String myFunction;
    private final Collection<SquirrelVariable> myVariables;

    public SquirrelCall(@NotNull String file, @NotNull int line, @NotNull String function, @NotNull Collection<SquirrelVariable> variables) {
        myFile = file;
        myLine = line;
        myFunction = function;
        myVariables = variables;
    }

    @NotNull
    public String getFile() {
        return myFile;
    }

    @NotNull
    public int getLine() {
        return myLine;
    }

    @NotNull
    public String getFunction() {
        return myFunction;
    }

    @NotNull
    public Collection<SquirrelVariable> getVariables() {
        return myVariables;
    }
}