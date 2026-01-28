/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jaa.bandwidthtester;


/**
 *
 * @author jerry
 */
@SuppressWarnings("unused")
public class TerminalType
{
    public  String ANSI_LINE                   = "─";
    public  String BOTTOM_LEFT_CORNER          = "└";
    public  String BOTTOM_RIGHT_CORNER         = "┘";
    public  String LEFT_COLUMN_LINE            = "├";
    public  String RIGHT_COLUMN_LINE           = "┤";
    public  String TOP_LEFT_CORNER             = "┌";
    public  String TOP_RIGHT_CORNER            = "┐";
    public  String VERTICAL_BAR                = "│";
    public String FANCY_RIGHT_ARROW            = "\ue0b4";
    public String FANCY_LEFT_ARROW             = "\ue0b6";

    private TERM m_term;
    private boolean m_utf;
    
    
    protected enum TERM {ANSI, DUMB}

    TerminalType()
    {
        m_term = TERM.ANSI;
        m_utf = true;

        
        String termEnv = System.getenv("TERM");
        if (termEnv == null) termEnv = System.getProperty("TERM");
        String lang = System.getenv("LANG");
        if (lang == null) lang = System.getProperty("LANG");     
        if ((termEnv == null) || termEnv.isEmpty() || termEnv.equalsIgnoreCase("dumb")) {
            m_term = TERM.DUMB;
        }
        if ((lang == null) || !lang.toLowerCase().contains("utf")) {
            m_utf = false;
        }

        // Windows has special Handling and escape sequence processing
        if (OS.getMyOS() == OS.OSTypes.WINDOWS) {
            m_utf = true;
            m_term = TERM.ANSI;
        }

        if (!m_utf) {
            VERTICAL_BAR = "|";
            BOTTOM_LEFT_CORNER = "+";
            BOTTOM_RIGHT_CORNER = "+";
            TOP_LEFT_CORNER = "+";
            TOP_RIGHT_CORNER = "+";
            LEFT_COLUMN_LINE = "+";
            RIGHT_COLUMN_LINE = "+";
            ANSI_LINE = "-";
            FANCY_RIGHT_ARROW=")";
            FANCY_LEFT_ARROW="(";
        }
    }

    

    public boolean isAnsiTerm()                          { return (m_term == TERM.ANSI);   }
    public boolean isUTF()                               { return m_utf;                   }
    @SuppressWarnings("ClassEscapesDefinedScope")
    public TERM getTerm()                                { return this.m_term;             }
    
}
