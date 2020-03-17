package com.gupaoedu.vip.spring.formework.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

// 目标对象的一个方法要增强
// 由自己实现的业务逻辑去增强
// 配置文件的目的：告诉spring的那些类的那些方法需要增强，增强的内容是什么
// 对配置文件中所体现的内容进行封装
public class GPAopConfig {

    // 以目标对象的method作为key 把需要增强的内容作为值（打印日志）
    private Map<Method,GPAspect> points = new HashMap<Method, GPAspect>();

    public void put(Method target, Object aspect, Method[] points){

        this.points.put(target, new GPAspect(aspect, points));
    }

    public GPAspect get(Method method){
        return this.points.get(method);
    }


    public boolean contains(Method method){
        return this.points.containsKey(method);
    }

    // 需要代理的对象，需要走切面的方法
    // 对增强的代码进行封装
    public class GPAspect{

        private Object aspect; // 这个是logaspet 对象赋值的
        private Method[] points; // 这个是logaspet 里面的before和after方法

        public GPAspect(Object aspect, Method[] points) {
            this.aspect = aspect;
            this.points = points;
        }

        public Object getAspect() {
            return aspect;
        }

        public void setAspect(Object aspect) {
            this.aspect = aspect;
        }

        public Method[] getPoints() {
            return points;
        }

        public void setPoints(Method[] points) {
            this.points = points;
        }
    }
}
