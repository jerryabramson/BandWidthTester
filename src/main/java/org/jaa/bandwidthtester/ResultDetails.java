package org.jaa.bandwidthtester;

import java.util.Date;

public class ResultDetails {
    String min = "N/A";
    String max = "N/A";
    String avg = "N/A";
    int rc = -1;
    Date runDate = new Date();

    public ResultDetails() {}
    public ResultDetails(int rc, String min, String max, String avg) {
        this.rc = rc;
        this.min = min;
        this.max = max;
        this.avg = avg;
    }

    @Override
    public String toString() {

        return String.format("Result=%d, min=%s, max=%s, avg=%s", rc, min, max, avg);
    }


    public void setMin(String min) { this.min = min; }
    public void setMax(String max) { this.max = max; }
    public void setAvg(String avg) { this.avg = avg; }
    public void setRc(int rc)      { this.rc = rc;   }
    public Date getRunDate()       { return runDate; }
    public int getRc()             { return rc;      }
    public void setRunDate(Date runDate) { this.runDate = runDate; }
    public String getMin()         { return min;     }
    public String getMax()         { return max;     }
    public String getAvg()         { return avg;     }

}
