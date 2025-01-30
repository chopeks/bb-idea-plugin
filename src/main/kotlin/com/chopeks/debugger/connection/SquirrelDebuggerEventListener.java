package com.chopeks.debugger.connection;

import com.chopeks.debugger.sqdbg.SquirrelProcessSnapshot;

public interface SquirrelDebuggerEventListener {
  void debuggerStarted();
//  void failedToInterpretModules(String nodeName, List<String> modules);
//  void failedToDebugRemoteNode(String nodeName, String error);
  void unknownMessage(String messageText);
  void breakpointIsSet(String module, int line);
  void breakpointIsRemoved(String module, int line);
  void breakpointReached(SquirrelProcessSnapshot snapshot);
  void debuggerStopped();
}
