//package com.gupaoedu.vip.spring.servlet;
//
//
//import com.gupaoedu.vip.demo.mvc.action.DemoAction;
//import com.gupaoedu.vip.spring.annotation.Autowried;
//import com.gupaoedu.vip.spring.annotation.Controller;
//import com.gupaoedu.vip.spring.annotation.Service;
//
//import javax.servlet.ServletConfig;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.Field;
//import java.net.URL;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class DispathchServlet extends HttpServlet {
//
//
//    private Properties contextConfig = new Properties();
//
//    private Map<String, Object> beanMap = new ConcurrentHashMap<String, Object>();
//
//    private List<String> clsssNames = new ArrayList<String>();
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        this.doPost(req, resp);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//       System.out.println("--------调用dopost-------");
//    }
//
//
//    // 定位 1 找文件 找配置文件
//    // 加载 2 找类名 找配置文件里面配置了需要扫描的包下面的类，放到List<String> clsssNames 中
//    // 注册 3 找注解 这一步已经实例化了对象 在map里面 map<类名，对象> 类名从clsssNames中找到
//    // 赋值 4 这里的赋值不是给容器里面的对象赋值，而是给类中Autowried 注解的字段赋值（一般是引用其他的service）
//
//    @Override
//    public void init(ServletConfig config) throws ServletException {
//
//        // 开始初始化
//
//        // 定位
//
//        doLoadConfig(config.getInitParameter("contextConfigLocation"));
//
//        // 加载
//        // 扫描指定包放到集合里面准备结合注解解析
//        doScanner(contextConfig.getProperty("scanPackage"));
//
//        // 注册
//        // 就是把扫描包下面的指定对象初始化放到容器里面
//        doRegister();
//
//        // 自动依赖注入
//        // 根据不同注解在容器里面获取对象放到指定的字段（对象）
//        doAutowired();
//
//        DemoAction demoAction = (DemoAction)beanMap.get("demoAction");
//        demoAction.query(null,null, "lichunnan");
//
//        //如果是springmvc会多设计一个hadndleMapping
//        initHandleMapping();
//    }
//
//    private void initHandleMapping() {
//    }
//
//    private void doRegister() {
//
//        if (clsssNames.isEmpty()){
//            return;
//        }
//
//        for (String clsssName : clsssNames) {
//            try {
//                Class<?> clazz = Class.forName(clsssName);
//                //在spring中用策略模式
//                if (clazz.isAnnotationPresent(Controller.class)){
//                    String beanName = lowerFirstCase(clazz.getSimpleName());
//                    beanMap.put(beanName, clazz.newInstance());
//                }else if (clazz.isAnnotationPresent(Service.class)){
//                    Service service = clazz.getAnnotation(Service.class);
//                    // 默认用类名首字母注入
//
//                    //自定义beanName优先
//
//                    // 如果是接口，使用接口类型自动注入
//
//                    String beanName = service.value();
//                    if ("".equals(beanName.trim())){
//
//                        beanName = lowerFirstCase(clazz.getSimpleName());
//                    }
//
//                    Object instance = clazz.newInstance();
//
//                    beanMap.put(beanName, instance);
//
//                    Class<?>[] interfaces = clazz.getInterfaces();
//                    for (Class<?> i : interfaces) {
//                        beanMap.put(i.getName(), instance);
//                    }
//
//                }else {
//                    continue;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    private void doAutowired() {
//        if (beanMap.isEmpty()){return;}
//        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
//            Field[] fields = entry.getValue().getClass().getDeclaredFields();
//            for (Field field : fields) {
//                if (field.isAnnotationPresent(Autowried.class)){
//                    Autowried autowried = field.getAnnotation(Autowried.class);
//                    String beanName = autowried.value().trim();
//                    if ("".equals(beanName)){
//                        beanName = field.getType().getName();
//                    }
//                    field.setAccessible(true);
//
//                    try {
//                        field.set(entry.getValue(), beanMap.get(beanName));
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }else {
//                    continue;
//                }
//            }
//
//        }
//    }
//
//    private void doScanner(String packageName) {
//        //
//        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
//        File classDir = new File(url.getFile());
//
//        for (File file : classDir.listFiles()) {
//            if (file.isDirectory()){
//                doScanner(packageName + "." + file.getName());
//            }else {
//                clsssNames.add(packageName + "." + file.getName().replace(".class", ""));
//            }
//        }
//
//    }
//
//    private void doLoadConfig(String location) {
//        // 在pring中是通过reader去查找和定位的
//        // this.getClass().getClassLoader() ： 取到根目录开始的文件
//        // this.getClass().getClassLoader().getResourceAsStream(xx); 搜索根目录开始的叫xx的文件
//        InputStream is = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:",""));
//        try {
//            contextConfig.load(is);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            if (null != is){
//                try {
//                    is.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private String lowerFirstCase(String str){
//        char[] chars = str.toCharArray();
//        chars[0] += 32;
//        // 不能直接toString,char没有重写toString方法
//        return String.valueOf(chars);
//    }
//
//}
//
