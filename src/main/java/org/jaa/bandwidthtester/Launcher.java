/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jaa.bandwidthtester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;



/**
 *
 * @author jerry
 */
public class Launcher
    implements Runnable
{
  private final BufferedReader m_in;
  private final ArrayBlockingQueue<String> m_lines;
  private Exception m_e;
  public static final String EOF = "EOF";
  
  public Launcher(InputStream in, ArrayBlockingQueue<String> lines) {
      m_in = new BufferedReader(new InputStreamReader(in));
      m_lines = lines;
  }
  
  @Override
  public void run()
  {
      String line;
      try {
          do {
              line = m_in.readLine();
              if (line != null) {
                  m_lines.offer(line);
                  //System.out.printf("\tDEBUG[%d] '%s'\n", m_lines.size(), line);                                          
                  //System.out.flush();                  
              }
          } while (line != null);
          m_lines.add(EOF);          
          //System.out.printf("EOF '%s'\n", line);              
          //System.out.printf("\tDEBUG[%d] '%s'\n", m_lines.size(), line);                                                    
      } catch(IOException e) {
          System.out.printf("Error: %s\n", e);
          e.printStackTrace(System.out);
          m_e = e;
      }
  }
  
  public String getLine(int lineNumber) {
      String line = null;
      if (m_lines.size() > lineNumber) {
          line = m_lines.poll();
      }
      return line;
  }

  
  public Exception getException()  { return m_e; }
  


}
