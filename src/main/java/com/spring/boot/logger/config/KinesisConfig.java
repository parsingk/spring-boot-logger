package com.spring.boot.logger.config;

import com.spring.boot.logger.aws.AwsKinesisDataProducer;
import com.spring.boot.logger.aws.AwsKinesisDataStreamConfig;
import com.spring.boot.logger.aws.AwsKinesisLogType;

class KinesisConfig implements IKinesisConfig {

    private final String region;
    private final String stream;
    private final AwsKinesisLogType logType;

    KinesisConfig(String region, String stream, AwsKinesisLogType logType) {
        this.region = region;
        this.stream = stream;
        this.logType = logType;
    }

    public void config(String awsAccessKey, String awsSecret) throws Exception {
        AwsKinesisDataStreamConfig config = AwsKinesisDataStreamConfig.getInstance(this.region, this.stream, awsAccessKey, awsSecret);
        config.setValidLogType(this.logType);

        AwsKinesisDataProducer.configure(config);
    }
}
