package com.gupaoedu.vip.demo.mvc.action;

import com.gupaoedu.vip.demo.mvc.service.IDemoService;
import com.gupaoedu.vip.spring.annotation.Autowried;
import com.gupaoedu.vip.spring.annotation.Controller;
import com.gupaoedu.vip.spring.annotation.RequestMapping;

@Controller
public class MyAction {

    @Autowried
    private IDemoService demoService;

    @RequestMapping("/index.html")
    public void query(){}
}
