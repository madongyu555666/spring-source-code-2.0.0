package com.spring.formework.beans;

public class MABeanWrapper {
    private Object wrappedInstance;//MABeanWrapper的实例
    private Class<?> wrappedClass;//wrapped的class名

    public MABeanWrapper(Object wrappedInstance){
        this.wrappedInstance = wrappedInstance;
    }
    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    // 返回代理以后的Class
    // 可能会是这个$Proxy0代理的类
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }
}
