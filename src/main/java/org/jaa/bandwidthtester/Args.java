package org.jaa.bandwidthtester;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
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
    public boolean verbose = false;
    public boolean debug   = false;
    public boolean UTF     = false;
    public boolean single  = false;
    public StringBuilder remainingArgs = new StringBuilder();    
    
    private TermType m_termType;
    private OS m_myOS;
    
    public void setTermType(TermType termType) { m_termType = termType; }
    public void setOS(OS myOS) { m_myOS = myOS; }
    
    public TermType getTermType() { return m_termType; }
    public OS getOS() { return m_myOS; }

}
