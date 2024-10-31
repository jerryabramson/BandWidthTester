package org.jaa.bandwidthtester;

import java.io.File;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author jerry
 */
public class BandWidthTester {
    private static String[] iperf3Executables = {
        "/usr/jaalocal/bin/iperf3",
        "/usr/local/bin/iperf3",
        "/usr/bin/iperf3",
        "/bin/iperf3",
	"C:/Program Files/iperf3.17.1_64/iperf3.exe",
	"C:/Program Files/iperf3-3.17.1_64/iperf3.exe",
        "C:/Program Files/iperf3.12_64/iperf3.exe",
        "C:/Program Files/iperf-3.1.3-win64/iperf3.exe"};
    
    public static OS myOS;
    
    /**
     * 
     * @return  find the preferred iperf3 executable
     */
    private static String findIPerf3() {
        String ip = null;
        for (String exe : iperf3Executables) {
            File f = new File(exe);
            if (f.exists() && f.canExecute()) {
                ip = exe;
                break;
            }
        }
        if (ip == null) {
            System.out.printf("Cannot find iperf3!");
            System.exit(1);
        }
        return ip;
    }
    
    /**
     * 
     * @param myArgs
     * @param tries
     * @return 
     */
    private static String prepareIPerfExe(Args myArgs, int tries) 
    {
        String forceFlush = "--forceflush";
        String connectTimeout = "--connect-timeout 3000";
        if (tries > 0) { forceFlush = ""; connectTimeout = ""; }
        return String.format("%s %s %s -c %s %s %s -t %d %s %s", 
                findIPerf3(),
                forceFlush,
                connectTimeout,
                myArgs.client, 
                myArgs.omit, 
                myArgs.parallel, 
                myArgs.times,
                (myArgs.reverse ? "-R" : ""),
                myArgs.remainingArgs.toString());
        }
        
    public static void main(String[] args) {
        myOS = new OS();
        TermType  termType = new TermType(myOS);
                
        Args myArgs = processArgs(args);
        if (myArgs == null) { usage(); return; }
        if (myArgs.client.isEmpty()) {
            System.out.printf("Must supply iperf3 server name: ");
            Scanner sc = new Scanner(System.in);
            myArgs.client = sc.nextLine();
            if (myArgs.client.isEmpty()) {
                usage();
                return;
            }
        }
        
        myArgs.setOS(myOS);        
        myArgs.setTermType(termType);        
        myOS.setWindowsConsoleMode(myArgs);        
        int rc = -999;            
        int tries = 0;
        while (rc == -999) {        
            String iperf3 = prepareIPerfExe(myArgs, tries);
            if (myArgs.repeat == 0) {
            rc = IPerf3Monitor.run(iperf3, myArgs);
            } else {
                List<Integer> results = new ArrayList<>();
                for (int loop = 0; loop < myArgs.repeat; loop++) {
                    System.out.printf("\n%s%sExecution #%d%s%s\n\n",
                                      AnsiCodes.ANSI_COLOR.PURPLE.getHighlightCode(myArgs.getTermType()),
                                      "                                        ",
                                      loop+1,
                                      "                                                            ",
                                      AnsiCodes.getReset(myArgs.getTermType()));
                    rc = IPerf3Monitor.run(iperf3, myArgs);
                    results.add(rc);
                    System.out.printf("  ");
                    MonitorIPerf3Output.printLine(myArgs, 78);

                }
                System.out.printf("\n\n%sResults%s: [",
                                  AnsiCodes.ANSI_COLOR.PURPLE.getHighlightCode(myArgs.getTermType()),
                                  AnsiCodes.getReset(myArgs.getTermType()));
                for (int i = 0; i < results.size(); i++) {
                    int r = results.get(i);
                    String color = AnsiCodes.ANSI_COLOR.RED.getCode(myArgs.getTermType());
                    if (r == 0) color = AnsiCodes.ANSI_COLOR.GREEN.getCode(myArgs.getTermType());
                    String returnString = (r == 0 ? "OK" : ("Error=" + r));
                    System.out.printf("%s%s%s",
                                      color,
                                      returnString,
                                      AnsiCodes.getReset(myArgs.getTermType()));
                    if (i < results.size() -1) System.out.printf(", ");
                }
                System.out.printf("]\n");
            }
            tries++;
        }
        System.exit(rc);
    }
    
    private static Args processArgs(String[] a) {
        
        Args args = new Args();
        String utf = System.getenv("LANG");
        if (utf != null && utf.toLowerCase().contains("utf")) args.UTF = true;
        args.client = "";
        args.omit = "-O 2";
        args.parallel = "-P 8";
        args.times = 10;
        int argc = 0;
        while (argc < a.length) {
            String arg = a[argc];
            String argVal = null;
            if (argc < a.length - 1) {
                argVal = a[argc+1];
            }
            if (!arg.startsWith("-") && argc == 0) {
                args.client = arg;
            } else {
                String argSwitch = arg;
                if (arg.length() > 2) {
                    argSwitch = arg.substring(0,2);
                }
                switch (argSwitch) {
                    case "-R":
                        args.reverse = true;
                        break;
                    case "-O":
                        if (arg.length() > 2) {
                            args.omit = arg;
                        } else {
                            if (argVal != null) {
                                args.omit= "-O " + argVal;
                                argc++;
                            } else {
                                usage();
                            }
                        }
                        break;
                    case "-c":
                        if (arg.length() > 2) {
                            args.client = arg.substring(2);
                        } else {
                            if (argVal != null) {
                                args.client = argVal;
                                argc++;
                            }
                        }
                        break;
                    case "-P":
                        if (arg.length() > 2) {
                            args.parallel = arg;
                            if (arg.substring(2).equals("1")) {
                                args.single = true;
                            }
                        } else {
                            if (argVal != null) {
                                if (argVal.equals("1")) {
                                    args.single = true;
                                }
                                args.parallel = "-P " + argVal;
                                argc++;                        
                            } else {
                                usage();
                            }
                        }
                        break;
                    case "-t":
                        if (arg.length() > 2) {
                            try {
                                args.times = Integer.valueOf(arg.substring(2));
                            } catch (NumberFormatException ne) {
                                System.out.printf("Invalid numeric format for '-t': %s\n", arg);
                                usage();
                            }
                        } else {
                            try {
                                args.times= Integer.valueOf(argVal);
                            } catch (NumberFormatException e) {
                                System.out.printf("Invalid numeric format for '-t': %s\n", argVal);
                                usage();
                            }
                            argc++;

                        }
                        break;
                    case "-l":
                        if (arg.length() > 2) {
                            try {
                                args.repeat = Integer.valueOf(arg.substring(2));
                            } catch (NumberFormatException ne) {
                                System.out.printf("Invalid numeric format for '-l': %s\n", arg);
                                usage();
                            }
                        } else {
                            try {
                                args.repeat = Integer.valueOf(argVal);
                            } catch (NumberFormatException e) {
                                System.out.printf("Invalid numeric format for '-l': %s\n", argVal);
                                usage();
                            }
                        }
                        break;
                    case "-v":
                        args.verbose=true;
                        break;
                    case "-d":
                        args.debug=true;
                        args.verbose=true;
                        break;
                    default:
                        args.remainingArgs.append(arg).append(" ");
                        break;
                }
            }
            argc++;
        }
        return args;
    }

    private static void usage() {
        System.out.printf("Bad command-line arguments!\n");
        System.exit(1);
    }
}
