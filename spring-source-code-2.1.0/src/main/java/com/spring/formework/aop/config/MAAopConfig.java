package com.spring.formework.aop.config;


import lombok.Data;

//配置文件对象
@Data
public class MAAopConfig {
    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
