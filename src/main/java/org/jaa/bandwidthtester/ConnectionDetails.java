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
    private String m_localHost;
    private String m_remoteHost;
    private int m_localPort;
    private int m_remotePort;
    private int m_timePeriod;
    private int m_resultEntry = 0;
    private boolean m_gathered = false;
    private boolean m_summaryResults = false;
    private boolean m_singleThread = false;

    public boolean isGathered()       { return m_gathered;              }
    public boolean isSingleThread()   { return m_singleThread;          }
    public boolean isFInished()       { return (m_resultEntry == -999); }
    public boolean isSummaryResults() { return m_summaryResults;        }
    public String  getLocalHost()     { return m_localHost;             }
    public int     getRemotePort()    { return m_remotePort;            }
    public int     getLocalPort()     { return m_localPort;             }
    public int     getTimePeriod()    { return m_timePeriod;            }
    public String  getRemoteHost()    { return m_remoteHost;            }
    public int     getResultEntry()   { return m_resultEntry;    }
    
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
