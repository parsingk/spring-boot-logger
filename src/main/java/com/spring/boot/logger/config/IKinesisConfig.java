package com.spring.boot.logger.config;

public interface IKinesisConfig {

    void config(String awsAccessKey, String awsSecret) throws Exception;
}
