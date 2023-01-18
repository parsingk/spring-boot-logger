package com.spring.boot.logger.general;

import com.google.gson.Gson;
import com.spring.boot.logger.AbstractLogger;
import com.spring.boot.logger.ILogDTO;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.aws.AwsKinesisDataProducer;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Arrays;

@Slf4j
public class GeneralLogger extends AbstractGeneralLogger {

    @Override
    public void log(ILogDTO payloadObj) {
        JSONObject json = new JSONObject();

        json.put(ILoggerBean.LOG_TYPE, String.valueOf(ILoggerBean.GENERAL_LOG));
        json.put(ILoggerBean.IS_CUSTOM_ERROR_LOG, true);
        try {
            String payload = new Gson().toJson(payloadObj);
            JSONParser parser = new JSONParser();
            JSONObject jsonpayload = (JSONObject) parser.parse(payload);

            json.put(ILoggerBean.SERVICE, AbstractLogger.getService());
            json.put(ILoggerBean.JSONPAYLOAD, jsonpayload);

            log.info(json.toJSONString());
        } catch (ParseException p) {
            json.put(ILoggerBean.MESSAGE, p.getMessage());
            json.put(ILoggerBean.STACKTRACE, Arrays.toString(p.getStackTrace()));
            json.put("reason", "[GeneralLogger] Log JSONParsing ERROR");

            log.error(json.toJSONString());
        } catch (Exception e) {
            json.put(ILoggerBean.MESSAGE, e.getMessage());
            json.put(ILoggerBean.STACKTRACE, Arrays.toString(e.getStackTrace()));

            log.error(json.toJSONString());
        } finally {
            if (AwsKinesisDataProducer.isConfigured()) {
                json.remove(ILoggerBean.IS_CUSTOM_ERROR_LOG);
                json.put(ILoggerBean.TIMESTAMP, this.formatTimestamp());
                AwsKinesisDataProducer.getInstance().putRecord(json);
            }
        }
    }
}
