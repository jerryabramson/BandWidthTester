package org.jaa.bandwidthtester;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
        myArgs.UTF = termType.isUTF();

        if (myArgs.client.isEmpty()) {
            System.out.print("Must supply iperf3 server name: ");
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
            StringBuilder timeWithUnit = new StringBuilder();
            if (myArgs.repeat == 0) {
                rc = IPerf3Monitor.run(iperf3, myArgs, timeWithUnit);
            } else {
                List<Integer> results = new ArrayList<>();
                List<Date> runDates = new ArrayList<>();
                List<String> averageResults = new ArrayList<>();
                int numLoops = myArgs.repeat;
                for (int loop = 0; loop < numLoops; loop++) {
                    Date currentDate = new Date();
                    System.out.printf("\n[%s%s%s]%s%15.15s%s%d%40.40s%s\n\n",
                                      AnsiCodes.ANSI_COLOR.GREEN.getHighlightCode(myArgs.getTermType()),
                                      currentDate,
                                      AnsiCodes.getReset(myArgs.getTermType()),
                                      AnsiCodes.ANSI_COLOR.PURPLE.getHighlightCode(myArgs.getTermType()),
                                      "                                        ",
                                      "Execution #",
                                      loop+1,
                                      "                                                            ",
                                      AnsiCodes.getReset(myArgs.getTermType()));
                    rc = IPerf3Monitor.run(iperf3, myArgs, timeWithUnit);
                    results.add(rc);
                    runDates.add(currentDate);
                    averageResults.add(timeWithUnit.toString());
                    System.out.print("  ");
                    MonitorIPerf3Output.printLine(myArgs, 78);

                }
                System.out.printf("\n\n%sResults%s: [\n",
                                  AnsiCodes.ANSI_COLOR.PURPLE.getHighlightCode(myArgs.getTermType()),
                                  AnsiCodes.getReset(myArgs.getTermType()));
                for (int i = 0; i < results.size(); i++) {
                    int r = results.get(i);
                    String color = AnsiCodes.ANSI_COLOR.RED.getCode(myArgs.getTermType());
                    if (r == 0) color = AnsiCodes.ANSI_COLOR.GREEN.getCode(myArgs.getTermType());
                    String returnString = (r == 0 ? "OK" : ("Error=" + r));
                    LocalDateTime localDateTime = runDates.get(i).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                    String isoDate = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                    System.out.printf("\t    {%s%19.19s%s} => %s%s%s [%s]",
                                      AnsiCodes.ANSI_COLOR.BLUE.getCode(myArgs.getTermType()),
                                      isoDate,
                                      AnsiCodes.getReset(myArgs.getTermType()),
                                      color,
                                      returnString,
                                      AnsiCodes.getReset(myArgs.getTermType()),
                                      averageResults.get(i));
                    if (i < results.size() -1) System.out.print(", ");
                    System.out.println();
                }
                System.out.print("\n\t  ]\n");
            }
            tries++;
        }
        System.exit(rc);
    }
    
    private static Args processArgs(String[] a) {
        
        Args args = new Args();
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
                                args.omit = "-O " + argVal;
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
                            } else {
                                usage();
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
                                args.times = Integer.parseInt(arg.substring(2));
                            } catch (NumberFormatException ne) {
                                System.out.printf("Invalid numeric format for '-t': %s\n", arg);
                                usage();
                            }
                        } else {
                            if (argVal != null) {
                                try {
                                    args.times = Integer.parseInt(argVal);
                                    argc++;
                                } catch (NumberFormatException e) {
                                    System.out.printf("Invalid numeric format for '-t': %s\n", argVal);
                                    usage();
                                }
                            } else {
                                usage();
                            }
                        }
                        break;
                    case "-l":
                        if (arg.length() > 2) {
                            try {
                                args.repeat = Integer.parseInt(arg.substring(2));
                            } catch (NumberFormatException ne) {
                                System.out.printf("Invalid numeric format for '-l': %s\n", arg);
                                usage();
                            }
                        } else {
                            if (argVal!= null) {
                                try {
                                    args.repeat = Integer.parseInt(argVal);
                                } catch (NumberFormatException e) {
                                    System.out.printf("Invalid numeric format for '-l': %s\n", argVal);
                                    usage();
                                }
                            } else {
                                usage();
                            }
                            argc++;
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
        System.out.println("Bad command-line arguments!");
        System.exit(1);
    }
}
