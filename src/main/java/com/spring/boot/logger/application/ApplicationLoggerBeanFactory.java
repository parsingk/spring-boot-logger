package com.spring.boot.logger.application;

import com.spring.boot.logger.AbstractLoggerBean;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.application.json.JsonLoggerBean;
import com.spring.boot.logger.config.IConfiguration;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ApplicationLoggerBeanFactory implements IApplicationLoggerBeanFactory {

    Class<? extends AbstractLoggerBean> bean;

    boolean isDefault = true;

    @Override
    public void setBean(Class<? extends AbstractLoggerBean> bean) {
        this.bean = bean;
        this.isDefault = false;
    }

    public ILoggerBean getBean(Map m) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, ParseException {
        if (bean == null) {
            bean = JsonLoggerBean.class;
        }

        AbstractLoggerBean loggerBean = (AbstractLoggerBean) bean.getDeclaredConstructor().newInstance().create(m);
        loggerBean.setService(AbstractApplicationLogger.getService().toLowerCase());

        return loggerBean;
    }
}
