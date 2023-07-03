package com.spring.boot.logger;

import org.json.simple.parser.ParseException;

import java.util.Map;

public abstract class AbstractLoggerBean implements ILoggerBean {

    private String service;

    private Integer logtype = ILoggerBean.APPLICATION_LOG;

    public void setService(String service) {
        this.service = service;
    }

    public void setLogtype(Integer logtype) {
        this.logtype = logtype;
    }

    public String getService() {
        return service;
    }

    public Integer getLogtype() {
        return logtype;
    }

    public abstract ILoggerBean create(Map json) throws ParseException;
}
