/*
 * Copyright 2012-2014 Sergey Ignatov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chopeks.debugger.xdebug;

import com.chopeks.debugger.sqdbg.SquirrelCall;
import com.chopeks.debugger.sqdbg.SquirrelProcessSnapshot;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SquirrelExecutionStack extends XExecutionStack {
  private final SquirrelCall myCall;
  private final SquirrelProcessSnapshot mySnapshot;
  private final List<SquirrelStackFrame> myStack;

  public SquirrelExecutionStack(SquirrelCall call, SquirrelProcessSnapshot snapshot) {
    super(call.getFunction());
    myCall = call;
    mySnapshot = snapshot;
    myStack = new ArrayList<SquirrelStackFrame>(mySnapshot.getStack().size());
  }

  @Nullable
  @Override
  public XStackFrame getTopFrame() {
    return ContainerUtil.getFirstItem(myStack);
  }

  @Override
  public void computeStackFrames(int firstFrameIndex, XStackFrameContainer container) {
    if (myStack.isEmpty()) {
      List<SquirrelCall> traceElements = mySnapshot.getStack();
      for (SquirrelCall traceElement : traceElements) {
        boolean isTopStackFrame = myStack.isEmpty(); // if it's a top stack frame we can set a line that's being executed.
        SquirrelStackFrame stackFrame = new SquirrelStackFrame(traceElement);
        myStack.add(stackFrame);
      }
      container.addStackFrames(myStack, true);
    }
  }
}