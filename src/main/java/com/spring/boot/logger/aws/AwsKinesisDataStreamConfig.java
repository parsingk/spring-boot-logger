package com.spring.boot.logger.aws;

import com.spring.boot.logger.utils.InputValidator;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamResponse;
import software.amazon.kinesis.common.KinesisClientUtil;

@Slf4j
public class AwsKinesisDataStreamConfig {

    private String streamName;
    private KinesisAsyncClient kinesisAsyncClient;
    private static AwsKinesisDataStreamConfig config = null;

    private AwsKinesisLogType validLogType = AwsKinesisLogType.BOTH;
    private void credentials(Region region, String accessKey, String secret) {
        AwsCredentialsProvider provider = () -> AwsBasicCredentials.create(accessKey, secret);
        this.kinesisAsyncClient = KinesisClientUtil.createKinesisAsyncClient(
                KinesisAsyncClient.builder()
                        .credentialsProvider(provider)
                        .region(region)
        );
    }
    public static synchronized AwsKinesisDataStreamConfig getInstance(String region, String streamName, String accessKey, String secret) throws Exception {
        if (config == null) {
            config = new AwsKinesisDataStreamConfig(region, streamName, accessKey, secret);
        }

        return config;
    }
    private AwsKinesisDataStreamConfig(String region, String streamName, String accessKey, String secret) throws Exception {
        this.initialize(region, streamName, accessKey, secret);
    }

    public void setValidLogType(String validLogType) {
        AwsKinesisLogType type = AwsKinesisLogType.BOTH;
        if (validLogType != null && validLogType.trim().length() != 0) {
            type = AwsKinesisLogType.getTypeByLabel(validLogType);
        }

        this.validLogType = type;
    }

    public void setValidLogType(AwsKinesisLogType validLogType) {
        if (validLogType == null) {
            validLogType = AwsKinesisLogType.BOTH;
        }

        this.validLogType = validLogType;
    }

    public String getStreamName() {
        return streamName;
    }
    public KinesisAsyncClient getKinesisAsyncClient() {
        return kinesisAsyncClient;
    }

    public AwsKinesisLogType getValidLogType() {
        return validLogType;
    }
    private void initialize(String regionName, String streamName, String accessKey, String secret) throws Exception {
        Region region = Region.of(regionName);

        if(InputValidator.isNull(region)) {
            throw new Exception("[AWS Kinesis] Region is not valid");
        }

        if (InputValidator.isBlankWithNull(streamName)) {
            throw new Exception("[AWS Kinesis] StreamName must not to be null or blank");
        }

        this.credentials(region, accessKey, secret);
        this.validateStream(streamName);
        this.streamName = streamName;

        log.info("[AWS Kinesis] Producer configured.");
    }
    private void validateStream(String streamName) throws Exception {
        DescribeStreamRequest describeStreamRequest = DescribeStreamRequest.builder().streamName(streamName).build();
        DescribeStreamResponse describeStreamResponse = this.kinesisAsyncClient.describeStream(describeStreamRequest).get();

        if(!describeStreamResponse.streamDescription().streamStatus().toString().equals("ACTIVE")) {
            throw new Exception("Stream :" + streamName + " is not active. Please wait a few moments and try again.");
        }
    }
}

