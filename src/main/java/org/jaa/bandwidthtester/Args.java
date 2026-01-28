package org.jaa.bandwidthtester;


/**
 *
 * @author jerry
 */

public class Args {
    public String client;
    public String omit;
    public String parallel;
    public int times=0;
    public int repeat=0;
    public boolean verbose = false;
    public boolean debug   = false;
    public boolean single  = false;
    public boolean reverse = false;
    
    public final StringBuilder remainingArgs = new StringBuilder();
    
    private TerminalType m_termType;
    public void setTermType(TerminalType termType) { m_termType = termType; }
    public TerminalType getTermType() { return m_termType; }
    public boolean isVerbose() { return verbose; }
    public boolean isDebug() { return debug; }

    public int getRepeat() { return repeat; }
    public String[] getRemainingArgs() {
        return remainingArgs.toString().split("\\s+");
    }
}
