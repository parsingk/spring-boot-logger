package com.spring.boot.logger.config;

import com.spring.boot.logger.utils.InputValidator;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LoggerConfig {
    @Getter
    private static String service;
    private IKinesisConfig kinesisConfig;

    LoggerConfig() {
    }

    LoggerConfig setService(String service) {
        if (InputValidator.isBlankOrNull(LoggerConfig.service)) {
            LoggerConfig.service = service.toLowerCase();
        }

        return this;
    }

    public void setAwsKinesisConfig(@NotNull IKinesisConfig config) {
        this.kinesisConfig = config;
    }

    public void onAwsCredentials(@NotBlank String awsAccessKey,@NotBlank String awsSecret) throws Exception {
        this.kinesisConfig.config(awsAccessKey, awsSecret);
    }
}
