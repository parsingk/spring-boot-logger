package com.spring.boot.logger;

import com.spring.boot.logger.application.ApplicationLogger;
import com.spring.boot.logger.application.SystemApplicationLogger;
import com.spring.boot.logger.general.GeneralLogger;

public class LoggerNamesFactory {

    private static String jsonLoggerName;

    private static final String generalLoggerName = GeneralLogger.class.getName();

    private static final String applicationLoggerName = ApplicationLogger.class.getName();

    private static final String systemApplicationLoggerName = SystemApplicationLogger.class.getName();

    public synchronized static void setApplicationJsonLoggerName(String loggerName) {
        if (jsonLoggerName == null) {
            jsonLoggerName = loggerName;
        }
    }

    protected static boolean isGeneralLogger(String eventLoggerName) {
        if (eventLoggerName.equals(generalLoggerName)) {
            return true;
        }

        return false;
    }

    protected static boolean isJsonApplicationLogger(String eventLoggerName) {
        if (eventLoggerName.equals(jsonLoggerName)) {
            return true;
        }

        return false;
    }

    protected static boolean isApplicationLogger(String eventLoggerName) {
        if (eventLoggerName.equals(applicationLoggerName)) {
            return true;
        }

        return false;
    }

    protected static boolean isSystemApplicationLogger(String eventLoggerName) {
        if (eventLoggerName.equals(systemApplicationLoggerName)) {
            return true;
        }

        return false;
    }
}
