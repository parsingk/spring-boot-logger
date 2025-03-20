package com.spring.boot.logger.application;

import com.spring.boot.logger.AbstractLogger;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.config.LoggerConfig;
import org.json.simple.parser.ParseException;

import java.util.Map;

public abstract class AbstractApplicationLoggerBean implements ILoggerBean {

    private String service = LoggerConfig.getService();

    private Integer logtype = ILoggerBean.APPLICATION_LOG;

    public String getService() {
        return service;
    }

    public Integer getLogtype() {
        return logtype;
    }

    public abstract ILoggerBean create(Map json) throws ParseException;
}
