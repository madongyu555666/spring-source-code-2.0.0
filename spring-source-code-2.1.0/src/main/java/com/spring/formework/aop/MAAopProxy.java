package com.spring.formework.aop;


/**
 * Created by Tom.
 */
//默认就用JDK 动态代理,代理的顶层接口
public interface MAAopProxy {
    Object getProxy();
    Object getProxy(ClassLoader classLoader);
}
