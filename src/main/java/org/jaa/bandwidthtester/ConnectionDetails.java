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

    private String minColor = "";
    private String maxColor = "";
    private String defaultUnitFormat = "  %-10.10s  ";
    private String defaultValFormat = " %,8.2f";
    private boolean isColored = true;

    public void disableColors() {
        minColor = "";
        maxColor = "";
        defaultUnitFormat = " %s";
        defaultValFormat = "%.2f";
        isColored = false;
    }

    public void enableColors() {
        minColor = AnsiCodes.ANSI_COLOR.RED.getReverseBoldCode(m_myArgs.getTermType());
        maxColor = AnsiCodes.ANSI_COLOR.BLUE.getReverseBoldCode(m_myArgs.getTermType());
        defaultUnitFormat = "  %-10.10s  ";
        defaultValFormat = " %,8.2f";
        isColored = true;
    }
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
    protected boolean  m_isBytesUnit     = true;
    private   boolean  m_verbose         = false;
    private   boolean  m_lastOmitted     = false;
    private ResultDetails m_resultDetails = new ResultDetails();
    private final Args m_myArgs;

    public ResultDetails getResultDetails()                   { return m_resultDetails;          }
    public void setResultDetails(ResultDetails resultDetails) { m_resultDetails = resultDetails; }


    public boolean isLastOmitted()    { return m_lastOmitted;           }

    public boolean isVerbose()        { return m_verbose;               }
    public boolean isGathered()       { return m_gathered;              }
    public boolean isSingleThread()   { return m_singleThread;          }
    public boolean isFinished()       { return (m_resultEntry != -999); }
    public boolean isSummaryResults() { return m_summaryResults;        }
    public String  getLocalHost()     { return m_localHost;             }
    public int     getRemotePort()    { return m_remotePort;            }
    public int     getLocalPort()     { return m_localPort;             }
    public int     getTimePeriod()    { return m_timePeriod;            }
    public String  getRemoteHost()    { return m_remoteHost;            }
    public int     getResultEntry()   { return m_resultEntry;           }
    public String getDefaultUnitFormat() { return defaultUnitFormat;    }
    public String getDefaultValFormat()  { return defaultValFormat;     }
    public boolean isColored()           { return isColored;            }


    /**
     * Constructor
     * @param args Command line arguments
     */
    public ConnectionDetails(Args args) {
        m_myArgs = args;
        enableColors();
    }

    public String getMinBitsBytesPerSec() {
        String result = "N/A";
        if (m_isBytesUnit) {
            if (m_minBytesPerSec != Double.MAX_VALUE) {
                result = String.format("%s%s",
                                       minColor,
                                       convertToHumanReadable(m_myArgs, m_minBytesPerSec, true,
                                                              getDefaultUnitFormat(), getDefaultValFormat(),
                                                              isColored()));
            }
        } else {
            if (m_minBitsPerSec != Double.MAX_VALUE) {
                result = String.format("%s%s",
                                       minColor,
                                       convertToHumanReadable(m_myArgs, m_minBitsPerSec, false,
                                                              getDefaultUnitFormat(), getDefaultValFormat(),
                                                              isColored()));
            }
        }

        return result;
    }
    public String getMaxBitsBytesPerSec() {
        String result = "N/A";
        if (m_isBytesUnit) {
            if (m_maxBytesPerSec != Double.MIN_VALUE) {
                result = String.format("%s%s",
                                       maxColor,
                                       convertToHumanReadable(m_myArgs, m_maxBytesPerSec, true,
                                                              getDefaultUnitFormat(), getDefaultValFormat(),
                                                              isColored()));
            }
        } else {
            if (m_maxBitsPerSec != Double.MIN_VALUE) {
                result = String.format("%s%s",
                                       maxColor,
                                       convertToHumanReadable(m_myArgs, m_maxBitsPerSec, false,
                                                              getDefaultUnitFormat(), getDefaultValFormat(),
                                                              isColored()));
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
            }
        } else {
            if (isVerbose()) System.out.printf("\tsetMaxBytesPerSec(%.2f %s): [current max bytes/sec = %.0f] ",
                                               value, unit,
                                               ((m_maxBytesPerSec != Double.MIN_VALUE) ? m_maxBytesPerSec : -1));

            if (maxBytesPerSec != -1 && maxBytesPerSec > m_maxBytesPerSec) {
                if (isVerbose()) System.out.printf(" [Setting maxBytesPerSec to %.0f bytes/sec (orig = %s)] ", maxBytesPerSec, unit);
                m_maxBytesPerSec = maxBytesPerSec;
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
            }
        } else {
            if (isVerbose()) System.out.printf("\tsetMinBytesPerSec(%.2f %s): [current min bytes/sec = %.0f] ",
                                               value, unit,
                                               ((m_minBytesPerSec != Double.MAX_VALUE) ? m_minBytesPerSec : -1));
            if (minBytesPerSec != -1 && minBytesPerSec < m_minBytesPerSec) {
                if (isVerbose()) System.out.printf(" [Setting minBytesPerSec to %.0f bytes/sec (orig = %s)] ", minBytesPerSec, unit);
                m_minBytesPerSec = minBytesPerSec;
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

            case "bits/sec":  bits = value;      m_isBytesUnit = false; break;
            case "kbits/sec": bits = value * kb; m_isBytesUnit = false; break;
            case "mbits/sec": bits = value * mb; m_isBytesUnit = false; break;
            case "gbits/sec": bits = value * gb; m_isBytesUnit = false; break;
            case "tbits/sec": bits = value * tb; m_isBytesUnit = false; break;
            default:
                throw new IllegalArgumentException("Invalid unit: " + unit);
        }
        result[0] = bits;
        result[1] = bytes;
        return result;
    }

    public static String convertToHumanReadable(Args args, double val, boolean bitsOrBytes, String fmt1, String fmt2, boolean isColor) {
        if (args.isDebug()) System.out.printf("Normalizing val = %,f, kb = %,f\n", val, kb);
        if (val <= 0) {
            if (isColor) return ("- - - - -" + AnsiCodes.getReset(args.getTermType()));
            else return "STALLED";
        }
        String tbString = "Tbits";
        String gbString = "Gbits";
        String mbString = "Mbits";
        String kbString = "Kbits";
        String bString = "bits";
        if (bitsOrBytes) {
            // Tbytes/sec
            // 1234567890
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

        String unitString;
        double valUnits;
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
        return String.format(fmt2 + "%s%s" + fmt1 + "%s",
                             valUnits,
                             AnsiCodes.getReset(args.getTermType()),
                             AnsiCodes.getBold(args.getTermType()),
                             unitString,
                             AnsiCodes.getReset(args.getTermType()));

    }


    public void setLastOmitted(boolean lastOmitted)       { m_lastOmitted = lastOmitted;       }
    public void setVerbose(boolean verbose)               { m_verbose = verbose;               }
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
            m_localPort = Integer.parseInt(localPort);
        } catch (NumberFormatException n) {
            m_localPort = -1;
        }
    }
    
    public void setRemotePort(String remotePort) { 
        try {
            m_remotePort = Integer.parseInt(remotePort);
        } catch (NumberFormatException n) {
            m_remotePort = -1;
        }
    }




}
