package com.spring.boot.logger.application;

import com.spring.boot.logger.ILoggerBean;
import org.json.simple.parser.ParseException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface IApplicationLoggerBeanFactory {

    void setBean(Class<? extends AbstractApplicationLoggerBean> bean);

    ILoggerBean getBean(Map m) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, ParseException;
}
