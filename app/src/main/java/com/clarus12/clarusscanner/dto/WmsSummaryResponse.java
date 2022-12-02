package com.clarus12.clarusscanner.dto;

public class WmsSummaryResponse {

    long completeCheckIn;
    long readyRelease;
    long completeRelease;


    public long getCompleteCheckIn() {
        return completeCheckIn;
    }

    public void setCompleteCheckIn(long completeCheckIn) {
        this.completeCheckIn = completeCheckIn;
    }

    public long getReadyRelease() {
        return readyRelease;
    }

    public void setReadyRelease(long readyRelease) {
        this.readyRelease = readyRelease;
    }

    public long getCompleteRelease() {
        return completeRelease;
    }

    public void setCompleteRelease(long completeRelease) {
        this.completeRelease = completeRelease;
    }
}
