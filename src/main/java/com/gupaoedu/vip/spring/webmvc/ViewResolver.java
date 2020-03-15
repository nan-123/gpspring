package com.gupaoedu.vip.spring.webmvc;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 设计这个类的目的
// 1：将一个静态文件变为一个动态文件
// 2根据用户传的参数不同，产生不同的结果
// 最终输出字符串，通过response输出
public class ViewResolver {

    private String viewName;
    private File templateFile;
    public ViewResolver(String viewName, File templateFile) {
        this.templateFile = templateFile;
        this.viewName = viewName;
    }

    public String viewResolver(GPModelAndView mv)throws Exception{
        StringBuffer sb = new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(this.templateFile,"r");
        String line = null;
        while (null != (line = ra.readLine())){
            Matcher m = matcher(line);
            while (m.find()){
                for(int i = 0; i < m.groupCount(); i++){
                    String paramName = m.group(i);
                    Object paramValue = mv.getModel().get(paramName.replace("@{","").replace("}",""));
                    if (null == paramValue){continue;}
                    line = line.replace( paramName, paramValue.toString());
                }
            }
            sb.append(line);
        }
        return sb.toString();
    }

    private Matcher matcher(String str){
        Pattern pattern = Pattern.compile("@\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public File getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(File templateFile) {
        this.templateFile = templateFile;
    }
}
