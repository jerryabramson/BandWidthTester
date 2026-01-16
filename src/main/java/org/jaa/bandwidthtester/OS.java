/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jaa.bandwidthtester;


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
    }

}
