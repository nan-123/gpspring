package com.gupaoedu.vip.spring.demo.action;

import com.gupaoedu.vip.spring.formework.annotation.GPController;
import com.gupaoedu.vip.spring.demo.service.IDemoService;
import com.gupaoedu.vip.spring.formework.annotation.GPAutowried;
import com.gupaoedu.vip.spring.formework.annotation.GPRequestMapping;
import com.gupaoedu.vip.spring.formework.annotation.GPRequestParam;
import com.gupaoedu.vip.spring.formework.webmvc.GPModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@GPController
@GPRequestMapping("/demo")
public class DemoAction {
    @GPAutowried
    private IDemoService demoService;

    @GPRequestMapping("query.json")
    public void query(HttpServletRequest req, HttpServletResponse resp,
                      @GPRequestParam("name")String name) throws Exception{

        String result = demoService.get(name);
        resp.getWriter().write(result);

    }

    @GPRequestMapping("/first.html")
    public GPModelAndView getkk(@GPRequestParam("teacher")String teacher){
        String result = demoService.get(teacher);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new GPModelAndView("first.html", model);
    }

}
