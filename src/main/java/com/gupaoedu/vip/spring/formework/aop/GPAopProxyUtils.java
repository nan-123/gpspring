package com.gupaoedu.vip.spring.formework.aop;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class GPAopProxyUtils {

    public static Object getTargetObjet(Object proxy) throws Exception{
        // 先判断一下，传进来的对象是否代理对象
        if (!isAopProxy(proxy)){return proxy;}

        return getProxyTargetObject(proxy);
    }

    public static boolean isAopProxy(Object object){
        return Proxy.isProxyClass(object.getClass());
    }

    public static Object getProxyTargetObject(Object proxy)throws Exception{

        // h属性保存了原始对象
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        GPAopProxy aopProxy = (GPAopProxy) h.get(proxy);

        Field target = aopProxy.getClass().getDeclaredField("target");
        target.setAccessible(true);

        return target.get(aopProxy);
    }
}
