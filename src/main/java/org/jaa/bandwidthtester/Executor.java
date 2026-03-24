/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jaa.bandwidthtester;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;


/**
 *
 * @author jerry
 */
public class Executor {

    Launcher m_launcherOut;
  Launcher m_launcherErr;
  Thread m_outThread;
  Thread m_errThread;
  Process m_proc;
  
  public void execCommand(String[] cmdLine, ArrayBlockingQueue<String> outputLines, ArrayBlockingQueue<String> errorLines, Args myArgs, boolean showCommand) {
      try {
          Runtime runtime = Runtime.getRuntime();
          m_proc = runtime.exec(cmdLine);
          m_launcherOut = new Launcher(m_proc.getInputStream(), outputLines);
          m_outThread = new Thread(m_launcherOut);
          
          m_launcherErr = new Launcher(m_proc.getErrorStream(), errorLines);
          m_errThread = new Thread(m_launcherErr);
          if (showCommand) {
              String output =
                      Arrays.stream(cmdLine)
                              .map(s ->
                                           (!s.isBlank() ? "'" : "")
                                                   + AnsiCodes.ANSI_COLOR.PURPLE.getCode(myArgs.getTermType())
                                                   + s
                                                   + AnsiCodes.getReset(myArgs.getTermType())
                                                   + ((!s.isBlank())  ? "'" : ""))
                              .collect(Collectors.joining(" "));
              System.out.printf("\n%siperf3 command-line%s:\n ==> %s\n",
                                AnsiCodes.ANSI_COLOR.GREEN.getCode(myArgs.getTermType()),
                                AnsiCodes.getReset(myArgs.getTermType()),
                                output);

          }
          if (myArgs.isAndroid()) {
              String androidOut =
                      AnsiCodes.getReset(myArgs.getTermType())
                              + "    "
                              +  AnsiCodes.ANSI_COLOR.YELLOW.getReverseBoldCode(myArgs.getTermType())
                              + "Running on android"
                              + AnsiCodes.getReset(myArgs.getTermType())
                              + ":    \n    {\n        '"
                              + AnsiCodes.ANSI_COLOR.RED.getBoldCode(myArgs.getTermType())
                              + "/Users/jerry/Library/Android/sdk/platform-tools/adb"
                              + AnsiCodes.getReset(myArgs.getTermType())
                              + "',\n"
                              + "        '"
                              + AnsiCodes.ANSI_COLOR.RED.getBoldCode(myArgs.getTermType())
                              + "shell"
                              + AnsiCodes.getReset(myArgs.getTermType())
                              + "',\n"
                              + "        '"
                              + AnsiCodes.ANSI_COLOR.RED.getBoldCode(myArgs.getTermType())
                              + "/data/local/tmp/iperf3.20"
                              + AnsiCodes.getReset(myArgs.getTermType())
                              + "'\n    }\n";
              System.out.print(androidOut);

          }

          m_outThread.start();
          m_errThread.start();
          if (myArgs.debug) {
              System.out.printf("My Thread = '%s'\n", Thread.currentThread().getName());
              System.out.printf("Out Thread='%s'\n", m_outThread.getName());
              System.out.printf("Error Thread='%s'\n", m_errThread.getName());
          }
          
      } catch (IOException ex) {
          System.out.println("Exception occurred while executing the command " +
                                     Arrays.toString(cmdLine) + " :\n" +
                  ex.getMessage());
      }
  }    
  
    
  public int getCommandReturnCode(Args myArgs) throws Exception {
      int exitCode = -999;
      try {
          m_proc.waitFor();
          m_outThread.join();
          m_errThread.join();
          exitCode = m_proc.exitValue();
          Exception threadExcep;
          if((threadExcep = m_launcherOut.getException()) != null) { throw threadExcep; }
          if((threadExcep = m_launcherErr.getException()) != null) { throw threadExcep; }
      } catch (InterruptedException ex) {
          System.out.printf("Exception in getreturncode: %s\n", ex);
          ex.printStackTrace(System.out);
      }
      if (myArgs.verbose) System.out.printf("Return Code = %d\n", exitCode);
      return exitCode;
  }
}
