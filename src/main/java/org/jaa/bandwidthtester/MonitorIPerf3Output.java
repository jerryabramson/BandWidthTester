/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jaa.bandwidthtester;

/**
 *
 * @author jerry
 */
public class MonitorIPerf3Output {
    private static final String WORD_DELIMITER_RE = "[ \t]++";
    protected static int columnMarker = 30;

    static void processLine(String line, ConnectionDetails conn, Args args) {
        String local;
        String lport;
        String connected;
        String to;
        String rport;
        String ID;
        if (args.debug) {
            System.out.printf("LINE: '%s'\n", line);
        }
        int firstLeftBracket = line.indexOf("[");
        int firstRightBracket = line.indexOf("]");
        if (firstLeftBracket >= 0 && firstRightBracket > firstLeftBracket) {
            ID = line.substring(firstLeftBracket + 1, firstRightBracket);
            String[] restOfLine = line.substring(firstRightBracket + 1).split(WORD_DELIMITER_RE);
            if (ID.contains("ID")) {
                if (!conn.isGathered()) {
                    if (args.verbose) {
                        System.out.printf("ID Separator\n", line);
                    }
                    System.out.printf("%s  Local Host/IP: %s%s%s remote Host/IP: %s%s%s Remote Port: %s%d%s\n", 
                            AnsiCodes.getCR(args.getTermType()),
                            AnsiCodes.ANSI_COLOR.RED.getBoldCode(args.getTermType()), conn.getLocalHost(), AnsiCodes.getReset(args.getTermType()), 
                            AnsiCodes.ANSI_COLOR.GREEN.getCode(args.getTermType()), conn.getRemoteHost(), AnsiCodes.getReset(args.getTermType()), 
                            AnsiCodes.ANSI_COLOR.GREEN.getCode(args.getTermType()), conn.getRemotePort(), AnsiCodes.getReset(args.getTermType()));
                    conn.setGathered(true);
                    conn.setResultEntry(0);
                    System.out.printf("  ");
                    printLine(args, 78);
                }
            }
            if (!conn.isGathered()) {
                if (restOfLine.length == 10) {
                    conn.setLocalHost(restOfLine[2]);
                    conn.setLocalPort(restOfLine[4]);
                    conn.setRemoteHost(restOfLine[7]);
                    conn.setRemotePort(restOfLine[9]);
                    local = restOfLine[1];
                    lport = restOfLine[3];
                    connected = restOfLine[5];
                    to = restOfLine[6];
                    rport = restOfLine[8];
                    if (args.debug) {
                        System.out.printf("[first=%s] id='%s', local='%s', localHost='%s', lport='%s', localPort='%d', connected='%s', to='%s', remoteHost='%s', rport='%s', remotePort='%d'\n", conn.isGathered(), ID, local, conn.getLocalHost(), lport, conn.getLocalPort(), connected, to, conn.getRemoteHost(), rport, conn.getRemotePort());
                    }
                } else {
                    // Other iperf3 information
                    System.out.println();
                    System.out.print(line);
                }
            } else {
                // connIsGathered
                if (restOfLine.length >= 7) {
                    String interval = restOfLine[1];
                    String intervalUnit = restOfLine[2];
                    String transfer = restOfLine[3];
                    String transferUnit = restOfLine[4];
                    String bitRate = restOfLine[5];
                    String bitRateUnit = restOfLine[6];
                    String sendOrReceive = "";
                    if (restOfLine.length > 7)  sendOrReceive = restOfLine[7];
                    if (restOfLine.length > 8)  sendOrReceive = restOfLine[8];
                    if (restOfLine.length > 9)  sendOrReceive = restOfLine[9];
                    if (restOfLine.length > 10) sendOrReceive = restOfLine[10];
                    
                    sendOrReceive = sendOrReceive.trim();
                    if (args.verbose) {
                        System.out.printf("id='%s', interval='%s', intervalUnit='%s', transfer='%s', transferUnit='%s', bitRate='%s', bitRateUnit='%s', sendOrReceive='%s'\n", 
                            ID, interval, intervalUnit, transfer, transferUnit, bitRate, bitRateUnit, sendOrReceive);
                    }
                    String color1 = AnsiCodes.ANSI_COLOR.BLUE.getBoldCode(args.getTermType());                    
                    String color2 = AnsiCodes.ANSI_COLOR.BLUE.getCode(args.getTermType());                    
                    String time = "   Running  ";
                    String columnSet = AnsiCodes.gotoColumn(args.getTermType(), 4);
                    if (ID.contains("SUM") || conn.isSingleThread()) {
                        switch (sendOrReceive.toLowerCase()) {
                            case "(omitted)":
                                time = "          ";
                                break;
                            case "sender":
                            case "receiver":
                                columnSet = AnsiCodes.gotoColumn(args.getTermType(), 3);
                                if (!conn.isFInished()) {
                                    conn.setFinished();
                                    conn.setSummaryResults(true);
                                    System.out.println();
                                    System.out.printf("  ");
                                    printLine(args, 78);
                                }
                                if (args.verbose) System.out.printf("sender/receiver interval = '%s'\n", interval);
                                color1 = AnsiCodes.ANSI_COLOR.GREY.getReverseHighlightCode(args.getTermType());                                    
                                color2 = AnsiCodes.ANSI_COLOR.BLUE.getReverseHighlightCode(args.getTermType());       
                                time = "Results:   ";
                                break;
                            default:
                                sendOrReceive = "";
                                conn.incrementResultEntry();
                        }
                        StringBuilder fmtString = new StringBuilder();
                        fmtString.append("%s");                   // goto column 4
                        fmtString.append("%s%s%-12.12s%s");       // time, color1, interval, reset
                        fmtString.append("%s%s");                  // goto columnMarker + " "
                        fmtString.append(" %s");                  // gotoColumn cm + times + 2
                        fmtString.append(" %s%6.6s%s");           // reverseHL, bitRate, reset
                        fmtString.append("  %s%s%s");             // underline, bitRateUnit, reset
                        fmtString.append(" %s%s%s");              // color2, sendOrReceive, reset
                        fmtString.append("%s");                   // clear to EOL
                        System.out.printf(fmtString.toString(), 
                            columnSet,
                            time, color1, interval, AnsiCodes.getReset(args.getTermType()),  
                            AnsiCodes.gotoColumn(args.getTermType(), MonitorIPerf3Output.columnMarker - 2), " ",
                            AnsiCodes.gotoColumn(args.getTermType(), columnMarker + args.times + 2),
                            AnsiCodes.ANSI_COLOR.GREEN.getReverseHighlightCode(args.getTermType()), bitRate, AnsiCodes.getReset(args.getTermType()), 
                            AnsiCodes.getUnderline(args.getTermType()), bitRateUnit, AnsiCodes.getReset(args.getTermType()), 
                            color2, sendOrReceive, AnsiCodes.getReset(args.getTermType()), 
                            AnsiCodes.getClearToEOL(args.getTermType()));
                        if (!sendOrReceive.isEmpty() || !args.getTermType().isAnsiTerm()) {
                            System.out.println();
                        } else {
                            printProgress(conn, args);
                        }

 
                    }
                } else {
                    if (args.debug) {
                        System.out.printf("Incorrect number of words: = %d:", restOfLine.length);
                        System.out.printf("'%s'\n", line);
                    }
                }
            }
        } else {
            if (!Launcher.EOF.equals(line) && 
                !line.startsWith("- -") && 
                !line.toLowerCase().contains("iperf done") && 
                !line.trim().isEmpty()) {
                System.out.printf("%s\n", line);
            }
        }
    }
    
    protected static void printLine(Args args, int col) {
        for (int j = 0; j < col; j++) {
            System.out.printf("%s", AnsiCodes.getHorizontalLine(args.getTermType()));
        }
        System.out.println();
    }
    
    private static void printProgress(ConnectionDetails conn, Args args) {
        System.out.printf("%s[%s]",
            AnsiCodes.gotoColumn(args.getTermType(), columnMarker),
            AnsiCodes.gotoColumn(args.getTermType(), columnMarker + conn.getTimePeriod() + 1));
//        int i = 0;
//        while (i < conn.getResultEntry()) {
            System.out.printf("%s%s%s%s%s%s%s",
                            AnsiCodes.gotoColumn(args.getTermType(), columnMarker + conn.getResultEntry()),
                            AnsiCodes.ANSI_COLOR.GREEN.getReverseHighlightCode(args.getTermType()),  " ", 
                            AnsiCodes.getReset(args.getTermType()),
                            AnsiCodes.ANSI_COLOR.GREEN.getCode(args.getTermType()), args.getTermType().FANCY_RIGHT_ARROW, AnsiCodes.getReset(args.getTermType()),
                            AnsiCodes.getReset(args.getTermType()));
            if (conn.getResultEntry() >= args.times) {
                System.out.printf("%s%s]", AnsiCodes.getBackSpace(args.getTermType()), AnsiCodes.getReset(args.getTermType()));
//            } else {
//                System.out.printf("%s", AnsiCodes.gotoColumn(args.getTermType(), columnMarker + conn.getResultEntry() + 2));
            }
//            i++;
//        }
//        while (i < conn.getTimePeriod()) {
//            System.out.printf("%s", " ");
//            i++;
//        }        

//        System.out.printf("%s", AnsiCodes.gotoColumn(args.getTermType(), columnMarker - 1));                
    }
    
}
