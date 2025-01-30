package com.chopeks.debugger.connection.events;

import com.chopeks.debugger.connection.SquirrelDebuggerConnection;
import com.chopeks.debugger.connection.SquirrelDebuggerEventListener;

class RemoveBreakpointResponseEvent extends SetBreakpointResponseEvent {
    public static final String NAME = "removebreakpoint";

    public RemoveBreakpointResponseEvent(String message) throws DebuggerEventFormatException {
        super(message);
    }

    @Override
    public void process(SquirrelDebuggerConnection debuggerNode, SquirrelDebuggerEventListener eventListener) {
        eventListener.breakpointIsRemoved(file, line);
    }
}
