package com.spring.boot.logger;

import com.spring.boot.logger.application.ApplicationLoggerBeanFactory;
import com.spring.boot.logger.general.LoggerBean;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;

public class LoggerBeanAdapter {

    public static boolean isApplicationLog(Integer val) {
        return ILoggerBean.APPLICATION_LOG == val;
    }

    public static boolean isApplicationLog(String val) {
        if (val == null) {
            return false;
        }

        return ILoggerBean.APPLICATION_LOG == Integer.parseInt(val);
    }

    public static ILoggerBean getBean(JSONObject json) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Integer logType = Integer.parseInt(json.get(ILoggerBean.LOG_TYPE).toString());
        if(isApplicationLog(logType)) {
            return ApplicationLoggerBeanFactory.getBean(json);
        }

        return LoggerBean.create(json);
    }
}
