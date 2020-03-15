package com.gupaoedu.vip.spring.formework.context;

import com.gupaoedu.vip.spring.formework.annotation.GPAutowried;
import com.gupaoedu.vip.spring.formework.annotation.GPController;
import com.gupaoedu.vip.spring.formework.annotation.GPService;
import com.gupaoedu.vip.spring.formework.beans.GPBeanDefinition;
import com.gupaoedu.vip.spring.formework.beans.GPBeanPostProcessor;
import com.gupaoedu.vip.spring.formework.beans.GPBeanWrapper;
import com.gupaoedu.vip.spring.formework.context.support.GPBeanDefinitionReader;
import com.gupaoedu.vip.spring.formework.core.GPBeanFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {

    private GPBeanDefinitionReader reader;

    private String[] configLocations;


    // 用来保证注册式单例
    private Map<String, Object> beanCacheMap = new HashMap<String, Object>();

    // 用来存储被代理的对象
    private Map<String, GPBeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, GPBeanWrapper>();


     public GPApplicationContext(String ... configLocations){
         this.configLocations = configLocations;
        this.refresh();
    }

    public void refresh(){

        // 定位

        // 这里做了获取配置文件和扫描包下面的类到集合中    // 获取类集合
        this.reader = new GPBeanDefinitionReader(configLocations);

        // 加载
        // 这里只是获取上面处理好的集合    // 同上
        List<String> beanDefinitions  = reader.loadBeanDefinitions();

        // 注册

        // 注册完其实对象还没初始化

        // 组装 beanDefinitions    map<factoryName, {className,lay-init, factory}>
        doRegisty(beanDefinitions);

        // 依赖注入（lazy-init = false）,要执行这里 调用getBean方法
        // 对象初始化 并 set到

        doAutowrited();

    }

    private void doAutowrited() {
        Set<Map.Entry<String, GPBeanDefinition>> entries = this.beanDefinitionMap.entrySet();
        for (Map.Entry<String, GPBeanDefinition> beanDefinitionEntry : entries) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()){
                getBean(beanName);
            }
        }

        Set<Map.Entry<String, GPBeanWrapper>> entries1 = this.beanWrapperMap.entrySet();
        for (Map.Entry<String, GPBeanWrapper> entry : entries1) {
            populateBean(entry.getKey(), entry.getValue().getWrappedInstance());
        }

    }

    public void populateBean(String beanName, Object instance){
        Class<?> clazz = instance.getClass();
        if (!(clazz.isAnnotationPresent(GPController.class) || clazz.isAnnotationPresent(GPService.class))){
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(GPAutowried.class)){
                continue;
            }

            GPAutowried autowried = field.getAnnotation(GPAutowried.class);
            String autowireName = autowried.value().trim();
            if ("".equals(autowireName)){
                autowireName = field.getType().getName();
            }

            field.setAccessible(true);

            try {
                field.set(instance, this.beanWrapperMap.get(autowireName).getWrappedInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void doRegisty(List<String> beanDefinitions) {

        try {
            for (String className : beanDefinitions) {
                Class<?> beanClass = Class.forName(className);

                // 如果是接口,跳过，不是说实现类有接口不管
                if (beanClass.isInterface()){
                    continue;
                }

                GPBeanDefinition beanDefinition = reader.registerBean(className);
                if (beanDefinition != null){
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);

                }

                // 获取类的所有接口
                // 这里就解析了一个实现类实现多个接口时的情况，这里注入的所有接口都是获取到对应的实现类
                // 所以疑问 ixxxxservice   xxxxservice 最后是从ioc中获取<isInterface, xxxxservice>
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    // 所以这里也能看出一个问题，一个接口对应多个实现类时会覆盖或者直接报错
                    // 但是可以通过指定名字来解决的
                    // 所以ioc里面保存的也不是实例，是需要实例的类的信息 ，名称，facroty。。。
                    this.beanDefinitionMap.put(i.getName(),beanDefinition);
                }


                // 1 beanName 三种情况
                // 默认是类名首字符小写
                // 自定义名
                //接口注入
            }

            // 容器初始化完毕
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //通过读取BeanDefinition 中的信息
    // 然后反射，创建实例返回
    //spring中会再封装成beanWrapper 不会直接放BeanDefinition（aop）
    public Object getBean(String beanName) {
        GPBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        try {
            // 生产通知事件
            GPBeanPostProcessor beanPostProcessor = new GPBeanPostProcessor();
            // 实例化
            Object instantion = instantionBean(beanDefinition);
            if (null == instantion){ return null; }
            // 初始化前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instantion, beanName);
            GPBeanWrapper beanWrapper = new GPBeanWrapper(instantion);
            beanWrapper.setBeanPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanName, beanWrapper);
            // 初始化后调用一次
            beanPostProcessor.postProcessAfterInitialization(instantion,beanName);

//            populateBean(beanName, instantion);
            return this.beanWrapperMap.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // 传一个beanDefinition 返回一个实例
    private Object instantionBean(GPBeanDefinition beanDefinition){
         Object instance = null;
        String className = beanDefinition.getBeanClassName();

        try {
            // 如果缓存器里面已经存在，直接赋值，没有才用反射创建实例
            if (this.beanCacheMap.containsKey(className)){
                instance = this.beanCacheMap.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.beanCacheMap.put(className, instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public String[] getBeanDefinitionNames(){

         return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);

    }

    public int getBeanDefinitionCount(){

         return this.beanDefinitionMap.size();
    }

    public Properties getconfig(){
         return this.reader.getConfig();
    }
}
