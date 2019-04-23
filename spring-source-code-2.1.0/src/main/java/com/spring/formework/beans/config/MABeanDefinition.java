package com.spring.formework.beans.config;
//用来存储配置文件中的信息
//相当于保存在内存中的配置
public class MABeanDefinition {
    private String beanClassName;//class名 //com.spring.demo.action.MyAction
    private boolean lazyInit = false;//是否开启懒加载
    private String factoryBeanName;//myAction

    public String getBeanClassName() {
        return beanClassName;
    }
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
    public boolean isLazyInit() {
        return lazyInit;
    }
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    public String getFactoryBeanName() {
        return factoryBeanName;
    }
    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
