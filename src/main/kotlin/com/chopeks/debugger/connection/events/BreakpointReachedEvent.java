package com.chopeks.debugger.connection.events;

import com.chopeks.debugger.connection.SquirrelDebuggerConnection;
import com.chopeks.debugger.connection.SquirrelDebuggerEventListener;
import com.chopeks.debugger.sqdbg.SquirrelCall;
import com.chopeks.debugger.sqdbg.SquirrelProcessSnapshot;
import com.chopeks.debugger.sqdbg.SquirrelVariable;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class BreakpointReachedEvent extends SquirrelDebuggerEvent {
    public static final String NAME = "break";

    protected SquirrelProcessSnapshot snapshot;

    public BreakpointReachedEvent(String message) throws DebuggerEventFormatException {
        try {
            org.jdom.Document doc = new SAXBuilder().build(new StringReader(message));
            String file = doc.getRootElement().getAttributeValue("file");
            int line = Integer.parseInt(doc.getRootElement().getAttributeValue("line"));
            String type = doc.getRootElement().getAttributeValue("type");
            String error = doc.getRootElement().getAttributeValue("error");

            Collection<SquirrelVariable> objects = new ArrayList<SquirrelVariable>();
            List<SquirrelCall> stack = new ArrayList<SquirrelCall>();

            snapshot = new SquirrelProcessSnapshot(file, line, type, error, objects, stack);
        } catch (IOException | JDOMException e) {
            throw new DebuggerEventFormatException();
        }
    }

    @Override
    public void process(SquirrelDebuggerConnection debuggerNode, SquirrelDebuggerEventListener eventListener) {
        eventListener.breakpointReached(snapshot);
    }
}
