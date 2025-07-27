/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jaa.bandwidthtester;

/**
 *
 * @author jerry
 */
public class ConnectionDetails {

    private static final double kb = 1024.0;
    private static final double mb = kb * kb;
    private static final double gb = kb * mb;
    private static final double tb = kb * gb;


    private   String   m_localHost       = "";
    private   String   m_remoteHost      = "";
    private   int      m_localPort       = -1;
    private   int      m_remotePort      = -1;
    private   int      m_timePeriod      = 0;
    private   int      m_resultEntry     = 0;
    private   boolean  m_gathered        = false;
    private   boolean  m_summaryResults  = false;
    private   boolean  m_singleThread    = false;
    protected double   m_maxBytesPerSec  = Double.MIN_VALUE;
    protected double   m_minBytesPerSec  = Double.MAX_VALUE;
    protected double   m_maxBitsPerSec   = Double.MIN_VALUE;
    protected double   m_minBitsPerSec   = Double.MAX_VALUE;
    protected String   m_maxBitsUnit     = "";
    protected String   m_maxBytesUnit    = "";
    protected String   m_minBitsUnit     = "";
    protected String   m_minBytesUnit    = "";
    protected boolean  m_isBytesUnit     = true;
    private   boolean  m_verbose         = false;
    private   boolean  m_debug           = false;
    private   boolean  m_lastOmitted     = false;
    private   String   m_lastResult      = "";
    private final Args m_myArgs;

    public String  getLastResult()      { return m_lastResult;          }
    public boolean isLastOmitted()    { return m_lastOmitted;           }
    public boolean isDebug()          { return m_debug;                 }
    public boolean isVerbose()        { return m_verbose;               }
    public boolean isGathered()       { return m_gathered;              }
    public boolean isSingleThread()   { return m_singleThread;          }
    public boolean isFinished()       { return (m_resultEntry == -999); }
    public boolean isSummaryResults() { return m_summaryResults;        }
    public String  getLocalHost()     { return m_localHost;             }
    public int     getRemotePort()    { return m_remotePort;            }
    public int     getLocalPort()     { return m_localPort;             }
    public int     getTimePeriod()    { return m_timePeriod;            }
    public String  getRemoteHost()    { return m_remoteHost;            }
    public int     getResultEntry()   { return m_resultEntry;           }


    public ConnectionDetails() {
        throw new IllegalArgumentException("No arguments provided");
    }
    /**
     * Constructor
     * @param args Command line arguments
     */
    public ConnectionDetails(Args args) {
        m_myArgs = args;
    }

    public String getMinBitsBytesPerSec() {
        String result = null;
        if (m_isBytesUnit) {
            if (m_minBytesPerSec != Double.MAX_VALUE) {
                result = String.format("%s%s",
                                       AnsiCodes.ANSI_COLOR.RED.getReverseBoldCode(m_myArgs.getTermType()),
                                       convertToHumanReadable(m_myArgs, m_minBytesPerSec, m_isBytesUnit));
            }
        } else {
            if (m_minBitsPerSec != Double.MAX_VALUE) {
                result = String.format("%s%s",
                                       AnsiCodes.ANSI_COLOR.RED.getReverseBoldCode(m_myArgs.getTermType()),
                                       convertToHumanReadable(m_myArgs, m_minBitsPerSec, m_isBytesUnit));
            }
        }
        return result;
    }
    public String getMaxBitsBytesPerSec() {
        String result = null;
        if (m_isBytesUnit) {
            if (m_maxBytesPerSec != Double.MIN_VALUE) {
                result = String.format("%s%s",
                                       AnsiCodes.ANSI_COLOR.BLUE.getReverseBoldCode(m_myArgs.getTermType()),
                                       convertToHumanReadable(m_myArgs, m_maxBytesPerSec, m_isBytesUnit));
            }
        } else {
            if (m_maxBitsPerSec != Double.MIN_VALUE) {
                result = String.format("%s%s",
                                       AnsiCodes.ANSI_COLOR.BLUE.getReverseBoldCode(m_myArgs.getTermType()),
                                       convertToHumanReadable(m_myArgs, m_maxBitsPerSec, m_isBytesUnit));
            }
        }
        return result;
    }
    
    public void setMaxBitsBytesPerSec(double value, String unit)  {
        double[] converted = convertUnitToBitsAndBytes(value, unit);
        double maxBitsPerSec = converted[0];
        double maxBytesPerSec = converted[1];
        if (!m_isBytesUnit) {
            if (isVerbose()) System.out.printf("\tsetMaxBitsPerSec(%.2f %s): [current max bits/sec = %.0f] ",
                                               value, unit,
                                               ((m_maxBitsPerSec != Double.MIN_VALUE) ? m_maxBitsPerSec : -1));
            if (maxBitsPerSec != -1 && maxBitsPerSec > m_maxBitsPerSec) {
                if (isVerbose()) System.out.printf(" [Setting maxBitsPerSec to %.0f bits/sec (orig = %s)] ", maxBitsPerSec, unit);
                m_maxBitsPerSec = maxBitsPerSec;
                m_maxBitsUnit = unit;
            }
        } else {
            if (isVerbose()) System.out.printf("\tsetMaxBytesPerSec(%.2f %s): [current max bytes/sec = %.0f] ",
                                               value, unit,
                                               ((m_maxBytesPerSec != Double.MIN_VALUE) ? m_maxBytesPerSec : -1));

            if (maxBytesPerSec != -1 && maxBytesPerSec > m_maxBytesPerSec) {
                if (isVerbose()) System.out.printf(" [Setting maxBytesPerSec to %.0f bytes/sec (orig = %s)] ", maxBytesPerSec, unit);
                m_maxBytesPerSec = maxBytesPerSec;
                m_maxBytesUnit = unit;
            }
        }
        if (isVerbose()) System.out.println();
    }

    public void setMinBitsBytesPerSec(double value, String unit)  {

        double[] converted = convertUnitToBitsAndBytes(value, unit);
        double minBitsPerSec = converted[0];
        double minBytesPerSec = converted[1];
        if (!m_isBytesUnit) {
            if (isVerbose()) System.out.printf("\tsetMinBitsPerSec(%.2f %s): [current min bits/sec = %.0f] ",
                                               value, unit,
                                               ((m_minBitsPerSec != Double.MAX_VALUE) ? m_minBitsPerSec : -1));
            if (minBitsPerSec != -1 && minBitsPerSec < m_minBitsPerSec) {
                if (isVerbose()) System.out.printf(" [Setting minBitsPerSec to %.0f bits/sec (orig = %s)] ", minBitsPerSec, unit);
                m_minBitsPerSec = minBitsPerSec;
                m_minBitsUnit = unit;
            }
        } else {
            if (isVerbose()) System.out.printf("\tsetMinBytesPerSec(%.2f %s): [current min bytes/sec = %.0f] ",
                                               value, unit,
                                               ((m_minBytesPerSec != Double.MAX_VALUE) ? m_minBytesPerSec : -1));
            if (minBytesPerSec != -1 && minBytesPerSec < m_minBytesPerSec) {
                if (isVerbose()) System.out.printf(" [Setting minBytesPerSec to %.0f bytes/sec (orig = %s)] ", minBytesPerSec, unit);
                m_minBytesPerSec = minBytesPerSec;
                m_minBytesUnit = unit;
            }
        }
        if (isVerbose()) System.out.println();
    }

    private double[] convertUnitToBitsAndBytes(double value, String unit) {
        double[] result = new double[2];
        double bits = -1;
        double bytes = -1;
        switch (unit.trim().toLowerCase()) {
            case "bytes/sec":  bytes = value; break;
            case "kbytes/sec": bytes = value * kb; break;
            case "mbytes/sec": bytes = value * mb; break;
            case "gbytes/sec": bytes = value * gb; break;
            case "tbytes/sec": bytes = value * tb; break;

            case "bits/sec":  bits = value;        m_isBytesUnit = false; break;
            case "kbits/sec": bits = value * kb; ; m_isBytesUnit = false; break;
            case "mbits/sec": bits = value * mb; ; m_isBytesUnit = false; break;
            case "gbits/sec": bits = value * gb; ; m_isBytesUnit = false; break;
            case "tbits/sec": bits = value * tb; ; m_isBytesUnit = false; break;
            default:
                throw new IllegalArgumentException("Invalid unit: " + unit);
        }
        result[0] = bits;
        result[1] = bytes;
        return result;
    }

    public static String convertToHumanReadable(Args args, double val, boolean bitsOrBytes) {
        if (args.isDebug()) System.out.printf("Normalizing val = %,f, kb = %,f\n", val, kb);
        double k = kb;
        if (val == -1) return String.format("%14.14s", "<Unknown>");
        if (val < 0) return String.format("%,5.2f BAD", val);
        if (val == 0) return String.format("%14.14s", "- - - -");
        String tbString = "Tbits";
        String gbString = "Gbits";
        String mbString = "Mbits";
        String kbString = "Kbits";
        String bString = "bits";
        if (bitsOrBytes) {
            tbString = "TBytes";
            gbString = "GBytes";
            mbString = "MBytes";
            kbString = "KBytes";
            bString = "Bytes";
        }
        double tbVal = val / tb;
        double gBVal = val / gb;
        double mBVal = val / mb;
        double kBVal = val / kb;

        String unitString = bString;
        double valUnits = 0;
        if (tbVal > 1.0) {
            valUnits = tbVal;
            unitString = tbString;
        } else if (gBVal > 1.0) {
            valUnits = gBVal;
            unitString = gbString;
        } else if (mBVal > 1.0) {
            valUnits = mBVal;
            unitString = mbString;
        } else if (kBVal > 1.0) {
            valUnits = kBVal;
            unitString = kbString;
        } else {
            valUnits = val;
            unitString = bString;
        }
        unitString += "/sec";
        return String.format(" %,8.2f%s  %s%s%s",
                             valUnits,
                             AnsiCodes.getReset(args.getTermType()),
                             AnsiCodes.getUnderline(args.getTermType()),
                             unitString,
                             AnsiCodes.getReset(args.getTermType()));
//        if (tbVal > 1.0) return String.format("%-5.2f%s %s%-10.10s%s",
//                                              tbVal,
//                                              AnsiCodes.getReset(args.getTermType()),
//                                              AnsiCodes.getUnderline(args.getTermType()),
//                                              tbString,
//                                              AnsiCodes.getReset(args.getTermType()));


//        if (gBVal > 1.0) return String.format("%-5.2f%s %s%-10.10s%s",
//                                              gBVal,
//                                              AnsiCodes.getReset(args.getTermType()),
//                                              AnsiCodes.getUnderline(args.getTermType()),
//                                              gbString,
//                                              AnsiCodes.getReset(args.getTermType()));


//        if (mBVal > 1.0) return String.format("%-5.2f%s %s%-10.10s%s",
//                                              mBVal,
//                                              AnsiCodes.getReset(args.getTermType()),
//                                              AnsiCodes.getUnderline(args.getTermType()),
//                                              mbString,
//                                              AnsiCodes.getReset(args.getTermType()));

//        if (kBVal > 1.0) return String.format("%-5.2f%s %s%-10.10s%s",
//                                              kBVal,
//                                              AnsiCodes.getReset(args.getTermType()),
//                                              AnsiCodes.getUnderline(args.getTermType()),
//                                              kbString,
//                                              AnsiCodes.getReset(args.getTermType()));
//        return String.format("%-5.2f%s %s%-10.10s%s",
//                             val,
//                             AnsiCodes.getReset(args.getTermType()),
//                             AnsiCodes.getUnderline(args.getTermType()),
//                             bString,
//                             AnsiCodes.getReset(args.getTermType()));

    }


    public void setLastOmitted(boolean lastOmitted)       { m_lastOmitted = lastOmitted;       }
    public void setLastResult(String lastResult)          { m_lastResult = lastResult;         }
    public void setVerbose(boolean verbose)               { m_verbose = verbose;               }
    public void setDebug(boolean debug)                   { m_debug = debug;                   }
    public void setGathered(boolean gathered)             { m_gathered = gathered;             }
    public void setTimePeriod(int timePeriod)             { m_timePeriod = timePeriod;         }
    public void setSummaryResults(boolean summaryResults) { m_summaryResults = summaryResults; }
    public void setLocalHost(String localHost)            { m_localHost = localHost;           }
    public void setRemoteHost(String remoteHost)          { m_remoteHost = remoteHost;         }
    public void setIsSingleThread(boolean singleThread)   { m_singleThread = singleThread;     }
    public void setResultEntry(int resultEntry)           { m_resultEntry = resultEntry;       }
    public void incrementResultEntry()                    { m_resultEntry++;                   }
    public void setFinished()                             { m_resultEntry = -999;              }    
    public void setLocalPort(String localPort) { 
        try {
            int l = Integer.valueOf(localPort);
            m_localPort = l;
        } catch (NumberFormatException n) {
            m_localPort = -1;
        }
    }
    
    public void setRemotePort(String remotePort) { 
        try {
            int r = Integer.valueOf(remotePort);
            m_remotePort = r;
        } catch (NumberFormatException n) {
            m_remotePort = -1;
        }
    }




}
