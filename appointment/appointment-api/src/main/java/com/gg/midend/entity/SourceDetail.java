package com.gg.midend.entity;

import java.io.Serializable;

public class SourceDetail implements Serializable {

    private int sourceId;

    private String start;

    private String end;

    private int order;

    private String status;

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "{sourceId="+getSourceId()+", start="+getStart()
                +", end="+getEnd()+", order="+getOrder()+", status="+getStatus()+"}";
    }
}
