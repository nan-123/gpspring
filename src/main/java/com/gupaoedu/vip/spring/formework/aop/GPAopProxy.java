package com.gupaoedu.vip.spring.formework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//默认用jdk动态代理
// jdk代理实现步骤
// 1: 一个接口：接口1 2：一个实现接口1的实现类
// 3：创建一个代理类，实现 InvocationHandler 接口，重新invoke（）方法
// 这里代理的都是service ，都会有接口
// 为什么要被代理类要有接口？
// 因为：public final class $Proxy0 extends Proxy implements tagService
// 生成的代理对象天然会继承一个父类Proxy 所以就不能再继承任何类，但代理类又要跟被代理类产生关联，只能通过实现被代理类的接口
public class GPAopProxy implements InvocationHandler {

    private GPAopConfig config;

    private Object target;


    // 原对象调用这个接口获取代理对象
    public Object getProxy(Object instance){
        this.target = instance;
        Class<?> clazz = instance.getClass();
        //newProxyInstance，方法有三个参数：
        //loader: 用哪个类加载器去加载代理对象
        //interfaces:动态代理类需要实现的接口
        //h:动态代理方法在执行时，会调用h里面的invoke方法去执行 ,也就是本来重新的invoke方法
        return Proxy.newProxyInstance(clazz.getClassLoader(),clazz.getInterfaces(), this);
    }

    public void setConfig(GPAopConfig config) {
        this.config = config;
    }

    // 所有被spring管理的对象方法调用都会在这里，会判断是否需要走aop
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Method m = this.target.getClass().getMethod(method.getName(), method.getParameterTypes());
        // 在原始方法调用前
        if (config.contains(m)){
            GPAopConfig.GPAspect aspect = config.get(m);
            // 因为是简化的，before方法没有参数
            aspect.getPoints()[0].invoke(aspect.getAspect());
        }

        // 这里才是真正调用方法
        Object obj = method.invoke(this.target, args);

        if (config.contains(m)){
            GPAopConfig.GPAspect aspect = config.get(m);
            aspect.getPoints()[1].invoke(aspect.getAspect());
        }
        return obj;
    }
}
