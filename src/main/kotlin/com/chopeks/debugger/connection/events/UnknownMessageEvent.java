package com.chopeks.debugger.connection.events;

import com.chopeks.debugger.connection.SquirrelDebuggerConnection;
import com.chopeks.debugger.connection.SquirrelDebuggerEventListener;

class UnknownMessageEvent extends SquirrelDebuggerEvent {
  private final String myUnknownMessageText;

  public UnknownMessageEvent(String message) {
    myUnknownMessageText = message;
  }

  @Override
  public void process(SquirrelDebuggerConnection debuggerNode, SquirrelDebuggerEventListener eventListener) {
    eventListener.unknownMessage(myUnknownMessageText);
  }
}
