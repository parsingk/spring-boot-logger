package com.spring.boot.logger.config;

import com.spring.boot.logger.aws.AwsKinesisDataProducer;
import com.spring.boot.logger.aws.AwsKinesisDataStreamConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.spring.boot.logger.config.IConfiguration.*;

@Configuration
public class KinesisConfiguration {

    @Value("${logging.aws.kinesis.logType:null}")
    private String validLogType;
    @Value("${logging.aws.kinesis.producer.region:null}")
    private String regionName;
    @Value("${logging.aws.kinesis.producer.streamName:null}")
    private String streamName;
    @Value("${logging.aws.credentials.accessKey:null}")
    private String awsAccessKey;
    @Value("${logging.aws.credentials.secretKey:null}")
    private String awsSecret;

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = KINESIS_PRODUCER_PRODUCE, havingValue = VALUE_TRUE)
    public void kinesisDataStreamProducer() throws Exception {
        AwsKinesisDataStreamConfig config = AwsKinesisDataStreamConfig.getInstance(regionName, streamName, awsAccessKey, awsSecret);
        config.setValidLogType(validLogType);

        AwsKinesisDataProducer.configure(config);
    }
}
