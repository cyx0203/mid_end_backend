package com.gg.midend.beans;

/**
 * @author txy
 * @create 2023-03-09-15:53
 */
public class TransDetails {

    private String merchantName;
    private String orderTrace;
    private String channelName;
    private String goodsName;
    private String buyerName;
    private String payStatus;
    private String refundStatus;
    private String createTimeFormat;
    private String orderAmt;
    private String refundAmt;

    public TransDetails() {
    }

    public TransDetails(String merchantName, String orderTrace, String channelName, String goodsName, String buyerName, String payStatus, String refundStatus, String createTimeFormat, String orderAmt, String refundAmt) {
        this.merchantName = merchantName;
        this.orderTrace = orderTrace;
        this.channelName = channelName;
        this.goodsName = goodsName;
        this.buyerName = buyerName;
        this.payStatus = payStatus;
        this.refundStatus = refundStatus;
        this.createTimeFormat = createTimeFormat;
        this.orderAmt = orderAmt;
        this.refundAmt = refundAmt;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getOrderTrace() {
        return orderTrace;
    }

    public void setOrderTrace(String orderTrace) {
        this.orderTrace = orderTrace;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getCreateTimeFormat() {
        return createTimeFormat;
    }

    public void setCreateTimeFormat(String createTimeFormat) {
        this.createTimeFormat = createTimeFormat;
    }

    public String getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(String orderAmt) {
        this.orderAmt = orderAmt;
    }

    public String getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(String refundAmt) {
        this.refundAmt = refundAmt;
    }
}
