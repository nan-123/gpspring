package com.gupaoedu.vip.spring.formework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

public class HandlerAdapter {

    private Map<String, Integer> paramMapping;

    public HandlerAdapter(Map<String, Integer> paramMapping){
        this.paramMapping = paramMapping;
    }
    /**
     *
     * @param req
     * @param resp
     * @param handler 为什么要传handler ,因为这包含了url，方法 ，controller
     * @return
     */
    public GPModelAndView handle(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handler) throws Exception{
        // 根据用户请求参数，跟方法中的参数进行动态匹配
        // resp 传进来只有一个目的：只是为了将其赋值给方法参数 resp不能new

        // 只有当用户传过来的ModelAndView 才会new一个默认的

        // 1 要准备好这个方法的参数列表  2 拿到自定义命名参数所在位置 3 构造参数列表 4从handle中获取 contrller methd 反射调用方法
        //例子：get（String name, int age） getParameterTypes() 得到 [java.lang.String, java.lang.int]
        // 如果没有参数，则返回[] 长度为0的数组
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();

        // 获取url中的 nam = xxx
        Map<String,String[]> reqParameter = req.getParameterMap();

        // 用来封装参数值，并且是根据之前paramMapping中排列好的参数位置存放
        Object[] paramValues = new Object[parameterTypes.length];
        for (Map.Entry<String, String[]> param : reqParameter.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s","");
            if (!this.paramMapping.containsKey(param.getKey())){continue;}
            Integer index = this.paramMapping.get(param.getKey());

            // 页面上传来的都是string 方法中的类型不一定是string
            paramValues[index] = caseStringValue(value, parameterTypes[index]);


        }

        if (this.paramMapping.containsKey(HttpServletRequest.class.getName())){
            Integer reqIndex = this.paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if (this.paramMapping.containsKey(HttpServletResponse.class.getName())){
            Integer respIndex = this.paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }


        Object controller = handler.getController();
        System.out.println(controller);
        // 方法名.invoke(方法所在对象，方法参数)
        Object result = handler.getMethod().invoke(handler.getController(), paramValues);
        if (result == null){return null;}
        boolean isModelAndView = handler.getMethod().getReturnType() == GPModelAndView.class;
        if (isModelAndView){
            return (GPModelAndView) result;
        }


        return null;
    }

    private Object caseStringValue(String value, Class<?> clazz){
        if (clazz == String.class){
            return value;
        }else if (clazz == Integer.class){
            return Integer.valueOf(value);
        }else if (clazz == int.class){
            return Integer.valueOf(value).intValue();
        }

        return null;
    }
}
