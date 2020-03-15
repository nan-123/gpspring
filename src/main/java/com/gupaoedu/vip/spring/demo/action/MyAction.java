//package com.gupaoedu.vip.spring.demo.action;
//
//import com.gupaoedu.vip.demo.mvc.service.IDemoService;
//import com.gupaoedu.vip.spring.annotation.Autowried;
//import com.gupaoedu.vip.spring.annotation.Controller;
//import com.gupaoedu.vip.spring.annotation.RequestMapping;
//import com.gupaoedu.vip.spring.annotation.RequestParam;
//import com.gupaoedu.vip.spring.formework.webmvc.GPModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.HashMap;
//import java.util.Map;
//
//@Controller
//public class MyAction {
//
//    @Autowried
//    private IDemoService demoService;
//
//    @RequestMapping("/index.html")
//    public void query(){}
//
//    @RequestMapping("/query.json")
//    public GPModelAndView query(HttpServletRequest request, HttpServletResponse response, String name){
//        String result = demoService.get(name);
//        System.out.println(result);
//
//        return null;
//    }
//
//    @RequestMapping("/first.html")
//    public GPModelAndView get(@RequestParam("teacher")String teacher){
//        String result = demoService.get(teacher);
//        Map<String, Object> model = new HashMap<String, Object>();
//        model.put("teacher", teacher);
//        model.put("data", result);
//        model.put("token", "123456");
//        return new GPModelAndView("first.html", model);
//    }
//}
