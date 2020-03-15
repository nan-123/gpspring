package com.gupaoedu.vip.demo.mvc.action;

import com.gupaoedu.vip.demo.mvc.service.IDemoService;
import com.gupaoedu.vip.spring.annotation.Autowried;
import com.gupaoedu.vip.spring.annotation.Controller;
import com.gupaoedu.vip.spring.annotation.RequestMapping;
import com.gupaoedu.vip.spring.annotation.RequestParam;
import com.gupaoedu.vip.spring.formework.webmvc.GPModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/demo")
public class DemoAction {
    @Autowried
    private IDemoService demoService;

    @RequestMapping("query.json")
    public void query(HttpServletRequest req, HttpServletResponse resp,
                      @RequestParam("name")String name) throws Exception{

        String result = demoService.get(name);
        resp.getWriter().write(result);

    }

    @RequestMapping("/first.html")
    public GPModelAndView get(@RequestParam("teacher")String teacher){
        String result = demoService.get(teacher);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new GPModelAndView("first.html", model);
    }

}
