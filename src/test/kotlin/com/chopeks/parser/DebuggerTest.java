package com.chopeks.parser;

import com.intellij.testFramework.PlatformLiteFixture;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class DebuggerTest extends PlatformLiteFixture {
    public void testDebug() {
        try {
            String hostName = "127.0.0.1";
            int portNumber = 1234;

            final Socket echoSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);

            out.println("ab:1:test.nut");
            out.flush();

            out.close();
            echoSocket.close();

            Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
                try {
                    echoSocket.close();
                    System.out.println("The server is shut down!");
                } catch (IOException e) { /* failed */ }
            }});

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
