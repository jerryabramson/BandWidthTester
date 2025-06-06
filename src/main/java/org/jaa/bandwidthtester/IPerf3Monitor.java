/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jaa.bandwidthtester;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jerry
 */
class IPerf3Monitor {
    protected static String[] tick = {"-", "/", "+", "\\"};
    protected static String stalled = " ";
    protected static String startProgress = "|";
    protected static String progress = "-";        
    protected static String progressRight = ">";
    protected static String doneProcessing = "*";

    protected static int run(String[] iperf3cmdLine, Args args, StringBuilder averageResult) {
        averageResult.setLength(0);
        ArrayBlockingQueue<String> outputLines = new ArrayBlockingQueue<>(1000);
        ArrayBlockingQueue<String> errorLines = new ArrayBlockingQueue<>(1000);
        long pollInterval = 100;

        String line;
        int rc = 1;
        Executor e = new Executor();
        ConnectionDetails conn = new ConnectionDetails();


        startProgress = "[";
        progress = " ";
        progressRight = args.getTermType().FANCY_RIGHT_ARROW;
        doneProcessing = progressRight;

        conn.setIsSingleThread(args.single);
        conn.setTimePeriod(args.times);
        conn.setVerbose(args.verbose);
        conn.setDebug(args.debug);
        MonitorIPerf3Output.rightColumnMarker = MonitorIPerf3Output.leftColumnMarker + conn.getTimePeriod() + 1;
        
    
        try {
            e.execCommand(iperf3cmdLine, outputLines, errorLines, args);
            boolean waitForResult = true;
            int iter = 0;
            System.out.printf("%s ", startProgress);
            Date start = new Date();
            boolean stalled = false;
            while (!stalled) {

                // If iperf3 starts to stall out, indicate this.
                line = outputLines.poll(pollInterval, TimeUnit.MILLISECONDS);
                if (line != null) {
                    if (waitForResult) {
                        System.out.printf("%s%s%s%s%s\n",
                                          AnsiCodes.getBackSpace(args.getTermType()),
                                          AnsiCodes.getClearToEOL(args.getTermType()),
                                          AnsiCodes.ANSI_COLOR.GREEN.getCode(args.getTermType()),
                                          doneProcessing,
                                          AnsiCodes.getReset(args.getTermType()));
                        waitForResult = false;
//                    } else {
//                          System.out.printf("%s",
//                                    AnsiCodes.gotoColumn(args.getTermType(), MonitorIPerf3Output.leftColumnMarker + conn.getResultEntry() + 1));
                    }
                    if (Launcher.EOF.equals(line)) {
                        break;
                    }                    
                    MonitorIPerf3Output.processLine(line, conn, args);
                    start = new Date();

                } else {
                    if (!conn.isGathered()) {
                        if (args.getTermType().isAnsiTerm()) {
                            System.out.printf("%s%s%s%s%s%s%s",
                                              AnsiCodes.getBackSpace(args.getTermType()),
                                              AnsiCodes.ANSI_COLOR.GREEN.getReverseHighlightCode(args.getTermType()),
                                              progress,
                                              AnsiCodes.getReset(args.getTermType()),
                                              AnsiCodes.ANSI_COLOR.GREEN.getCode(args.getTermType()),
                                              progressRight,
                                              AnsiCodes.getReset(args.getTermType()));
                        } else {
                            System.out.print(".");
                        }
                    }
                    Date end = new Date();
                    if (conn.isGathered() && !conn.isSummaryResults()) {
                        int restingColumn = MonitorIPerf3Output.leftColumnMarker + conn.getResultEntry() + 1;
                        if (args.reverse) {
                            restingColumn = MonitorIPerf3Output.rightColumnMarker - conn.getResultEntry() - 1;
                        }
                        if ((end.getTime() - start.getTime()) > TimeUnit.SECONDS.toMillis(5)) {
                            stalled = true;
                        } else if ((end.getTime() - start.getTime()) > TimeUnit.MILLISECONDS.toMillis(500)) {
                            if (args.getTermType().isAnsiTerm()) {
                                System.out.printf("%s%s%s%s%s%s",
                                                  AnsiCodes.getReset(args.getTermType()),
                                                  AnsiCodes.gotoColumn(args.getTermType(), MonitorIPerf3Output.leftColumnMarker - 2),
                                                  AnsiCodes.ANSI_COLOR.RED.getCode(args.getTermType()),
                                                  tick[iter % tick.length],
                                                  AnsiCodes.getReset(args.getTermType()),
                                                  AnsiCodes.gotoColumn(args.getTermType(), restingColumn));
                            } else {
                                System.out.print("X");
                            }
                        }
                        else {
                            if (args.getTermType().isAnsiTerm()) {
                                System.out.printf("%s%s%s%s",
                                                  AnsiCodes.getReset(args.getTermType()),
                                                  AnsiCodes.gotoColumn(args.getTermType(), MonitorIPerf3Output.leftColumnMarker - 2),
                                                  tick[iter % tick.length],
                                                  AnsiCodes.gotoColumn(args.getTermType(), restingColumn));
                            } else {
                                System.out.print(".");
                            }
                        }
                        iter++;
                    }
                }
            }

            int errorCounter = 0;
            while (true) {
                line = errorLines.poll(100, TimeUnit.MILLISECONDS);
                if (line != null) {
                    if (Launcher.EOF.equals(line)) {
                        break;
                    } else {
                        if (line.toLowerCase().contains("unknown option")) {
                            rc = -999;
                            System.out.printf("%s%s%s: [%sOutput will not be buffered%s]\n", 
                                AnsiCodes.ANSI_COLOR.PURPLE.getCode(args.getTermType()), line, AnsiCodes.getReset(args.getTermType()),
                                AnsiCodes.ANSI_COLOR.GREY.getHighlightCode(args.getTermType()), AnsiCodes.getReset(args.getTermType()));
                            break;
                        }
                        if (errorCounter == 0) {
                            System.out.printf("\n%sErrors%s:\n",
                                AnsiCodes.ANSI_COLOR.RED.getHighlightCode(args.getTermType()), AnsiCodes.getReset(args.getTermType()));
                        }
                        System.out.printf("\t%s%s%s\n", 
                            AnsiCodes.ANSI_COLOR.RED.getCode(args.getTermType()), line, AnsiCodes.getReset(args.getTermType()));                        
                    }
                    errorCounter++;                                                
                }
            }

            System.out.printf("%s", AnsiCodes.gotoColumn(args.getTermType(), 0));
            if (stalled) {
                System.out.print("       ");
                MonitorIPerf3Output.printLine(args, 80);
                System.out.printf("           STALLED: %s%03d%s\n", AnsiCodes.ANSI_COLOR.RED.getCode(args.getTermType()), 999, AnsiCodes.getReset(args.getTermType()));

            } else if (rc != -999) {
                rc = e.getCommandReturnCode(args);
                if (rc == 0) {
                    System.out.printf("  Return Code: %s%03d%s [%s%s%s]\n",
                                      AnsiCodes.ANSI_COLOR.GREEN.getCode(args.getTermType()),
                                      rc,
                                      AnsiCodes.getReset(args.getTermType()),
                                      AnsiCodes.getBold(args.getTermType()),
                                      conn.getLastResult(),
                                      AnsiCodes.getReset(args.getTermType()));
                } else {
                    System.out.print("       ");
                    MonitorIPerf3Output.printLine(args, 80);
                    System.out.printf("       Return Code: %s%03d%s\n",
                                      AnsiCodes.ANSI_COLOR.RED.getCode(args.getTermType()), rc, AnsiCodes.getReset(args.getTermType()));
                }
            }
        } catch (Exception ex) {
            System.out.printf("Main Exception: %s\n", ex);
            ex.printStackTrace(System.out);
        }
        averageResult.append(conn.getLastResult());
        return rc;
    }

 
}
