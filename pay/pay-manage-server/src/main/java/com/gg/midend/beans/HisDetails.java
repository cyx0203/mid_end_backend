package com.gg.midend.beans;

/**
 * @author txy
 * @create 2023-03-08-16:53
 */
public class HisDetails {

    private String merchantName;
    private String transType;
    private String thirdName;
    private String payTypeName;
    private String transTypeName;
    private String flowNo;
    private String transAmt;
    private String operId;
    private String transTime;
    private String refundFlowNo;
    private String serialNo;
    private String refundSerialNo;

    public HisDetails() {
    }

    public HisDetails(String merchantName, String transType, String thirdName, String payTypeName, String transTypeName, String flowNo, String transAmt, String operId, String transTime, String refundFlowNo, String serialNo, String refundSerialNo) {
        this.merchantName = merchantName;
        this.transType = transType;
        this.thirdName = thirdName;
        this.payTypeName = payTypeName;
        this.transTypeName = transTypeName;
        this.flowNo = flowNo;
        this.transAmt = transAmt;
        this.operId = operId;
        this.transTime = transTime;
        this.refundFlowNo = refundFlowNo;
        this.serialNo = serialNo;
        this.refundSerialNo = refundSerialNo;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getThirdName() {
        return thirdName;
    }

    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
    }

    public String getPayTypeName() {
        return payTypeName;
    }

    public void setPayTypeName(String payTypeName) {
        this.payTypeName = payTypeName;
    }

    public String getTransTypeName() {
        return transTypeName;
    }

    public void setTransTypeName(String transTypeName) {
        this.transTypeName = transTypeName;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    public String getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(String transAmt) {
        this.transAmt = transAmt;
    }

    public String getOperId() {
        return operId;
    }

    public void setOperId(String operId) {
        this.operId = operId;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getRefundFlowNo() {
        return refundFlowNo;
    }

    public void setRefundFlowNo(String refundFlowNo) {
        this.refundFlowNo = refundFlowNo;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getRefundSerialNo() {
        return refundSerialNo;
    }

    public void setRefundSerialNo(String refundSerialNo) {
        this.refundSerialNo = refundSerialNo;
    }
}
