package org.jaa.bandwidthtester;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    private static List<Integer> results = new ArrayList<>();
    private static List<Date> runDates = new ArrayList<>();
    private static List<String> averageResults = new ArrayList<>();
    private static TermType termType = null;
    private static Args myArgs = null;

    private static boolean cleanExit = false;


    public static OS myOS;

    public BandWidthTester() {}

    /**
     * @return find the preferred iperf3 executable
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
     * @param myArgs
     * @param noBuffer whether to use buffering (iperf3 will not buffer output)
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


    /**
     * cleans up the console and displays the results even on interrupt
     */
    private void cleanup() {
        System.out.println("\n\n\033[31mExiting.\033[0m\n");
        System.out.flush();
        System.exit(cleanExit ? 0 : 1);
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                if (!cleanExit) System.out.println("\n\033[31mShutdown hook ran\033[0m!");
            }
        });


        myOS = new OS();
        termType = new TermType(myOS);

        myArgs = processArgs(args);
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

        boolean noBuffer = false;
        while (rc == -999) {
            String[] iperf3cmdLine = prepareIPerfExe(myArgs, noBuffer);
            StringBuilder timeWithUnit = new StringBuilder();
            if (myArgs.repeat == 0) {
                System.out.printf("\n[%s%s%s]%s%15.15s%s%s: ",
                                  AnsiCodes.ANSI_COLOR.GREEN.getReverseBoldCode(myArgs.getTermType()),
                                  new Date(),
                                  AnsiCodes.getReset(myArgs.getTermType()),
                                  AnsiCodes.ANSI_COLOR.PURPLE.getReverseBoldCode(myArgs.getTermType()),
                                  "                                        ",
                                  "Executing",
                                  AnsiCodes.getReset(myArgs.getTermType()));

                rc = IPerf3Monitor.run(iperf3cmdLine, myArgs, timeWithUnit, true);
                if (rc == -999) {
                    noBuffer = true;
                    iperf3cmdLine = prepareIPerfExe(myArgs, noBuffer);
                    timeWithUnit.setLength(0);
                    rc = IPerf3Monitor.run(iperf3cmdLine, myArgs, timeWithUnit, true);
                }
            } else {
                int loop = 0;

                while (loop < myArgs.getRepeat()) {
                    boolean showOutput = (loop == 0);
                    Date currentDate = new Date();
                    System.out.printf("\n[%s%s%s]%s%15.15s%s%d%s%d%40.40s%s: ",
                                      AnsiCodes.ANSI_COLOR.GREEN.getReverseBoldCode(myArgs.getTermType()),
                                      currentDate,
                                      AnsiCodes.getReset(myArgs.getTermType()),
                                      AnsiCodes.ANSI_COLOR.PURPLE.getReverseBoldCode(myArgs.getTermType()),
                                      "                                        ",
                                      "Execution #",
                                      loop + 1,
                                      " of ",
                                      myArgs.repeat,
                                      "                                                            ",
                                      AnsiCodes.getReset(myArgs.getTermType()));
                    rc = IPerf3Monitor.run(iperf3cmdLine, myArgs, timeWithUnit, showOutput);
                    if (rc == -999) {
                        noBuffer = true;
                        iperf3cmdLine = prepareIPerfExe(myArgs, noBuffer);
                        continue;
                    }
                    loop++;
                    results.add(rc);
                    runDates.add(currentDate);
                    averageResults.add(timeWithUnit.toString());
                    System.out.print("  ");
                    MonitorIPerf3Output.printLine(myArgs, 78);
                    if (loop < (myArgs.getRepeat())) {
                        System.out.print("pause between runs: 2 seconds ... ");
                        try { Thread.sleep(2000); } catch (InterruptedException ignored) { /**/ }
                        System.out.println("Done.");
                    }
                }
                double sum = 0;
            }
            displayResults();
            cleanExit = true;
            System.exit(rc);
        }
    }


    private static void displayResults() {
        if (termType == null || myArgs == null || results.isEmpty()) return;
        Path resultsFile = Paths.get("results.txt");
        try (PrintStream out = new PrintStream(Files.newOutputStream(resultsFile))) {
            System.out.printf("\n\n%sResults%s:  [\n",
                              AnsiCodes.ANSI_COLOR.PURPLE.getReverseBoldCode(myArgs.getTermType()),
                              AnsiCodes.getReset(myArgs.getTermType()));
            out.println("\n\nResults:  [");
            for (int i = 0; i < results.size(); i++) {
                int r = results.get(i);
                String color = AnsiCodes.ANSI_COLOR.RED.getCode(myArgs.getTermType());
                if (r == 0) color = AnsiCodes.ANSI_COLOR.GREEN.getCode(myArgs.getTermType());
                String returnString = (r == 0 ? "OK" : ("Error=" + r));
                LocalDateTime localDateTime = runDates.get(i).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                String isoDate = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                out.printf("\t    {%19.19s} => %s [%s]",
                                  isoDate,
                                  returnString,
                                  averageResults.get(i));


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
                out.printf("\t    {%19.19s} => %s [%s]",
                                  isoDate,
                                  returnString,
                                  averageResults.get(i));

                if (i < results.size() - 1) { System.out.print(", "); out.print(", "); }
                System.out.println();
                out.println();
            }
            System.out.print("\n\t  ]\n");
            out.print("\n\t  ]\n");
            results.clear();
            runDates.clear();
            averageResults.clear();
            System.out.printf("Output is saved to file: %s%s%s\n",
                              AnsiCodes.ANSI_COLOR.BLUE.getCode(myArgs.getTermType()),
                              resultsFile.toAbsolutePath(),
                              AnsiCodes.getReset(myArgs.getTermType()));
        } catch (IOException e) {
            System.err.printf("Error writing to %s: %s\n",  resultsFile.toAbsolutePath(), e.getMessage());
        }
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
