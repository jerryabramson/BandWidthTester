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
public class AnsiCodes
{

    private static final String ANSI_BOLD          = "\033[1m";
    private static final String ANSI_UNDERLINE     = "\033[4m";
    private static final String ANSI_RESET         = "\033[0m";
    private static final String ANSI_CLEAR_TO_EOL  = "\033[K";
    private static final String CR                 = "\015";
    private static final String BACKSPACE          = "\010";


    public static String getHorizontalLine(TermType term)   { return term.ANSI_LINE;                                }
    public static String getBold(TermType term)             { return (term.isAnsiTerm() ? ANSI_BOLD : "");          }
    public static String getClearToEOL(TermType term)       { return (term.isAnsiTerm() ? ANSI_CLEAR_TO_EOL : "");  }
    public static String getCR(TermType term)               { return (term.isAnsiTerm() ? CR:  "");                 }
    public static String getUnderline(TermType term)        { return (term.isAnsiTerm() ? ANSI_UNDERLINE : "");     }
    public static String getReset(TermType term)            { return (term.isAnsiTerm() ? ANSI_RESET : "");         }
    public static String getBackSpace(TermType term)        { return (term.isAnsiTerm() ? BACKSPACE : "");          }
    
    public static String gotoColumn(TermType term, int col) { return (term.isAnsiTerm() ? String.format("\033[%dG", col) : ""); }
    // standard ANSI color sequences
    public enum ANSI_COLOR
    {
        BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, CYAN, GREY;
        private static final int BASE_COLOR_CODE = 30;
        private static final int BASE_HIGHLIGHT_CODE = 40;

        public static ANSI_COLOR getMin() { return ANSI_COLOR.BLACK; }
        public static ANSI_COLOR getMax() { return ANSI_COLOR.GREY;  }
        public int getValue() { return this.ordinal(); }
        public String getCode(TermType termType)                 { return ((termType.getTerm() == TermType.TERM.ANSI) ? String.format("\033[%dm", getValue() + BASE_COLOR_CODE) : ""); }
        public String getBoldCode(TermType termType)             { return (termType.getTerm() == TermType.TERM.ANSI ?  String.format("\033[%d;1m", getValue() + BASE_COLOR_CODE) : ""); }
        public String getHighlightCode(TermType termType)        { return ((termType.getTerm() == TermType.TERM.ANSI) ? String.format("\033[%dm", getValue() + BASE_HIGHLIGHT_CODE) : ""); }
        public String getReverseHighlightCode(TermType termType) { return ((termType.getTerm() == TermType.TERM.ANSI) ? String.format("\033[%d;1m", getValue() + BASE_HIGHLIGHT_CODE) : ""); }
        public static ANSI_COLOR next(ANSI_COLOR currentColor)   {  return (currentColor == getMax() ? getMin() : ANSI_COLOR.values()[currentColor.ordinal() + 1]); }


    }
}


