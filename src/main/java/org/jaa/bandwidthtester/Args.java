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
    public boolean UTF     = false;
    public boolean single  = false;
    public boolean reverse = false;
    
    public StringBuilder remainingArgs = new StringBuilder();    
    
    private TermType m_termType;
    private OS m_myOS;
    
    public void setTermType(TermType termType) { m_termType = termType; }
    public void setOS(OS myOS) { m_myOS = myOS; }
    
    public TermType getTermType() { return m_termType; }
    public OS getOS() { return m_myOS; }
    public boolean isVerbose() { return verbose; }
    public boolean isDebug() { return debug; }
    public boolean isUTF() { return UTF; }
    public boolean isSingle() { return single; }
    public boolean isReverse() { return reverse; }
    public int getTimes() { return times; }
    public int getRepeat() { return repeat; }
    public String getClient() { return client; }
    public String getOmit() { return omit; }
    public String getParallel() { return parallel; }
    public String[] getRemainingArgs() {
        return remainingArgs.toString().split("\\s+");
    }


}
