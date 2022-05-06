package com.spring.boot.logger.application;

import com.spring.boot.logger.AbstractLoggerBean;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.application.json.JsonLoggerBean;
import com.spring.boot.logger.application.stackdriver.StackdriverLoggerBean;
import com.spring.boot.logger.config.IConfiguration;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ApplicationLoggerBeanFactory {

    static final ConcurrentMap<String, Class<? extends AbstractLoggerBean>> loggerBeanMap
            = new ConcurrentHashMap<>(){
        {
            put(IConfiguration.APPLICATION_LOGGING_TYPE_JSON, JsonLoggerBean.class);
            put(IConfiguration.APPLICATION_LOGGING_TYPE_STACKDRIVER, StackdriverLoggerBean.class);
        }
    };


    public static ILoggerBean getBean(JSONObject json) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<? extends AbstractLoggerBean> bean = loggerBeanMap.get(AbstractApplicationLogger.getApplicationLoggingType());

        return (ILoggerBean) bean.getDeclaredMethod("create", JSONObject.class).invoke(null, json);
    }
}
