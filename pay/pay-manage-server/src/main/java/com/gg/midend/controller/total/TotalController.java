package com.gg.midend.controller.total;

import com.gg.core.controller.BaseController;
import com.gg.midend.service.TotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TotalController extends BaseController {

    @Autowired
    private TotalService totalService;

    @RequestMapping("/overview")
    public Object selectIndex(@RequestBody Map paramMap){
        return totalService.selectTotal(paramMap);
    }

}
