package com.example.kafkaconsumer.config;

import org.springframework.beans.factory.config.AbstractFactoryBean;

public class MyFactoryBean extends AbstractFactoryBean {

    private Class<Object> targetClass;

    public void setTargetClass(Class<Object> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    protected Object createInstance() throws Exception {
        return targetClass.getDeclaredConstructor().newInstance();
    }

    @Override
    public Class getObjectType() {
        return targetClass;
    }

}