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
    protected static int leftColumnMarker = 30;
    protected static int  rightColumnMarker;

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
                        System.out.printf("ID Separator: %s\n", line);
                    }
                    System.out.printf("%s  Local Host/IP: %s%s%s remote Host/IP: %s%s%s Remote Port: %s%d%s\n", 
                            AnsiCodes.getCR(args.getTermType()),
                            AnsiCodes.ANSI_COLOR.RED.getBoldCode(args.getTermType()), conn.getLocalHost(), AnsiCodes.getReset(args.getTermType()), 
                            AnsiCodes.ANSI_COLOR.GREEN.getCode(args.getTermType()), conn.getRemoteHost(), AnsiCodes.getReset(args.getTermType()), 
                            AnsiCodes.ANSI_COLOR.GREEN.getCode(args.getTermType()), conn.getRemotePort(), AnsiCodes.getReset(args.getTermType()));
                    conn.setGathered(true);
                    conn.setResultEntry(0);
                    // System.out.print("  ");
                    // printLine(args, 78);
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
                    if (!line.trim().isEmpty()) {
                        System.out.println();
                        System.out.print(line);
                    }
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
                    if (!sendOrReceive.toLowerCase().contains("sender") && !sendOrReceive.toLowerCase().contains("receiv") && !sendOrReceive.toLowerCase().contains("omit")) {
                        sendOrReceive = "";
                    }
                    if (args.verbose) {
                        System.out.printf("id='%s', interval='%s', intervalUnit='%s', transfer='%s', transferUnit='%s', bitRate='%s', bitRateUnit='%s', sendOrReceive='%s'\n", 
                            ID, interval, intervalUnit, transfer, transferUnit, bitRate, bitRateUnit, sendOrReceive);
                    }
                   
                    String color1 = AnsiCodes.ANSI_COLOR.BLUE.getBoldCode(args.getTermType());                    
                    String color2 = AnsiCodes.ANSI_COLOR.BLUE.getCode(args.getTermType());                    
                    String time = "   Running  ";
                    boolean done = false;
                    String columnSet = AnsiCodes.gotoColumn(args.getTermType(), 4);
                    if (ID.contains("SUM") || conn.isSingleThread()) {
                        double bitRateValue = -1;
                        try { bitRateValue = Double.valueOf(bitRate); } catch (NumberFormatException ignored) { }
                        if (sendOrReceive.isEmpty()) {                    
                            conn.setMaxBytesPerSec(bitRateValue);
                            conn.setMinBytesPerSec(bitRateValue);
                        }
                        switch (sendOrReceive.toLowerCase()) {
                            case "(omitted)":
                                if (conn.isLastOmitted() || conn.getResultEntry() == 0) {
                                    System.out.printf("%s%s", AnsiCodes.getCR(args.getTermType()), AnsiCodes.getClearToEOL(args.getTermType()));
                                }
                                time = "   Skipping ";
                                if (args.getTermType().isAnsiTerm()) {
                                    System.out.printf("%s%s%s", 
                                        AnsiCodes.getReset(args.getTermType()),
                                        AnsiCodes.gotoColumn(args.getTermType(), MonitorIPerf3Output.leftColumnMarker - 2),
                                        " ");
                                }
                                conn.setLastOmitted(true);
                                break;
                            case "sender":
                            case "receiver":
                                conn.setLastOmitted(false);
                                columnSet = AnsiCodes.gotoColumn(args.getTermType(), 3);
                                if (!conn.isFInished()) {
                                    if (conn.isLastOmitted() || conn.getResultEntry() == 0) {
                                        System.out.printf("%s%s", AnsiCodes.getCR(args.getTermType()), AnsiCodes.getClearToEOL(args.getTermType()));
                                    }
                                    conn.setFinished();
                                    time = "Results:   ";                                    
                                    conn.setSummaryResults(true);
                                } else {
                                    time = "        => ";                                    
                                    System.out.println();
                                    done = true;
                                }
                                if (args.verbose) System.out.printf("sender/receiver interval = '%s'\n", interval);
                                color1 = AnsiCodes.ANSI_COLOR.GREEN.getReverseHighlightCode(args.getTermType());
                                color2 = AnsiCodes.ANSI_COLOR.BLUE.getReverseHighlightCode(args.getTermType());
                                break;
                            default:
                                conn.incrementResultEntry();                                
                                if (conn.isLastOmitted() || conn.getResultEntry() == 0) {
                                    System.out.printf("%s%s", AnsiCodes.getCR(args.getTermType()), AnsiCodes.getClearToEOL(args.getTermType()));
                                }
                                conn.setLastOmitted(false);
                                sendOrReceive = "";
                        }

                        StringBuilder fmtString = new StringBuilder();
                        String bitRateColor = AnsiCodes.ANSI_COLOR.GREEN.getReverseBoldCode(args.getTermType());
                        if (bitRateValue == 0) {
                            bitRateColor = AnsiCodes.ANSI_COLOR.RED.getReverseBoldCode(args.getTermType());
                        }
                        fmtString.append("%s");                   // goto column 4
                        fmtString.append("%s%s%-12.12s%s");       // time, color1, interval, reset
                        fmtString.append(" %s");                  // gotoColumn cm + times + 2
                        fmtString.append(" %s%,9.2f%s");          // reverseHL, bitRate, reset
                        fmtString.append("  %s%s%s");             // underline, bitRateUnit, reset
                        fmtString.append(" %s%s%s");              // color2, sendOrReceive, reset
                        fmtString.append("%s");                   // clear to EOL
                        conn.setLastResult(bitRateValue + " " + bitRateUnit);
                        System.out.printf(fmtString.toString(), 
                            columnSet,
                            time, color1, interval, AnsiCodes.getReset(args.getTermType()),  
                            AnsiCodes.gotoColumn(args.getTermType(), leftColumnMarker + args.times + 2),
                            bitRateColor, bitRateValue, AnsiCodes.getReset(args.getTermType()),
                            AnsiCodes.getUnderline(args.getTermType()), bitRateUnit, AnsiCodes.getReset(args.getTermType()), 
                            color2, sendOrReceive, AnsiCodes.getReset(args.getTermType()), 
                            AnsiCodes.getClearToEOL(args.getTermType()));
                        if (!args.getTermType().isAnsiTerm()) {
                            System.out.println();
                        }
                        else if (!done && !conn.isFInished()) {
                            printProgress(conn, args);
                        }
                        if (done) {
                            columnSet = AnsiCodes.gotoColumn(args.getTermType(), leftColumnMarker);
                           // System.out.printf("%s%s", AnsiCodes.getCR(args.getTermType()), AnsiCodes.getClearToEOL(args.getTermType()));                            
                           System.out.println();
                            // System.out.printf("  ");
                            // printLine(args, 78);
                            if (conn.getMinBytesPerSec() > 0) {
                                System.out.printf("%s[%s%s%s]%s%s%s%s %s %s%s\n",
                                                  columnSet,
                                                  AnsiCodes.ANSI_COLOR.RED.getReverseBoldCode(args.getTermType()),
                                                  "Min",
                                                  AnsiCodes.getReset(args.getTermType()),
                                                  AnsiCodes.gotoColumn(args.getTermType(), leftColumnMarker + args.times + 3),
                                                  AnsiCodes.ANSI_COLOR.RED.getReverseBoldCode(args.getTermType()),
                                                  normalizeValue(conn.getMinBytesPerSec()),
                                                  AnsiCodes.getReset(args.getTermType()),
                                                  AnsiCodes.getUnderline(args.getTermType()), bitRateUnit, AnsiCodes.getReset(args.getTermType()));
                            }
                            if (conn.getMaxBytesPerSec() > 0) {
                                System.out.printf("%s[%s%s%s]%s%s%s%s %s %s%s\n",
                                                  columnSet,
                                                  AnsiCodes.ANSI_COLOR.BLUE.getReverseBoldCode(args.getTermType()),
                                                  "Max",
                                                  AnsiCodes.getReset(args.getTermType()),
                                                  AnsiCodes.gotoColumn(args.getTermType(), leftColumnMarker + args.times + 3),
                                                  AnsiCodes.ANSI_COLOR.BLUE.getReverseBoldCode(args.getTermType()),
                                                  normalizeValue(conn.getMaxBytesPerSec()),
                                                  AnsiCodes.getReset(args.getTermType()),
                                                  AnsiCodes.getUnderline(args.getTermType()), bitRateUnit, AnsiCodes.getReset(args.getTermType()));
                            }
                            System.out.print("  ");
                            printLine(args, 78);
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
        String leftBracket = "[";
        String rightBracket = "]";

          int column1 = leftColumnMarker + conn.getResultEntry();
          int column2 = column1 - 1;
          String arrow = args.getTermType().FANCY_RIGHT_ARROW;
          if (args.reverse) {
              leftBracket = "(";
              column1 = rightColumnMarker - conn.getResultEntry();
              arrow = args.getTermType().FANCY_LEFT_ARROW;
              column2 = column1 + 1;
          } else {
              rightBracket = ")";
          }
          if (conn.isLastOmitted() || conn.getResultEntry() == 0) {
              column1 = leftColumnMarker;
              column2 = leftColumnMarker;
          }
          StringBuilder fmt = new StringBuilder();
          fmt.append("%s"); // goto column1
          fmt.append("%s%s%s"); // green, arrow, reset
          fmt.append("%s%s%s"); // goto column2, reverse, space
          fmt.append("%s"); // reset
          System.out.printf(fmt.toString(),
                            AnsiCodes.gotoColumn(args.getTermType(), column1),
                            AnsiCodes.ANSI_COLOR.YELLOW.getCode(args.getTermType()), arrow, AnsiCodes.getReset(args.getTermType()),
                            AnsiCodes.gotoColumn(args.getTermType(), column2),                            
                            AnsiCodes.ANSI_COLOR.YELLOW.getReverseHighlightCode(args.getTermType()), " ",
                            AnsiCodes.getReset(args.getTermType()));

        System.out.printf("%s%s%s%s",
                          AnsiCodes.gotoColumn(args.getTermType(), leftColumnMarker),
                          leftBracket,
                          AnsiCodes.gotoColumn(args.getTermType(), rightColumnMarker),
                          rightBracket);
          if (conn.getResultEntry() >= args.times) {
              if (!args.reverse) {
                  System.out.printf("%s%s%s%s%s%s%s%s",
                                    AnsiCodes.getReset(args.getTermType()),
                                    AnsiCodes.gotoColumn(args.getTermType(), rightColumnMarker - 1),
                                    AnsiCodes.ANSI_COLOR.YELLOW.getReverseHighlightCode(args.getTermType()),  " ",
                                    AnsiCodes.getReset(args.getTermType()),
                                    AnsiCodes.ANSI_COLOR.YELLOW.getCode(args.getTermType()),
                                    arrow,
                                    AnsiCodes.getReset(args.getTermType()));
              } else {
                  System.out.printf("%s%s%s%s%s%s%s",
                                    AnsiCodes.getReset(args.getTermType()),
                                    AnsiCodes.gotoColumn(args.getTermType(), leftColumnMarker),
                                    AnsiCodes.ANSI_COLOR.YELLOW.getCode(args.getTermType()),
                                    arrow,
                                    AnsiCodes.ANSI_COLOR.YELLOW.getReverseHighlightCode(args.getTermType()),
                                    " ",
                                    AnsiCodes.getReset(args.getTermType()));
              }
          } else {
              if (args.getTermType().isAnsiTerm()) {
                int restingColumn = MonitorIPerf3Output.leftColumnMarker + conn.getResultEntry() + 1;
                if (args.reverse) {
                  restingColumn = MonitorIPerf3Output.rightColumnMarker - conn.getResultEntry() - 1;
                }
                System.out.printf("%s%s", 
                                 AnsiCodes.getReset(args.getTermType()),
                                 AnsiCodes.gotoColumn(args.getTermType(), restingColumn));
                }
          }
     }
    
       private static String normalizeValue(double val) {
           return String.format("%,9.2f", val);
    }
    
}
