package com.spring.boot.logger;

import com.spring.boot.logger.application.json.ApplicationLoggerBean;
import com.spring.boot.logger.config.ApplicationFactoryAdapter;
import com.spring.boot.logger.general.GeneralLoggerBean;

import java.util.Map;

import static com.spring.boot.logger.ILoggerBean.APPLICATION_LOG;
import static com.spring.boot.logger.ILoggerBean.GENERAL_LOG;

public class LoggerBeanAdapter {

    public static ILoggerBean getBean(Map m, String eventLoggerName) throws Exception {
        if (LoggerNamesFactory.isApplicationLogger(eventLoggerName)) {
            return new ApplicationLoggerBean().create(m);
        }

        if (LoggerNamesFactory.isGeneralLogger(eventLoggerName)) {
            return new GeneralLoggerBean().create(m);
        }

        return ApplicationFactoryAdapter.getLoggerBean(m);
    }
}
