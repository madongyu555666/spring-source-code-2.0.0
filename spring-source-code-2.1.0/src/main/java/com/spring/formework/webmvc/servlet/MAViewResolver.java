package com.spring.formework.webmvc.servlet;


import java.io.File;
import java.util.Locale;

/**
 * 处理modelAndview
 */
//设计这个类的主要目的是：
//1、讲一个静态文件变为一个动态文件
//2、根据用户传送参数不同，产生不同的
//最终输出字符串，交给Response 输出
public class MAViewResolver {
    //解析html的
    private final String DEFAULT_TEMPLATE_SUFFX = ".html";
    private File templateRootDir;//模板路径

    //通过构造给MAViewResolver传值
    public MAViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }


    public MAView resolveViewName(String viewName, Locale locale) throws Exception{
        if(null == viewName || "".equals(viewName.trim())){return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new MAView(templateFile);
    }
}
