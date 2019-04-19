package com.spring.formework.context;

import com.spring.formework.annotation.MAAutowired;
import com.spring.formework.annotation.MAController;
import com.spring.formework.annotation.MAService;
import com.spring.formework.beans.MABeanWrapper;
import com.spring.formework.beans.config.MABeanDefinition;
import com.spring.formework.beans.config.MABeanPostProcessor;
import com.spring.formework.beans.support.MABeanDefinitionReader;
import com.spring.formework.beans.support.MADefaultListableBeanFactory;
import com.spring.formework.core.MABeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 按之前源码分析的套路，IOC、DI、MVC、AOP
 */
public class MAApplicationContext extends MADefaultListableBeanFactory implements MABeanFactory {

    private String [] configLoactions;
    private MABeanDefinitionReader reader;
    //单例的IOC容器缓存
    private Map<String,Object> singletonObjects = new ConcurrentHashMap<String, Object>();
    //通用的IOC容器
    private Map<String,MABeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, MABeanWrapper>();

    public MAApplicationContext(String... configLoactions) {
        this.configLoactions=configLoactions;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //依赖注入，从这里开始，通过读取BeanDefinition中的信息
    //然后，通过反射机制创建一个实例并返回
    //Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    //1、保留原来的OOP关系
    //2、我需要对它进行扩展，增强（为了以后AOP打基础）
    @Override
    public Object getBean(String beanName) throws Exception {
       MABeanDefinition mABeanDefinition=this.beanDefinitionMap.get(beanName);
       Object instance=null;
      //这个逻辑还不严谨，自己可以去参考Spring源码
        //工厂模式 + 策略模式


        //生成通知事件
        MABeanPostProcessor postProcessor = new MABeanPostProcessor();
        //在实例初始化以前调用一次
        postProcessor.postProcessBeforeInitialization(instance,beanName);

        //实例化
        instance = instantiateBean(beanName,mABeanDefinition);
        //3、把这个对象封装到BeanWrapper中
        MABeanWrapper mABeanWrapper=new MABeanWrapper(instance);

        //4、把BeanWrapper存到IOC容器里面
        //class A{ B b;}
//        //class B{ A a;}
//        //先有鸡还是先有蛋的问题，一个方法是搞不定的，要分两次
        //2、拿到BeanWraoper之后，把BeanWrapper保存到IOC容器中去
        this.factoryBeanInstanceCache.put(beanName,mABeanWrapper);
        //在实例初始化以前调用一次
        postProcessor.postProcessAfterInitialization(instance,beanName);
        //3、注入
        populateBean(beanName,new MABeanDefinition(),mABeanWrapper);
        //返回实例
        return  this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }


    /**
     * DI注入
     * @param beanName
     * @param maBeanDefinition
     * @param mABeanWrapper
     */
    private void populateBean(String beanName, MABeanDefinition maBeanDefinition, MABeanWrapper mABeanWrapper) {
        Object instance =  mABeanWrapper.getWrappedInstance();

        Class<?> clazz = mABeanWrapper.getWrappedClass();
        //判断只有加了注解的类，才执行依赖注入
        if(!(clazz.isAnnotationPresent(MAController.class) || clazz.isAnnotationPresent(MAService.class))){
            return;
        }
        //获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //是不是注入注解
            if(!field.isAnnotationPresent(MAAutowired.class)){ continue;}
              MAAutowired mAAutowired=field.getAnnotation(MAAutowired.class);
              //得到注入的名字
              String autowiredBeanName= mAAutowired.value().trim();
              if("".equals(autowiredBeanName)){
                  autowiredBeanName=field.getType().getName();//如果没有填写名字，直接去类型的名
              }
            //强制访问
            field.setAccessible(true);
            //为什么会为NULL，先留个坑
            if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){ continue; }
            //调用jdk的方法注入
            try {                //第一个参数是自己实例化的对象，第二个是从缓存取得对象
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * //传一个BeanDefinition，就返回一个实例Bean
     * @param beanName
     * @param mABeanDefinition
     * @return
     */
    private Object instantiateBean(String beanName, MABeanDefinition mABeanDefinition) {
        //1、拿到要实例化的对象的类名
        String className = mABeanDefinition.getBeanClassName();
        //2、反射实例化，得到一个对象
        Object instance = null;
        try{
            //假设默认就是单例,细节暂且不考虑，先把主线拉通，//因为根据Class 才能确定一个类是否有实例
            if(this.singletonObjects.containsKey(className)){
                   instance=this.singletonObjects.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                //存两个名字
                this.singletonObjects.put(className,instance);
                this.singletonObjects.put(mABeanDefinition.getFactoryBeanName(),instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return instance;
    }


    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }


    @Override
    public void refresh() throws Exception {
        //1.定位配置文件
        reader=new MABeanDefinitionReader(this.configLoactions);
        //2.加载配置文件，扫描相关的类，把他们封装成BeanDefintion
        List<MABeanDefinition> beanDefinitions=reader.loadBeanDefinitions();
        //3.注册，把配置文件信息放到容器里面（伪IOC容器初始化）
        doRegisterBeanDefinition(beanDefinitions);
        //4.把不是延时加载的类，有提前初始化
        doAutowrited();
    }


    //加载饥饿单例,//只处理非延时加载的情况
    private void doAutowrited() {
        for (Map.Entry<String, MABeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //注册
    private void doRegisterBeanDefinition(List<MABeanDefinition> beanDefinitions) throws Exception{
        for (MABeanDefinition beanDefinition : beanDefinitions) {
            if(super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
        //到这里为止，容器初始化完毕
    }


    //提供对外的方法
    //把key转成数组
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new  String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }

}
