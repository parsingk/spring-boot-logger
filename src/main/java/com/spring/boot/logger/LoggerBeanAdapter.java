package com.spring.boot.logger;

import com.spring.boot.logger.application.json.SystemJsonLoggerBean;
import com.spring.boot.logger.config.ApplicationFactoryAdapter;
import com.spring.boot.logger.general.LoggerBean;

import java.util.Map;

import static com.spring.boot.logger.ILoggerBean.APPLICATION_LOG;
import static com.spring.boot.logger.ILoggerBean.GENERAL_LOG;

public class LoggerBeanAdapter {

    public static boolean isApplicationLog(Integer val) {
        return APPLICATION_LOG == val;
    }

    public static boolean isGeneralLog(Integer val) {
        return GENERAL_LOG == val;
    }

    public static boolean isSystemLog(Map json) {
        if (json == null) return false;
        return Boolean.parseBoolean(json.getOrDefault(ILoggerBean.IS_SYSTEM_LOG, false).toString());
    }

    public static ILoggerBean getBean(Map m) throws Exception {
        if (isSystemLog(m)) {
            return new SystemJsonLoggerBean().create(m);
        }

        Integer logType = Integer.parseInt(m.getOrDefault(ILoggerBean.LOG_TYPE, APPLICATION_LOG).toString());
        if (isGeneralLog(logType)) {
            return new LoggerBean().create(m);
        }

        return ApplicationFactoryAdapter.getLoggerBean(m);
    }
}
