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

  Exception m_e;
  Launcher m_launcherOut;
  Launcher m_launcherErr;
  Thread m_outThread;
  Thread m_errThread;
  Process m_proc;
  
  public void execCommand(String[] cmdLine, ArrayBlockingQueue<String> outputLines, ArrayBlockingQueue<String> errorLines, Args myArgs)
            throws Exception  {
      try {
          Runtime runtime = Runtime.getRuntime();
          m_proc = runtime.exec(cmdLine);
          m_launcherOut = new Launcher(m_proc.getInputStream(), outputLines);
          m_outThread = new Thread(m_launcherOut);
          
          m_launcherErr = new Launcher(m_proc.getErrorStream(), errorLines);
          m_errThread = new Thread(m_launcherErr);
          String output =
                  Arrays.stream(cmdLine)
                          .map(s ->
                                       "'"
                                               + AnsiCodes.ANSI_COLOR.BLUE.getCode(myArgs.getTermType())
                                               + s
                                               + AnsiCodes.getReset(myArgs.getTermType())
                                               + "'")
                          .collect(Collectors.joining(" "));
          System.out.printf("%sExecuting command%s => %s: ",
                            AnsiCodes.ANSI_COLOR.GREEN.getCode(myArgs.getTermType()),
                            AnsiCodes.getReset(myArgs.getTermType()),
                            output);

          m_outThread.start();
          m_errThread.start();
          if (myArgs.debug) {
              System.out.printf("My Thread = '%s'\n", Thread.currentThread().getName());
              System.out.printf("Out Thread='%s'\n", m_outThread.getName());
              System.out.printf("Error Thread='%s'\n", m_errThread.getName());
          }
          
      } catch (IOException ex) {
          System.out.println("Exception occured while executing the command " +
                  cmdLine + " :\n" +
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
