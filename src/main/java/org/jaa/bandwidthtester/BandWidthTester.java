package org.jaa.bandwidthtester;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
            "/opt/local/bin/iperf3",
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
     * @param repeat TBD
     * @return command-line arguments for iperf3
     */
    private static String[] prepareIPerfExe(Args myArgs, boolean noBuffer) {
        List<String> args = new ArrayList<>();
        String[] ret = {
                findIPerf3(),
                (!noBuffer ? "--forceflush" : ""),
                (!noBuffer ? "--connect-timeout" : ""),
                (!noBuffer ? "3000" : ""),
                "-c",
                myArgs.client,
                myArgs.omit,
                myArgs.parallel,
                "-t",
                Integer.toString(myArgs.times),
                (myArgs.reverse ? "-R" : ""),
        };
        args.addAll(Arrays.asList(ret));
        args.addAll(Arrays.asList(myArgs.getRemainingArgs()));
        return args.toArray(new String[0]);
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
        boolean noBuffer = false;
        while (rc == -999) {
            String[] iperf3cmdLine = prepareIPerfExe(myArgs, noBuffer);
            StringBuilder timeWithUnit = new StringBuilder();
            if (myArgs.repeat == 0) {
                rc = IPerf3Monitor.run(iperf3cmdLine, myArgs, timeWithUnit);
                if (rc == -999) {
                    noBuffer = true;
                    iperf3cmdLine = prepareIPerfExe(myArgs, noBuffer);
                    timeWithUnit.setLength(0);
                    rc = IPerf3Monitor.run(iperf3cmdLine, myArgs, timeWithUnit);
                }
            } else {
                List<Integer> results = new ArrayList<>();
                List<Date> runDates = new ArrayList<>();
                List<String> averageResults = new ArrayList<>();
                int numLoops = myArgs.repeat;
                int loop = 0;
                while (loop < numLoops) {
                    Date currentDate = new Date();
                    System.out.printf("\n[%s%s%s]%s%15.15s%s%d%40.40s%s\n\n",
                                      AnsiCodes.ANSI_COLOR.GREEN.getReverseBoldCode(myArgs.getTermType()),
                                      currentDate,
                                      AnsiCodes.getReset(myArgs.getTermType()),
                                      AnsiCodes.ANSI_COLOR.PURPLE.getReverseBoldCode(myArgs.getTermType()),
                                      "                                        ",
                                      "Execution #",
                                      loop+1,
                                      "                                                            ",
                                      AnsiCodes.getReset(myArgs.getTermType()));
                    rc = IPerf3Monitor.run(iperf3cmdLine, myArgs, timeWithUnit);
                    if (rc == -999) {
                        noBuffer = true;
                        continue;
                    }
                    loop++;
                    results.add(rc);
                    runDates.add(currentDate);
                    averageResults.add(timeWithUnit.toString());
                    System.out.print("  ");
                    MonitorIPerf3Output.printLine(myArgs, 78);
                    if (loop < numLoops - 1) {
                        System.out.print("pause between runs: 2 seconds ... ");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ignored) { /**/ }
                        System.out.println("Done.");
                        MonitorIPerf3Output.printLine(myArgs, 78);
                    }
                }
                System.out.printf("\n\n%sResults%s: [\n",
                                  AnsiCodes.ANSI_COLOR.PURPLE.getReverseBoldCode(myArgs.getTermType()),
                                  AnsiCodes.getReset(myArgs.getTermType()));
                double sum = 0;
                for (int i = 0; i < results.size(); i++) {
                    int r = results.get(i);
                    String color = AnsiCodes.ANSI_COLOR.RED.getCode(myArgs.getTermType());
                    if (r == 0) color = AnsiCodes.ANSI_COLOR.GREEN.getCode(myArgs.getTermType());
                    String returnString = (r == 0 ? "OK" : ("Error=" + r));
                    LocalDateTime localDateTime = runDates.get(i).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                    String isoDate = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                    System.out.printf("\t    {%s%19.19s%s} => %s%s%s [%s%s%s]",
                                      AnsiCodes.ANSI_COLOR.BLUE.getCode(myArgs.getTermType()),
                                      isoDate,
                                      AnsiCodes.getReset(myArgs.getTermType()),
                                      color,
                                      returnString,
                                      AnsiCodes.getReset(myArgs.getTermType()),
                                      AnsiCodes.getBold(myArgs.getTermType()),
                                      averageResults.get(i),
                                      AnsiCodes.getReset(myArgs.getTermType()));

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
