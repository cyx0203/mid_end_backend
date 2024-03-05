package com.gg.midend.entity;

import java.io.Serializable;

public class SubDepartment implements Serializable {
    private String deptCode;
    private String deptName;

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public String toString() {
        return "{deptCode="+getDeptCode()+", deptName="+getDeptName()+"}";
    }
}
