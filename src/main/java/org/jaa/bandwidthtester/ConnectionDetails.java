
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.jaa.bandwidthtester;

enum Type {MIN, MAX, AVG};
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
    private String avgColor = "";
    private String defaultUnitFormat = "  %-10.10s  ";
    private String defaultValFormat = " %,8.2f";
    private boolean isColored = true;

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
    protected double   m_avgBitsPerSec   = Double.NaN;
    protected double   m_avgBytesPerSec  = Double.NaN;
    protected String   m_maxBitsUnit     = "";
    protected String   m_maxBytesUnit    = "";
    protected String   m_minBitsUnit     = "";
    protected String   m_minBytesUnit    = "";
    protected String   m_avgBitsUnit     = "";
    protected String   m_avgBytesUnit    = "";
    protected boolean  m_isBytesUnit     = true;
    private   boolean  m_verbose         = false;
    private   boolean  m_debug           = false;
    private   boolean  m_lastOmitted     = false;
    private   String   m_lastResult      = "";
    private ResultDetails m_resultDetails = new ResultDetails();
    private final Args m_myArgs;


    /**
     * Disables the use of ANSI color codes in the output.
     * This method sets the color strings for minimum and maximum values to empty strings,
     * adjusts the default formatting for units and values to a non-colored version,
     * and sets the isColored flag to false.
     */
    public void disableColors() {
        minColor = "";
        maxColor = "";
        avgColor = "";
        defaultUnitFormat = " %s";
        defaultValFormat = "%.2f";
        isColored = false;
    }

    /**
     * Enables the use of ANSI color codes in the output.
     * This method sets the color strings for minimum and maximum values to specific ANSI codes,
     * resets the default formatting for units and values to support colored output,
     * and sets the isColored flag to true.
     */
    public void enableColors() {
        minColor = AnsiCodes.ANSI_COLOR.RED.getReverseBoldCode(m_myArgs.getTermType());
        maxColor = AnsiCodes.ANSI_COLOR.BLUE.getReverseBoldCode(m_myArgs.getTermType());
        avgColor = AnsiCodes.ANSI_COLOR.GREY.getReverseBoldCode(m_myArgs.getTermType());
        defaultUnitFormat = "  %-10.10s  ";
        defaultValFormat = " %,8.2f";
        isColored = true;
    }
    public ResultDetails getResultDetails()                   { return m_resultDetails;          }
    public void setResultDetails(ResultDetails resultDetails) { m_resultDetails = resultDetails; }

    public String  getLastResult()    { return m_lastResult;            }
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
    public String getDefaultUnitFormat() { return defaultUnitFormat;    }
    public String getDefaultValFormat()  { return defaultValFormat;     }
    public boolean isColored()           { return isColored;            }

    public ConnectionDetails() {
        throw new IllegalArgumentException("No arguments provided");
    }
    /**
     * Constructor
     * @param args Command line arguments
     */
    public ConnectionDetails(Args args) {
        m_myArgs = args;
        enableColors();
    }


    public String getMinBitsBytesPerSec() { return getMinMaxBitsBytesPerSec(Type.MIN); }
    public String getMaxBitsBytesPerSec() { return getMinMaxBitsBytesPerSec(Type.MAX); }
    public String getAvgBitsBytesPerSec() { return getMinMaxBitsBytesPerSec(Type.AVG); }

    public void setMinBitsBytesPerSec(double value, String unit)  { setMinMaxBitsBytesPerSec(value, unit, Type.MIN); }
  public void setAvgBitsBytesPerSec(double value, String unit)  { setMinMaxBitsBytesPerSec(value, unit, Type.AVG); }
    public void setMaxBitsBytesPerSec(double value, String unit)  { setMinMaxBitsBytesPerSec(value, unit, Type.MAX); }

    /**
     * Retrieves the minimum or maximum bandwidth or bit rate in the specified unit.
     *
     * @param type Whether to retrieve the minimum, maximum, or average value.
     * @return A formatted string representing the minimum or maximum bandwidth or bit rate.
     *         If the unit is bytes, the result will be in bytes/sec.
     *         If the unit is bits, the result will be in bits/sec.
     *         If no minimum or maximum value has been set, the result will be "N/A".
     *
     * @see #convertToHumanReadable(Args, double, boolean, String, String, boolean)
     */
    public String getMinMaxBitsBytesPerSec(Type type) {
        String result = "N/A";
        double value = (type == Type.MIN) ? m_minBitsPerSec : ((type == Type.MAX) ? m_maxBitsPerSec : m_avgBitsPerSec);
        String unit = (type == Type.MIN) ? m_minBitsUnit : ((type == Type.MAX) ? m_maxBitsUnit : m_avgBitsUnit);
        if (m_isBytesUnit) {
            if (value != Double.MIN_VALUE && value != Double.MAX_VALUE && value != Double.NaN) {
                result = String.format("%s%s",
                                       (type == Type.MIN) ? minColor : ((type == Type.MAX) ? maxColor : avgColor),
                                       convertToHumanReadable(m_myArgs, value, true,
                                                              getDefaultUnitFormat(), getDefaultValFormat(),
                                                              isColored()));
            }
        } else {
            if (value != Double.MIN_VALUE && value != Double.MAX_VALUE && value!= Double.NaN) {
                result = String.format("%s%s",
                                       (type == Type.MIN) ? minColor : ((type == Type.MAX) ? maxColor : avgColor),
                                       convertToHumanReadable(m_myArgs, value, m_isBytesUnit,
                                                              getDefaultUnitFormat(), getDefaultValFormat(),
                                                              isColored()));
            }
        }
        return result;
    }

    /**
     * Sets the minimum or maximum bandwidth or bit rate in the specified unit.
     *
     * @param value The bandwidth or bit rate value.
     * @param unit The unit of the bandwidth or bit rate value.
     * @param type Whether to set the minimum, maximum, or average value.
     *
     * The function converts the given value and unit to bits or bytes per second,
     * and then updates the corresponding minimum or maximum value and unit.
     * If the given value is less than the current minimum or greater than the current maximum,
     * the function updates the minimum or maximum value and unit accordingly.
     *
     * If the unit is bytes, the function converts the value to bytes per second.
     * If the unit is bits, the function converts the value to bits per second.
     *
     * If the unit is invalid, the function throws an IllegalArgumentException.
     *
     * If the verbose flag is set to true, the function prints debug information.
     */
    public void setMinMaxBitsBytesPerSec(double value, String unit, Type type) {
        double[] converted = convertUnitToBitsAndBytes(value, unit);
        double bitsPerSec = converted[0];
        double bytesPerSec = converted[1];
        if (!m_isBytesUnit) {
            if (isVerbose()) System.out.printf("\tset%sBitsPerSec(%.2f %s %s)\n", value, unit, type);

            if (bitsPerSec != -1 &&
                    ((type == Type.MIN  && bitsPerSec < m_minBitsPerSec) ||
                            (type == Type.MAX && bitsPerSec > m_maxBitsPerSec) ||
                            (type == Type.AVG && bitsPerSec != -1 && bitsPerSec != Double.NaN)))
            {
                if (isVerbose()) System.out.printf(" [Setting %sBitsPerSec to %.0f bits/sec (orig = %s)] ", type, bitsPerSec, unit);
                if (type == Type.MIN  && bitsPerSec < m_minBitsPerSec) {
                    m_minBitsPerSec = bitsPerSec;
                    m_minBitsUnit = unit;
                } else if (type == Type.MAX && bitsPerSec > m_maxBitsPerSec) {
                    m_maxBitsPerSec = bitsPerSec;
                    m_maxBitsUnit = unit;
                } else if (type == Type.AVG) {
                    m_avgBitsPerSec = bitsPerSec;
                    m_avgBitsUnit = unit;
                }
            }
        } else {
            if (isVerbose()) System.out.printf("\tset %sBytesPerSec(%.2f %s): [current %s bytes/sec = %.0f] ",
                                               type, value, unit);
                if (isVerbose()) System.out.printf(" [Setting %sBytesPerSec to %.0f bytes/sec (orig = %s)] ", type, bytesPerSec, unit);
                if (type == Type.MIN && bytesPerSec < m_minBytesPerSec) {
                    m_minBytesPerSec = bytesPerSec;
                    m_minBytesUnit = unit;
                } else if (type == Type.MAX && bytesPerSec > m_maxBytesPerSec) {
                    m_maxBytesPerSec = bytesPerSec;
                    m_maxBytesUnit = unit;
                } else if (type == Type.AVG) {
                    m_avgBytesPerSec = bytesPerSec;
                    m_avgBytesUnit = unit;
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

    public static String convertToHumanReadable(Args args, double val, boolean bitsOrBytes, String fmt1, String fmt2, boolean isColor) {
        if (args.isDebug()) System.out.printf("Normalizing val = %,f, kb = %,f\n", val, kb);
        double k = kb;
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
        return String.format(fmt2 + "%s%s" + fmt1 + "%s",
                             valUnits,
                             AnsiCodes.getReset(args.getTermType()),
                             AnsiCodes.getBold(args.getTermType()),
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
