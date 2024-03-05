package com.gg.midend.bean;

import com.gg.core.annotation.Mobile;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class TestBean {

    /**
     * 院区号
     */
    private String hospitalId;

    /**
     * 患者ID
     */
    private String id;

    public TestBean(){
        this.id = "1";
        this.hospitalId = "hos_1";
        System.out.println("test:"+this.hospitalId);
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public String getId() {
        return id;
    }
}
