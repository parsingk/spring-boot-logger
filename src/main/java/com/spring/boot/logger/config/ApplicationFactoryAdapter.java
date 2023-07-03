package com.spring.boot.logger.config;

import com.spring.boot.logger.AbstractLoggerBean;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.application.*;
import org.json.simple.parser.ParseException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ApplicationFactoryAdapter {

    private static final IApplicationLoggerFactory loggerFactory = new ApplicationLoggerFactory();

    private static final IApplicationLoggerBeanFactory loggerBeanFactory = new ApplicationLoggerBeanFactory();



    /**
     * Setting Logger Instance.
     *
     * @param logger
     */
    static void setLogger(AbstractApplicationLogger logger) {
        loggerFactory.setLogger(logger);
    }

    /**
     * Setting Logger Bean Class.
     *
     * @param bean
     */
    static void setBean(Class<? extends AbstractLoggerBean> bean) {
        loggerBeanFactory.setBean(bean);
    }

    public static IApplicationLogger getLogger() {
        return loggerFactory.getLogger();
    }

    public static ILoggerBean getLoggerBean(Map m) throws ParseException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        return loggerBeanFactory.getBean(m);
    }
}
