package com.spring.formework.webmvc.servlet;


import java.util.Map;

//封装的视图信息
public class MAModelAndView {
    private String viewName;
    private Map<String,?> model;
    public MAModelAndView(String viewName) { this.viewName = viewName; }
    public MAModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    /**
     * 得到map
     * @return
     */
    public Map<String, ?> getModel() {
        return model;
    }
}
