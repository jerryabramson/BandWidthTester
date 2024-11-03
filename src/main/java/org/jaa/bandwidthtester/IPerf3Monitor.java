/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jaa.bandwidthtester;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jerry
 */
class IPerf3Monitor {
    protected static String[] tick = {"-", "/", "+", "\\"};
    protected static String startProgress = "|";
    protected static String progress = "-";        
    protected static String progressRight = ">";
    protected static String doneProcessing = "*";

    protected static int run(String iperf3, Args args, StringBuilder averageResult) {
        averageResult.setLength(0);
        ArrayBlockingQueue<String> outputLines = new ArrayBlockingQueue<>(1000);
        ArrayBlockingQueue<String> errorLines = new ArrayBlockingQueue<>(1000);

        String line;
        int rc = 1;
        Executor e = new Executor();
        ConnectionDetails conn = new ConnectionDetails();

        if (args.UTF) {
            startProgress = args.getTermType().LEFT_COLUMN_LINE;
            progress = args.getTermType().ANSI_LINE;
            progressRight = args.getTermType().ANSI_LINE;
            doneProcessing = args.getTermType().RIGHT_COLUMN_LINE;
        }
        conn.setIsSingleThread(args.single);
        conn.setTimePeriod(args.times);
        conn.setVerbose(args.verbose);
        conn.setDebug(args.debug);
        MonitorIPerf3Output.rightColumnMarker = MonitorIPerf3Output.leftColumnMarker + conn.getTimePeriod() + 1;
        
    
        try {
            e.execCommand(iperf3, outputLines, errorLines, args);
            boolean waitForResult = true;
            int iter = 0;
            System.out.printf("%s", startProgress);
            while (true) {
                line = outputLines.poll(250, TimeUnit.MILLISECONDS);
                if (line != null) {
                    if (waitForResult) {
                        System.out.printf("%s%s%s\n", 
                            AnsiCodes.getBackSpace(args.getTermType()),
                            AnsiCodes.getClearToEOL(args.getTermType()),
                            doneProcessing);
                        waitForResult = false;
//                    } else {
//                          System.out.printf("%s",
//                                    AnsiCodes.gotoColumn(args.getTermType(), MonitorIPerf3Output.leftColumnMarker + conn.getResultEntry() + 1));
                    }
                    if (Launcher.EOF.equals(line)) {
                        break;
                    }                    
                    MonitorIPerf3Output.processLine(line, conn, args);
                } else {
                    if (!conn.isGathered()) {
                        if (args.getTermType().isAnsiTerm()) {
                            System.out.printf("%s%s%s", 
                                    progress, progressRight, AnsiCodes.getBackSpace(args.getTermType()));
                        } else {
                            System.out.print(".");
                        }
                    }
                    if (conn.isGathered() && !conn.isSummaryResults()) {
                        int restingColumn = MonitorIPerf3Output.leftColumnMarker + conn.getResultEntry() + 1;
                        if (args.reverse) {
                            restingColumn = MonitorIPerf3Output.rightColumnMarker - conn.getResultEntry() - 1;
                        }
                        if (args.getTermType().isAnsiTerm()) {
                            System.out.printf("%s%s%s%s", 
                                    AnsiCodes.getReset(args.getTermType()),
                                    AnsiCodes.gotoColumn(args.getTermType(), MonitorIPerf3Output.leftColumnMarker - 2),
                                    tick[iter % tick.length],
                                    AnsiCodes.gotoColumn(args.getTermType(), restingColumn));
                        } else {
                            System.out.print(".");
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
                            System.out.printf("%sErrors%s:\n",
                                AnsiCodes.ANSI_COLOR.RED.getHighlightCode(args.getTermType()), AnsiCodes.getReset(args.getTermType()));
                        }
                        System.out.printf("\t%s%s%s\n", 
                            AnsiCodes.ANSI_COLOR.RED.getCode(args.getTermType()), line, AnsiCodes.getReset(args.getTermType()));                        
                    }
                    errorCounter++;                                                
                }
            }
            System.out.printf("%s", AnsiCodes.gotoColumn(args.getTermType(), 0));
            if (rc != -999) {
                rc = e.getCommandReturnCode(args);
                if (rc == 0) {
                    System.out.printf("  Return Code: %s%03d%s [%s]\n",
                                      AnsiCodes.ANSI_COLOR.GREEN.getCode(args.getTermType()),
                                      rc,
                                      AnsiCodes.getReset(args.getTermType()),
                                      conn.getLastResult());
                } else {
                    System.out.print("       ");
                    MonitorIPerf3Output.printLine(args, 80);
                    System.out.printf("       Return Code: %s%03d%s\n", AnsiCodes.ANSI_COLOR.RED.getCode(args.getTermType()), rc, AnsiCodes.getReset(args.getTermType()));
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
