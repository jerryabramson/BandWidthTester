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

/**
 *
 * @author jerry
 */
public class BandwidthTester {
    private static final String[] iperf3Executables = {
            "/usr/jaalocal/bin/iperf3",
            "/usr/local/bin/iperf3",
            "/usr/bin/iperf3",
            "/bin/iperf3",
            "/opt/local/bin/iperf3",
            "C:/Program Files/iperf3.17.1_64/iperf3.exe",
            "C:/Program Files/iperf3-3.17.1_64/iperf3.exe",
            "C:/Program Files/iperf3.12_64/iperf3.exe",
            "C:/Program Files/iperf-3.1.3-win64/iperf3.exe"};

    private static final List<ResultDetails> averageResults = new ArrayList<>();
    private static final List<String> androidIperfBinaries = new ArrayList<>();

    private static TerminalType termType = null;
    private static Args myArgs = null;
    private static boolean cleanExit = false;
    private static String androidHome = "";



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
            usage("Cannot find iperf3!");
        }
        return ip;
    }

    /**
     * @return find the preferred iperf3 executable on Android device
     */
    private static String findAndroidIPerf3(Args myArgs) {
        for (String iperfPath : androidIperfBinaries) {
            try {
                ProcessBuilder pb = new ProcessBuilder(myArgs.getAndroidADBPath(), "shell", "test", "-x", iperfPath, "&&", "echo", "exists");
                Process p = pb.start();
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
                String result = reader.readLine();
                p.waitFor();
                if ("exists".equals(result)) {
                    myArgs.setAndroidIperfPath( iperfPath);
                    return iperfPath;
                }
            } catch (Exception e) {
                // Continue to next binary
            }
        }
        usage("Cannot find iperf3 on Android device!");
        return null;
    }


    /**
     * @param myArgs Command-line arguments
     * @param noBuffer whether to use buffering (iperf3 will not buffer output)
     * @return command-line arguments for iperf3
     */
    private static String[] prepareIPerfExe(Args myArgs, boolean noBuffer) {
        List<String> args = new ArrayList<>();

        if (!myArgs.isAndroid()) {
            args.add(findIPerf3());
        } else {
            String adb = myArgs.getAndroidHome() + "/platform-tools/adb";
            if (new File(adb).exists() && new File(adb).canExecute()) {
                myArgs.setAndroidADBPath(adb);
                androidIperfBinaries.add("/data/local/tmp/iperf3.20");
                androidIperfBinaries.add("/data/local/tmp/iperf3");
                androidIperfBinaries.add("/system/bin/iperf3");
                androidIperfBinaries.add("/system/xbin/iperf3");
                args.add(adb);
                args.add("shell");
                args.add(findAndroidIPerf3(myArgs));
            } else {
                usage("Cannot find adb executable!");
            }
        }
        if (!noBuffer) {
            args.add("--forceflush");
            args.add("--connect-timeout");
            args.add("3000");
        }
        args.addAll(Arrays.asList("-c", myArgs.client, myArgs.omit, myArgs.parallel, "-t", Integer.toString(myArgs.times), (myArgs.reverse ? "-R" : "")));
        args.addAll(Arrays.asList(myArgs.getRemainingArgs()));

        // cannot reach
        return args.toArray(new String[0]);
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!cleanExit) {
                System.out.print("\033[0m\033[0G\033[0m");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { /**/ }
                System.out.print("\033[0m\033[0G\033[0m");
                System.out.flush();
                System.out.println("\n\033[31mShutdown hook called! Exiting\033[0m!");
                System.out.flush();
            }
        }));

        termType = new TerminalType();
        myArgs = processArgs(args);
        if (myArgs.client.isEmpty()) {
            System.out.print("Must supply iperf3 server name: ");
            Scanner sc = new Scanner(System.in);
            myArgs.client = sc.nextLine();
            if (myArgs.client.isEmpty()) {
                usage();
                return;
            }
        }

        myArgs.setTermType(termType);
        OS.setWindowsConsoleMode(myArgs);
        int rc = -999;


        String[] iperf3cmdLine = prepareIPerfExe(myArgs, false);


        if (myArgs.repeat == 0) {
            System.out.printf("[%s%s%s] ",
                              AnsiCodes.ANSI_COLOR.GREEN.getReverseBoldCode(myArgs.getTermType()),
                              new Date(),
                              AnsiCodes.getReset(myArgs.getTermType()));
            ResultDetails resultDetails = new ResultDetails();
            rc = IPerf3Monitor.run(iperf3cmdLine, myArgs, resultDetails, true);
            if (rc == 999) {
                iperf3cmdLine = prepareIPerfExe(myArgs, true);
                IPerf3Monitor.run(iperf3cmdLine, myArgs, resultDetails, true);
            }

        } else {
            int loop = 0;

            while (loop < myArgs.getRepeat()) {
                boolean showOutput = (loop == 0);
                Date currentDate = new Date();
                System.out.printf("\n[%s%s%s] %s %s%02d%s%02d%s:\n",
                                  AnsiCodes.ANSI_COLOR.GREEN.getReverseBoldCode(myArgs.getTermType()),
                                  currentDate,
                                  AnsiCodes.getReset(myArgs.getTermType()),
                                  AnsiCodes.ANSI_COLOR.PURPLE.getReverseBoldCode(myArgs.getTermType()),
                                  "Execution # ",
                                  loop + 1,
                                  " of ",
                                  myArgs.repeat,
                                  AnsiCodes.getReset(myArgs.getTermType()));
                ResultDetails resultDetails = new ResultDetails();
                rc = IPerf3Monitor.run(iperf3cmdLine, myArgs, resultDetails, showOutput);
                if (rc == -999) {
                    iperf3cmdLine = prepareIPerfExe(myArgs, true);
                    continue;
                }
                loop++;
                resultDetails.setRunDate(currentDate);
                averageResults.add(resultDetails);
                System.out.print("  ");
                MonitorIPerf3Output.printLine(myArgs, 78);
                if (loop < (myArgs.getRepeat())) {
                    System.out.print("pause between runs: 2 seconds ... ");
                    try { //noinspection BusyWait
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                        /**/
                        System.out.printf("%s%s%s%s",
                                          AnsiCodes.getReset(myArgs.getTermType()),
                                          AnsiCodes.gotoColumn(myArgs.getTermType(), 0),
                                          AnsiCodes.getClearToEOL(myArgs.getTermType()),
                                          AnsiCodes.getReset(myArgs.getTermType()));
                        System.out.flush();
                        System.out.print("Interrupted!");
                        System.exit(1);

                    }
                    System.out.printf("%s%s%s%s",
                                      AnsiCodes.getReset(myArgs.getTermType()),
                                      AnsiCodes.gotoColumn(myArgs.getTermType(), 0),
                                      AnsiCodes.getClearToEOL(myArgs.getTermType()),
                                      AnsiCodes.getReset(myArgs.getTermType()));


                }
            }
        }
        displayResults();
        cleanExit = true;
        System.exit(rc);
    }


    private static void displayResults() {
        if (termType == null || myArgs == null || averageResults.isEmpty()) return;
        Path resultsFile = Paths.get("results.txt");
        try (PrintStream out = new PrintStream(Files.newOutputStream(resultsFile))) {
            System.out.printf("\n\n%sResults%s:  [\n",
                              AnsiCodes.ANSI_COLOR.PURPLE.getReverseBoldCode(myArgs.getTermType()),
                              AnsiCodes.getReset(myArgs.getTermType()));
            out.println("\n\nResults:  [");
            for (int i = 0; i < averageResults.size(); i++) {
                ResultDetails r = averageResults.get(i);
                String color = AnsiCodes.ANSI_COLOR.RED.getCode(myArgs.getTermType());
                if (r.getRc() == 0) color = AnsiCodes.ANSI_COLOR.GREEN.getCode(myArgs.getTermType());
                String returnString = ((r.getRc() == 0) ? "OK" : ("Error=" + r.getRc()));
                LocalDateTime localDateTime = r.getRunDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                String isoDate = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                out.printf("\t    {%19.19s} => %s [%s]",
                                  isoDate,
                                  returnString,
                                  r);


                System.out.printf("\t    {%s%19.19s%s} => %s%s%s [%s%s%s]",
                                  AnsiCodes.ANSI_COLOR.BLUE.getCode(myArgs.getTermType()),
                                  isoDate,
                                  AnsiCodes.getReset(myArgs.getTermType()),
                                  color,
                                  returnString,
                                  AnsiCodes.getReset(myArgs.getTermType()),
                                  AnsiCodes.getBold(myArgs.getTermType()),
                                  r,
                                  AnsiCodes.getReset(myArgs.getTermType()));

                if (i < averageResults.size() - 1) { System.out.print(", "); }
                System.out.println();
                out.println();
            }
            System.out.print("\n\t  ]\n");
            out.print("\n\t  ]\n");
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

            if (arg.equals("-android")) {
                String androidHome = OS.getEnv("ANDROID_HOME");
                if (androidHome == null || androidHome.isEmpty()) {
                    usage("ANDROID_HOME environment variable not set!");
                }
                args.setAndroidHome(androidHome);
                args.enableAndroid();
                argc++;
                continue;
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

    private static void usage(String msg) {
        System.out.println(msg);
        System.exit(1);
    }

    private static void usage() {
        usage("Bad command-line arguments!");
    }
}
