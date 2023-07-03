package com.spring.boot.logger.config;

import com.spring.boot.logger.aws.AwsKinesisLogType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class KinesisConfigBuilder {

    private String region;
    private String stream;
    private AwsKinesisLogType logType = AwsKinesisLogType.BOTH;

    public KinesisConfigBuilder region(@NotBlank String region) {
        this.region = region;
        return this;
    }

    public KinesisConfigBuilder streamName(@NotBlank String streamName) {
        this.stream = streamName;
        return this;
    }

    public KinesisConfigBuilder logType(@NotNull AwsKinesisLogType logType) {
        this.logType = logType;
        return this;
    }

    public KinesisConfig build() throws Exception {
        this.validate();
        return new KinesisConfig(this.region, this.stream, this.logType);
    }

    private void validate() throws Exception {
        if (this.region == null || this.region.isBlank()) {
            throw new Exception("[Kinesis] Region must be set.");
        }

        if (this.stream == null || this.stream.isBlank()) {
            throw new Exception("[Kinesis] StreamName must be set.");
        }
    }
}
