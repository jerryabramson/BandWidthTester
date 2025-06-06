package org.jaa.bandwidthtester;


import com.sun.jna.Function;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

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
     * On Windows, in order to get ANSI escape sequence colors, and some other
     * required terminal output, we resort to calling into
     * Following documented APIs from:
     *    Console Developer's guide & API Reference - 05/18/2021
     *       <li></li><a href="https://learn.microsoft.com/en-us/windows/console/setconsolemode">...</a></li>
     *       <li></li><a href="https://learn.microsoft.com/en-us/windows/console/setconsoleoutputcp">...</a></li>
     *
     * @param myArgs - Arguments from command-line
     */
    public void setWindowsConsoleMode(Args myArgs) {
        if (myArgs.verbose) System.out.printf("setting Windows Console Mode: myOS.getOS() = '%s' ? ", getOS());
        if (myOS == OSTypes.WINDOWS) {
            if (myArgs.verbose) System.out.println("YES");
            try {
                // Set output mode to handle virtual terminal sequences
                try {
                    if (myArgs.verbose) System.out.println("On Windows we are forcing Java to use UTF-8");
                    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8.toString()));
                } catch (Exception e) {
                    System.out.println("Cannot properly set  Windows Code Page to UTF-8, output may be garbled!");
                }
                Function GetStdHandleFunc  = Function.getFunction("kernel32", "GetStdHandle");
                DWORD    STD_OUTPUT_HANDLE = new DWORD(-11);
                HANDLE   hOut              = (HANDLE) GetStdHandleFunc.invoke(HANDLE.class, new Object[]{STD_OUTPUT_HANDLE});

                DWORDByReference p_dwMode           = new DWORDByReference(new DWORD(0));
                Function         GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode");
                GetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, p_dwMode});

                int   ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
                DWORD dwMode                             = p_dwMode.getValue();
                dwMode.setValue(dwMode.intValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
                Function SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode");
                SetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, dwMode});

                if (myArgs.verbose) System.out.println("And now we force Windows to also use the Code Page for UTF-8");
                UINTByReference p_wCodePageID = new UINTByReference(new UINT(0));
                UINT wCodePageID           = p_wCodePageID.getValue();
                wCodePageID.setValue(65001);
                Function SetConsoleOutputCP  = Function.getFunction("kernel32", "SetConsoleOutputCP");
                SetConsoleOutputCP.invoke(BOOL.class, new Object[]{wCodePageID});

            } catch (NoClassDefFoundError ne) {

                System.out.println("Running on Windows without console mode, characters can be garbled.");

            }
        } else {
            if (myArgs.verbose) System.out.println("NO");
        }
    }

}
