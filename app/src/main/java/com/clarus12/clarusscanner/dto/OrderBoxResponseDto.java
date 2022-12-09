package com.clarus12.clarusscanner.dto;

public class OrderBoxResponseDto {
    /*    "orderBoxId": 536,
    "orderBoxName": "가죽 야구장갑(야구 글로브) 가죽 장",
    "localTrackingNo": "570802882719",
    "overseasTrackingNo": "1000003773355",
    "containerCode": "2000111",
    "senderName": "홍길동",
    "senderAddress": "서울 강남구 남부순환로 2609 1390-10 (도곡동)",
    "receiverName": "Gildong Hong",
    "receiverAddress": "12, 18 65 B St - Jumeirah - Jumeirah 1 - Dubai - United Arab Emirates"/


     */

    long orderBoxId;
    String orderBoxShortId;
    String orderBoxName;
    String localTrackingNo;
    String overseasTrackingNo;
    String shipStatusName;



    String containerCode;
    String senderName;
    String senderAddress;
    String receiverName;
    String receiverAddress;



    String lastShipStatusDate;

    public long getOrderBoxId() {
        return orderBoxId;
    }

    public void setOrderBoxId(long orderBoxId) {
        this.orderBoxId = orderBoxId;
    }

    public String getOrderBoxShortId() {
        return orderBoxShortId;
    }

    public void setOrderBoxShortId(String orderBoxShortId) {
        this.orderBoxShortId = orderBoxShortId;
    }

    public String getOrderBoxName() {
        return orderBoxName;
    }

    public void setOrderBoxName(String orderBoxName) {
        this.orderBoxName = orderBoxName;
    }

    public String getLocalTrackingNo() {
        return localTrackingNo;
    }

    public void setLocalTrackingNo(String localTrackingNo) {
        this.localTrackingNo = localTrackingNo;
    }

    public String getOverseasTrackingNo() {
        return overseasTrackingNo;
    }

    public void setOverseasTrackingNo(String overseasTrackingNo) {
        this.overseasTrackingNo = overseasTrackingNo;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getShipStatusName() {
        return shipStatusName;
    }

    public void setShipStatusName(String shipStatusName) {
        this.shipStatusName = shipStatusName;
    }

    public String getLastShipStatusDate() {
        return lastShipStatusDate;
    }

    public void setLastShipStatusDate(String lastShipStatusDate) {
        this.lastShipStatusDate = lastShipStatusDate;
    }
}
