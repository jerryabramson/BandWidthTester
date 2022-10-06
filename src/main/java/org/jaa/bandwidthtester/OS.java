/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jaa.bandwidthtester;


import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinNT.HANDLE;
/**
 *
 * @author jerry
 */
public class OS
{
    protected enum OSTypes
    {
        LINUX,
        MAC,
        WINDOWS,
        UNKNOWN
    }
    OSTypes myOS;
    
    
    public OS()
    {
        String os = System.getProperty("os.name");
      //  System.out.printf("os = '%s'\n", os);
        switch (os.toLowerCase().substring(0, 3))
        {
            case "mac":
                myOS = OSTypes.MAC;
                break;
            case "lin":
                myOS = OSTypes.LINUX;
                break;
            case "win":
                myOS = OSTypes.WINDOWS;
                break;
            default:
                myOS = OSTypes.UNKNOWN;
                break;
        }
    }
    
    public OSTypes getOS() { return myOS; }
    
    
    /**
     * On Windows, in order to get ANSI escape sequence colors, this is needed
     * @param myArgs - Arguments from command-line
     */
    public void setWindowsConsoleMode(Args myArgs) {
        if (myArgs.debug) System.out.printf("setting Windows Console Mode: myOS.getOS() = '%s' ? ", getOS());
        if (myOS == OSTypes.WINDOWS) {
            myArgs.getTermType().FANCY_RIGHT_ARROW = ">"; // Windows doesn't handle unicode nerd fonts!
            if (myArgs.debug) System.out.printf("YES\n");
            // Set output mode to handle virtual terminal sequences
            try {
                Function GetStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle");
                DWORD STD_OUTPUT_HANDLE = new DWORD(-11);
                HANDLE hOut = (HANDLE) GetStdHandleFunc.invoke(HANDLE.class, new Object[] { STD_OUTPUT_HANDLE });
                
                DWORDByReference p_dwMode = new DWORDByReference(new DWORD(0));
                Function GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode");
                GetConsoleModeFunc.invoke(BOOL.class, new Object[] { hOut, p_dwMode });
                
                int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
                DWORD dwMode = p_dwMode.getValue();
                dwMode.setValue(dwMode.intValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
                Function SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode");
                SetConsoleModeFunc.invoke(BOOL.class, new Object[] { hOut, dwMode });
            } catch (NoClassDefFoundError ne) {
                System.out.printf("Running on Windows without console mode, characters can be garbled.\n");
            }
        } else {
            if (myArgs.debug) System.out.printf("NO\n");
        }
    }

}
