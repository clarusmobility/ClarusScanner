package com.clarus12.clarusscanner.dto;

public class WmsSummaryResponse {

    private long cntCompleteCheckInToday;
    private long cntCompleteCheckInMonth;

    private long cntCompleteReleaseToday;
    private long cntCompleteReleaseMonth;

    private long cntCurrentCompleteCheckIn;
    private long cntCurrentReadyRelease;
    private long cntCurrentCompleteRelease;

    public long getCntCompleteCheckInToday() {
        return cntCompleteCheckInToday;
    }

    public void setCntCompleteCheckInToday(long cntCompleteCheckInToday) {
        this.cntCompleteCheckInToday = cntCompleteCheckInToday;
    }

    public long getCntCompleteCheckInMonth() {
        return cntCompleteCheckInMonth;
    }

    public void setCntCompleteCheckInMonth(long cntCompleteCheckInMonth) {
        this.cntCompleteCheckInMonth = cntCompleteCheckInMonth;
    }

    public long getCntCompleteReleaseToday() {
        return cntCompleteReleaseToday;
    }

    public void setCntCompleteReleaseToday(long cntCompleteReleaseToday) {
        this.cntCompleteReleaseToday = cntCompleteReleaseToday;
    }

    public long getCntCompleteReleaseMonth() {
        return cntCompleteReleaseMonth;
    }

    public void setCntCompleteReleaseMonth(long cntCompleteReleaseMonth) {
        this.cntCompleteReleaseMonth = cntCompleteReleaseMonth;
    }

    public long getCntCurrentCompleteCheckIn() {
        return cntCurrentCompleteCheckIn;
    }

    public void setCntCurrentCompleteCheckIn(long cntCurrentCompleteCheckIn) {
        this.cntCurrentCompleteCheckIn = cntCurrentCompleteCheckIn;
    }

    public long getCntCurrentReadyRelease() {
        return cntCurrentReadyRelease;
    }

    public void setCntCurrentReadyRelease(long cntCurrentReadyRelease) {
        this.cntCurrentReadyRelease = cntCurrentReadyRelease;
    }

    public long getCntCurrentCompleteRelease() {
        return cntCurrentCompleteRelease;
    }

    public void setCntCurrentCompleteRelease(long cntCurrentCompleteRelease) {
        this.cntCurrentCompleteRelease = cntCurrentCompleteRelease;
    }
}
