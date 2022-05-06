package com.spring.boot.logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class AbstractLoggerBean implements ILoggerBean {

    public static ILoggerBean create(JSONObject json) throws ParseException {
        return null;
    }
}
