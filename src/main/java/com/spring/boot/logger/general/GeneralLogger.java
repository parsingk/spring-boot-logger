package com.spring.boot.logger.general;

import com.google.gson.Gson;
import com.spring.boot.logger.AbstractLogger;
import com.spring.boot.logger.ILogDTO;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.aws.AwsKinesisDataProducer;
import com.spring.boot.logger.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class GeneralLogger extends AbstractGeneralLogger {

    private GeneralLoggerBean printInfo(ILogDTO payloadObj) {
        GeneralLoggerBean bean = new GeneralLoggerBean();
        try {
            loggerBean(payloadObj, bean);

            log.info(this.gson.toJson(bean));
        } catch (Exception e) {
            bean.setMessage(e.getMessage());
            bean.setStacktrace(ExceptionUtils.getPrintStackTrace(e));

            log.error(this.gson.toJson(bean));
        }

        return bean;
    }

    private GeneralLoggerBean printWarn(ILogDTO payloadObj) {
        GeneralLoggerBean bean = new GeneralLoggerBean();
        try {
            loggerBean(payloadObj, bean);

            log.warn(this.gson.toJson(bean));
        } catch (Exception e) {
            bean.setMessage(e.getMessage());
            bean.setStacktrace(ExceptionUtils.getPrintStackTrace(e));

            log.error(this.gson.toJson(bean));
        }

        return bean;
    }

    private GeneralLoggerBean printError(ILogDTO payloadObj) {
        GeneralLoggerBean bean = new GeneralLoggerBean();
        try {
            loggerBean(payloadObj, bean);

            log.error(this.gson.toJson(bean));
        } catch (Exception e) {
            bean.setMessage(e.getMessage());
            bean.setStacktrace(Arrays.toString(e.getStackTrace()));

            log.error(this.gson.toJson(bean));
        }

        return bean;
    }

    private void loggerBean(ILogDTO payloadObj, GeneralLoggerBean bean) throws ParseException {
        if (includeHeaders) {
            bean.setHeaders(getHeaders(getRequest()));
        }

        String payload = this.gson.toJson(payloadObj);
        JSONParser parser = new JSONParser();
        JSONObject jsonpayload = (JSONObject) parser.parse(payload);

        bean.setJsonpayload(jsonpayload);
    }


    @Override
    public void log(ILogDTO payloadObj) {
        GeneralLoggerBean bean = this.printInfo(payloadObj);
        bean.addTimestamp(formatTimestamp());

        if (AwsKinesisDataProducer.isConfigured()) {
            AwsKinesisDataProducer.getInstance().putRecord(bean);
        }
    }

    @Override
    public void log(List<? extends ILogDTO> payloadList) {
        List<ILoggerBean> beans = new ArrayList<>();
        for (ILogDTO payload : payloadList) {
            GeneralLoggerBean bean = this.printInfo(payload);
            bean.addTimestamp(formatTimestamp());
            beans.add(bean);
        }

        if (AwsKinesisDataProducer.isConfigured()) {
            AwsKinesisDataProducer.getInstance().putRecords(beans);
        }
    }

    @Override
    public void warn(ILogDTO payloadObj) {
        GeneralLoggerBean bean = this.printWarn(payloadObj);
        bean.addTimestamp(formatTimestamp());

        if (AwsKinesisDataProducer.isConfigured()) {
            AwsKinesisDataProducer.getInstance().putRecord(bean);
        }
    }

    @Override
    public void error(ILogDTO payloadObj) {
        GeneralLoggerBean bean = this.printError(payloadObj);
        bean.addTimestamp(formatTimestamp());

        if (AwsKinesisDataProducer.isConfigured()) {
            AwsKinesisDataProducer.getInstance().putRecord(bean);
        }
    }
}
