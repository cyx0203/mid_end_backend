package com.gg.midend;

import com.gg.midend.bean.TestBean;

public class TestStartApplication {

    public void getData(){
        TestBean t = new TestBean();
        System.out.println(t.getHospitalId());
        System.out.println(t.getId());
    }
}
