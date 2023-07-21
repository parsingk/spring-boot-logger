package com.spring.boot.logger.application;

import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.application.json.JsonLoggerBean;
import org.json.simple.parser.ParseException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ApplicationLoggerBeanFactory implements IApplicationLoggerBeanFactory {

    Class<? extends AbstractApplicationLoggerBean> bean;

    boolean isDefault = true;

    @Override
    public void setBean(Class<? extends AbstractApplicationLoggerBean> bean) {
        this.bean = bean;
        this.isDefault = false;
    }

    public ILoggerBean getBean(Map m) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, ParseException {
        if (bean == null) {
            bean = JsonLoggerBean.class;
        }

        return bean.getDeclaredConstructor().newInstance().create(m);
    }
}
