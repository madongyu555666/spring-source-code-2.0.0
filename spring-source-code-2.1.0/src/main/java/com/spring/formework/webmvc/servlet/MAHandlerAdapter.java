package com.spring.formework.webmvc.servlet;

public class MAHandlerAdapter {
    public boolean supports(Object handler){ return (handler instanceof MAHandlerMapping);}



}
