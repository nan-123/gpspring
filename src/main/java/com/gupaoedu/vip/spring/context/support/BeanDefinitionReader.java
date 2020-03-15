package com.gupaoedu.vip.spring.context.support;

import com.gupaoedu.vip.spring.beans.BeanDefinition;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// 用来对配置文件查找，读取，解析 == 定位
public class BeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registyBeanClasses = new ArrayList<String>();

    private  final  String SCAN_PACKGE = "scanPackage";

    public BeanDefinitionReader(String ... locations){

        // 在pring中是通过reader去查找和定位的
        // this.getClass().getClassLoader() ： 取到根目录开始的文件
        // this.getClass().getClassLoader().getResourceAsStream(xx); 搜索根目录开始的叫xx的文件
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            config.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (null != is){
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        doScanner(config.getProperty(SCAN_PACKGE));
    }

    public List<String> loadBeanDefinitions(){
        return this.registyBeanClasses;
    }

    //每注册一个className ,就返回一个BeanDefinition
    public BeanDefinition registerBean(String className){
        if (this.registyBeanClasses.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf(".")+1)));
            return beanDefinition;
        }
        return null;

    }

    public Properties getConfig(){
        return this.config;
    }

    private void doScanner(String packageName) {
        //
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()) {
            if (file.isDirectory()){
                doScanner(packageName + "." + file.getName());
            }else {
                registyBeanClasses.add(packageName + "." + file.getName().replace(".class", ""));
            }
        }

    }

    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        // 不能直接toString,char没有重写toString方法
        return String.valueOf(chars);
    }
}
