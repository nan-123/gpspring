package com.gupaoedu.vip.spring.formework.webmvc.servlet;

import com.gupaoedu.vip.spring.formework.annotation.GPController;
import com.gupaoedu.vip.spring.formework.annotation.GPRequestMapping;
import com.gupaoedu.vip.spring.formework.annotation.GPRequestParam;
import com.gupaoedu.vip.spring.formework.context.GPApplicationContext;
import com.gupaoedu.vip.spring.formework.webmvc.GPModelAndView;
import com.gupaoedu.vip.spring.formework.webmvc.HandlerAdapter;
import com.gupaoedu.vip.spring.formework.webmvc.HandlerMapping;
import com.gupaoedu.vip.spring.formework.webmvc.ViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatchcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";

//    private Map<String, HandlerMapping> handlerMapping = new HashMap<String, HandlerMapping>();


    // 思考怎么给这个容器赋值 mvc的核心设计
    private List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();

    private Map<HandlerMapping,HandlerAdapter> handlerAdapters = new HashMap<HandlerMapping, HandlerAdapter>();

    private List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]","").replaceAll("\\s","\r\n"));
        }

    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
            HandlerMapping handler =  getHandler(req);

            if (handler == null){
                resp.getWriter().write("<font size='25' color='red'> 404 NOT found @li.cn </font>");
            }

            // 因为handleradpter 的key是hadlermapping
            HandlerAdapter ha = getHandlerAdapter(handler);

            // 调用方法
        // 分；两块，1：排参数值，2：调用返回一个GPModelAndView
            GPModelAndView mv =  ha.handle(req,resp,handler);

            // 把GPModelAndView里面的内容替代 后 重新write输出
            processDispatchResult(resp, mv);

    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        // 把ioc初始化了
        GPApplicationContext context = new GPApplicationContext(config.getInitParameter(LOCATION));

        //
        initStrategies(context);

    }

    private void processDispatchResult(HttpServletResponse resp, GPModelAndView mv) throws Exception{

        if (null == mv){return;}
        if (this.viewResolvers.isEmpty()){return;}
        for (ViewResolver viewResolver : viewResolvers) {

            if (!mv.getViewName().equals(viewResolver.getViewName())){
                continue;
            }
            String out = viewResolver.viewResolver(mv);

            if (out != null){
                resp.getWriter().write(out);
                break;
            }
        }

        // 调用resolveViewName
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()){return null;}
        return this.handlerAdapters.get(handler);

    }

    private HandlerMapping getHandler(HttpServletRequest req) {

        if (this.handlerMappings.isEmpty()){return null;}

        String url = req.getRequestURI();
//        比如你现在的URL是192.1.1.1:8080/my/index.jsp
//        tomcat配置的当前项目访问地址是192.1.1.1:8080/my
//        request.getContextPath（）得到的就是192.1.1.1:8080/my
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+", "/");

        for (HandlerMapping handlerMapping : handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()){continue;}
            return handlerMapping;
        }

        return null;
    }



    protected void initStrategies(GPApplicationContext context) {
        initMultipartResolver(context);
        initLocaleResolver(context);
        initThemeResolver(context);
        // 需要实现
        // 用来保存controller 中配置的requsttmapping 和method 的一个对应关系
        initHandlerMappings(context);
        // 需要实现
        // 用来匹配method参数，包含转换，赋值
        initHandlerAdapters(context);
        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context);
        // 需要实现
        // 实现动态模板解析
        initViewResolvers(context);
        initFlashMapManager(context);
    }

    private void initFlashMapManager(GPApplicationContext context) {}


    // 需要实现
    // 实现动态模板解析
    private void initViewResolvers(GPApplicationContext context) {

        // 猜测应该还有一个集合，保存视图和url等信息，使得视图跟url一一对应

        // 解决一个页面名字和模板文件关联问题

        String templateRoot = context.getconfig().getProperty("templateRoot");

        String path = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(path);

        for (File template : templateRootDir.listFiles()) {

            this.viewResolvers.add(new ViewResolver(template.getName(),template));
        }

    }



    private void initRequestToViewNameTranslator(GPApplicationContext context) {}

    private void initHandlerExceptionResolvers(GPApplicationContext context) {}



    // 需要实现
    // 用来匹配method参数，包含转换，赋值
    private void initHandlerAdapters(GPApplicationContext context) {

        //猜测这里是给handlerAdapters 赋值的，一个方法一个对象，主要是为了获取方法的参数用户转换赋值

        // 要处理命名参数和非命名参数

         // 记录参数位置

        for (HandlerMapping handlerMapping : handlerMappings) {
            Map<String, Integer> paramMapping = new HashMap<String, Integer>();

            // 这个二维数组里面 参数名-类型 是一个元素
            // 处理命名位置

//            首先举个例子:
//            @RedisScan
//            public void save(@RedisSave()int id,@RedisSave()String name){
//
//            }
//
//            第一个参数下表为0,第二个为1
//
//            也就是说:annos[0][0] = RedisSave
//            annos[1][0] = RedisSave
            Annotation[][] parameterAnnotations = handlerMapping.getMethod().getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length ; i ++) {
                for (Annotation a : parameterAnnotations[i]) {

                    if (a instanceof GPRequestParam){
                        String paramName = ((GPRequestParam) a).value();

                        if (!"".equals(paramName.trim())){
                            paramMapping.put(paramName, i);
                        }
                    }



                }
            }

            // 处理非命名参数，只处理http
            // getMethod().getParameterTypes() 返回方法的所有参数类型
            //例子：get（String name, int age） getParameterTypes() 得到 [java.lang.String, java.lang.int]
            // 如果没有参数，则返回[] 长度为0的数组
            Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();
            for(int i = 0; i < parameterTypes.length; i ++){
                Class<?> type = parameterTypes[i];
                if (type == HttpServletRequest.class || type == HttpServletResponse.class){
                    paramMapping.put(type.getName(), i);
                }
            }

            this.handlerAdapters.put(handlerMapping,new HandlerAdapter(paramMapping));


        }


    }



    // 需要实现
    // 用来保存controller 中配置的requsttmapping 和method 的一个对应关系
    private void initHandlerMappings(GPApplicationContext context) {
        // 猜测这里是给handlerMappings赋值的，一个方法一个对象


        // 先从容器中获取所有实例
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames) {
            Object instance = context.getBean(beanName);
            Class<?> clazz = instance.getClass();
            // 过滤没有controller 注解的
            if (!clazz.isAnnotationPresent(GPController.class)){continue;}

            String baseUrl = "";

            if (clazz.isAnnotationPresent(GPRequestMapping.class)){
                GPRequestMapping GPRequestMapping = clazz.getAnnotation(GPRequestMapping.class);
                baseUrl = GPRequestMapping.value();
            }

            // 扫描方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(GPRequestMapping.class)){continue;}

                GPRequestMapping GPRequestMapping = method.getAnnotation(GPRequestMapping.class);
                String regex = (baseUrl + GPRequestMapping.value().replaceAll("/+","/"));

                // Pattern.compile(regex); 从字符串中搜索符合的字符串
                Pattern pattern = Pattern.compile(regex);

                this.handlerMappings.add(new HandlerMapping(pattern,instance,method));

                System.out.println("Mapping" + regex + "," + method);
            }
        }


    }


    private void initThemeResolver(GPApplicationContext context) {}

    private void initLocaleResolver(GPApplicationContext context) {}

    private void initMultipartResolver(GPApplicationContext context) {}

}
