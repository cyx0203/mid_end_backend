package com.gg.midend.beans;

/**
 * @author txy
 * @create 2023-03-09-15:53
 */
public class CheckDetails {

    private String accountDate;
    private String checkId;
    private String orderId;
    private String refundId;
    private String merchantName;
    private String channelName;
    private String payTypeName;
    private String transType;
    private String thirdTransAmt;
    private String bizTransAmt;
    private String errorType;
    private String hisAccountSource;
    private String thirdAccountSource;

    public CheckDetails() {
    }

    public CheckDetails(String accountDate, String checkId, String orderId, String refundId, String merchantName, String channelName, String payTypeName, String transType, String thirdTransAmt, String bizTransAmt, String errorType, String hisAccountSource, String thirdAccountSource) {
        this.accountDate = accountDate;
        this.checkId = checkId;
        this.orderId = orderId;
        this.refundId = refundId;
        this.merchantName = merchantName;
        this.channelName = channelName;
        this.payTypeName = payTypeName;
        this.transType = transType;
        this.thirdTransAmt = thirdTransAmt;
        this.bizTransAmt = bizTransAmt;
        this.errorType = errorType;
        this.hisAccountSource = hisAccountSource;
        this.thirdAccountSource = thirdAccountSource;
    }

    public String getAccountDate() {
        return accountDate;
    }

    public void setAccountDate(String accountDate) {
        this.accountDate = accountDate;
    }

    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getPayTypeName() {
        return payTypeName;
    }

    public void setPayTypeName(String payTypeName) {
        this.payTypeName = payTypeName;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getThirdTransAmt() {
        return thirdTransAmt;
    }

    public void setThirdTransAmt(String thirdTransAmt) {
        this.thirdTransAmt = thirdTransAmt;
    }

    public String getBizTransAmt() {
        return bizTransAmt;
    }

    public void setBizTransAmt(String bizTransAmt) {
        this.bizTransAmt = bizTransAmt;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getHisAccountSource() {
        return hisAccountSource;
    }

    public void setHisAccountSource(String hisAccountSource) {
        this.hisAccountSource = hisAccountSource;
    }

    public String getThirdAccountSource() {
        return thirdAccountSource;
    }

    public void setThirdAccountSource(String thirdAccountSource) {
        this.thirdAccountSource = thirdAccountSource;
    }
}
