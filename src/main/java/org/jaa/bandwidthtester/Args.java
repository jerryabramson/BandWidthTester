package org.jaa.bandwidthtester;

/*
 * Click njbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

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
    public boolean android = false;
    private TerminalType m_termType;
    private String m_androidIperfPath = "";
    private String m_androidADBPath = "";
    private String m_androidHome = "";

    public final StringBuilder remainingArgs = new StringBuilder();

    public String getAndroidHome() { return m_androidHome; }
    public void setAndroidHome(String androidHome) { m_androidHome = androidHome; }
    public boolean isAndroid() { return android; }
    public String getAndroidIperfPath() { return m_androidIperfPath; }
    public void setAndroidIperfPath(String path) { m_androidIperfPath = path; }
    public String getAndroidADBPath() { return m_androidADBPath; }
    public void setAndroidADBPath(String path) { m_androidADBPath = path; }
    public void setTermType(TerminalType termType) { m_termType = termType; }
    public TerminalType getTermType() { return m_termType; }
    public boolean isVerbose() { return verbose; }
    public boolean isDebug() { return debug; }
    public int getRepeat() { return repeat; }
    public void enableAndroid() {android = true; }

    public String[] getRemainingArgs() {
        return remainingArgs.toString().split("\\s+");
    }

}
