package com.spring.formework.beans.support;

import com.spring.formework.beans.config.MABeanDefinition;
import com.spring.formework.context.support.MAAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MADefaultListableBeanFactory extends MAAbstractApplicationContext {
    //存储注册信息的BeanDefinition
    protected final Map<String, MABeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String,
                MABeanDefinition>();
}
