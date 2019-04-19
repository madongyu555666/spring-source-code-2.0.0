package com.spring.formework.core;


/**
 * 单例工厂的顶层设计
 */
public interface MABeanFactory {

    /**
     * 根据beanName 从IOC 容器中获得一个实例Bean
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;


    /**
     * 根据Class 从IOC 容器中获得一个实例Bean
     * @param beanClass
     * @return
     * @throws Exception
     */
    public Object getBean(Class<?> beanClass) throws Exception;
}
