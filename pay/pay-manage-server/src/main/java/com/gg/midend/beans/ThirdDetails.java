package com.gg.midend.beans;

/**
 * @author txy
 * @create 2023-03-09-16:53
 */
public class ThirdDetails {

    private String merchantName;
    private String transType;
    private String thirdName;
    private String flowNo;
    private String transAmt;
    private String payerAccount;
    private String transTime;
    private String refundFlowNo;
    private String serialNo;
    private String refundSerialNo;

    public ThirdDetails() {
    }

    public ThirdDetails(String merchantName, String transType, String thirdName, String flowNo, String transAmt, String payerAccount, String transTime, String refundFlowNo, String serialNo, String refundSerialNo) {
        this.merchantName = merchantName;
        this.transType = transType;
        this.thirdName = thirdName;
        this.flowNo = flowNo;
        this.transAmt = transAmt;
        this.payerAccount = payerAccount;
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

    public String getPayerAccount() {
        return payerAccount;
    }

    public void setPayerAccount(String payerAccount) {
        this.payerAccount = payerAccount;
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
