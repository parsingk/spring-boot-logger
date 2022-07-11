package com.spring.boot.logger.config;

public interface IConfiguration {

    /**
     * In application.yml.
     *
     * logging:
     *    service: ${tag for your service}
     *    application:
     *      on: true
     *      type: json or stackdriver
     *    aws:
     *      credentials:
     *          accessKey: ${AWS_ACCESS_KEY}
     *          secretKey: ${AWS_SECRET_KEY}
     *      kinesis:
     *          producer:
     *              produce: true (default false)
     *              region: ${region}
     *              streamName: ${streamName}
     *    parameters:
     *      masking-keys: ${key}, ${key}
     *
     */

    String PREFIX = "logging";

    String APPLICATION_ON = "application.on-log";
    String VALUE_TRUE = "true";

    String KINESIS_PRODUCER_PRODUCE = "aws.kinesis.producer.produce";

    String APPLICATION_LOGGING_TYPE_JSON = "json";
    String APPLICATION_LOGGING_TYPE_STACKDRIVER = "stackdriver";

    String GENERAL_LOGGING_TYPE_DEFAULT = "default";

//    String PARAMETERS_MASKING_KEYS
}

