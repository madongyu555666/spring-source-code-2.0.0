package com.spring.formework.webmvc.servlet;


import java.io.File;
import java.util.Locale;

/**
 * 处理modelAndview
 */
public class MAViewResolver {
    //解析html的
    private final String DEFAULT_TEMPLATE_SUFFX = ".html";
    private File templateRootDir;//模板路径

    //通过构造给MAViewResolver传值
    public MAViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }


    /*public MAView resolveViewName(String viewName, Locale locale) throws Exception{
        if(null == viewName || "".equals(viewName.trim())){return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new GPView(templateFile);
    }*/
}
