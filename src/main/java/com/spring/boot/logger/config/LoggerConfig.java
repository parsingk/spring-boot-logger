package com.spring.boot.logger.config;

import com.spring.boot.logger.AbstractLoggerBean;
import com.spring.boot.logger.application.AbstractApplicationLogger;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class LoggerConfig {
    private IKinesisConfig kinesisConfig;

    public LoggerConfig(@NotBlank String service) {
        AbstractApplicationLogger.setService(service);
    }

    public String getService() {
        return AbstractApplicationLogger.getService();
    }

    /**
     * Set true, If you want to print General Log with Request Headers.
     * @param includeHeaders
     */
    public void setGeneralLoggerIncludeHeaders(boolean includeHeaders) {
        GeneralFactoryAdapter.setIncludeHeaders(includeHeaders);
    }

    /**
     * Set Masking keys for when you want to hide request parameter's value.
     *
     * @param maskingKeys
     */
    public void setMaskingKeys(@NotNull List<String> maskingKeys) {
        AbstractApplicationLogger.setMaskingKeys(maskingKeys);
    }

    /**
     * Set Unexpected ErrorCode
     * Default 500.
     * @param errorCode
     */
    public void setUnexpectedErroCode(int errorCode) {
        AbstractApplicationLogger.setUnexpectedErrorCode(errorCode);
    }

    /**
     * Set custom application logger. Logger is showing how to print in AOP
     * It must be extended AbstractApplicationLogger.
     * Default JsonLogger.
     * @param applicationLogger
     */
    public void setApplicationLogger(@NotNull AbstractApplicationLogger applicationLogger) {
        ApplicationFactoryAdapter.setLogger(applicationLogger);
    }

    /**
     * Set custom application logger bean. Bean is Object what you want to print.
     * It must be extended AbstractLoggerBean.
     * Default JsonLoggerBean.
     * @param bean
     */
    public void setApplicationLoggerBean(@NotNull Class<? extends AbstractLoggerBean> bean) {
        ApplicationFactoryAdapter.setBean(bean);
    }

    /**
     * Set AWS Kinesis Stream Config when you want to send log.
     * @param config
     */
    public void setAwsKinesisConfig(@NotNull IKinesisConfig config) {
        this.kinesisConfig = config;
    }

    /**
     * For AWS, Set Credential. This credential need about Kinesis Stream.
     * @param awsAccessKey
     * @param awsSecret
     * @throws Exception
     */
    public void onAwsCredentials(@NotBlank String awsAccessKey,@NotBlank String awsSecret) throws Exception {
        this.kinesisConfig.config(awsAccessKey, awsSecret);
    }
}
