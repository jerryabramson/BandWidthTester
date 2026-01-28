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
public final class AnsiCodes
{

    private AnsiCodes() { /**/ }

    private static final String ANSI_BOLD          = "\033[1m";

    private static final String ANSI_RESET         = "\033[0m";
    private static final String ANSI_CLEAR_TO_EOL  = "\033[K";
    private static final String CR                 = "\015";
    private static final String BACKSPACE          = "\010";



    public static String getHorizontalLine(TerminalType term)   { return term.ANSI_LINE;                                }
    public static String getBold(TerminalType term)             { return (term.isAnsiTerm() ? ANSI_BOLD : "");          }
    public static String getClearToEOL(TerminalType term)       { return (term.isAnsiTerm() ? ANSI_CLEAR_TO_EOL : "");  }
    public static String getCR(TerminalType term)               { return (term.isAnsiTerm() ? CR:  "");                 }

    public static String getReset(TerminalType term)            { return (term.isAnsiTerm() ? ANSI_RESET : "");         }
    public static String getBackSpace(TerminalType term)        { return (term.isAnsiTerm() ? BACKSPACE : "");          }


    public static String gotoColumn(TerminalType term, int col) { return (term.isAnsiTerm() ? String.format("\033[%dG", col) : ""); }
    // standard ANSI color sequences
    @SuppressWarnings("unused")
    public enum ANSI_COLOR
    {
        BLACK, RED, GREEN, YELLOW, BLUE, PURPLE, CYAN, GREY;
        private static final int BASE_COLOR_CODE = 30;
        private static final int BASE_HIGHLIGHT_CODE = 40;

        public int getValue() { return this.ordinal(); }
        public String getCode(TerminalType termType)                 { return ((termType.getTerm() == TerminalType.TERM.ANSI) ? String.format("\033[%dm", getValue() + BASE_COLOR_CODE) : "");       }
        public String getBoldCode(TerminalType termType)             { return (termType.getTerm() == TerminalType.TERM.ANSI ?  String.format("\033[%d;1m", getValue() + BASE_COLOR_CODE) : "");      }
        public String getHighlightCode(TerminalType termType)        { return ((termType.getTerm() == TerminalType.TERM.ANSI) ? String.format("\033[%dm", getValue() + BASE_HIGHLIGHT_CODE) : "");   }
        public String getReverseHighlightCode(TerminalType termType) { return ((termType.getTerm() == TerminalType.TERM.ANSI) ? String.format("\033[%d;1m", getValue() + BASE_HIGHLIGHT_CODE) : ""); }
        public String getReverseBoldCode(TerminalType termType)      { return ((termType.getTerm() == TerminalType.TERM.ANSI) ? String.format("\033[%d;7m", getValue() + BASE_COLOR_CODE) : "");     }
    }
}


