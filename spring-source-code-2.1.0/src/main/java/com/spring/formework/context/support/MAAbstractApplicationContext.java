package com.spring.formework.context.support;

/**
 * IOC 容器实现的顶层设计的抽象类
 */
public abstract class MAAbstractApplicationContext {
    //受保护，只提供给子类重写
    public void refresh() throws Exception {}
}
