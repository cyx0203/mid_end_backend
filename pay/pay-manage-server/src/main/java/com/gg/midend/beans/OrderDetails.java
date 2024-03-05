package com.gg.midend.beans;

import lombok.Data;

/**
 * @author lfm
 * @create 2023-03-09-15:53
 */
@Data
public class OrderDetails {

    private String accountDate;
    private String orderTrace;
    private String orderId;
    private String buyerName;
    private String buyerPhone;
    private String merchantName;
    private String channelName;
    private String payTypeName;
    private String goodsName;
    private String orderSum;
    private String orderAmt;
    private String acctPay;
    private String fundPaySumamt;
    private String transType;
    private String status;

}
