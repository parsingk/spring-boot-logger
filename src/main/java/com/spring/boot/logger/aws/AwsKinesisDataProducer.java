package com.spring.boot.logger.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.logger.utils.InputValidator;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
public class AwsKinesisDataProducer {

    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final KinesisAsyncClient kinesisAsyncClient;
    private final String streamName;

    private static AwsKinesisDataProducer producer = null;

    public static synchronized void configure(AwsKinesisDataStreamConfig config) {
        if (producer == null) {
            producer = new AwsKinesisDataProducer(config);
        }
    }

    public static boolean isConfigured() {
        return producer != null;
    }

    public static AwsKinesisDataProducer getInstance() {
        return producer;
    }

    private AwsKinesisDataProducer(AwsKinesisDataStreamConfig config) {
        this.streamName = config.getStreamName();
        this.kinesisAsyncClient = config.getKinesisAsyncClient();
    }

    public void putRecord(String key, Object sendObject) {
        byte[] bytes = this.toBytes(sendObject);

        if (InputValidator.isNull(bytes)) { return; }

        PutRecordRequest request = PutRecordRequest.builder()
                .partitionKey(key)
                .streamName(streamName)
                .data(SdkBytes.fromByteArray(bytes))
                .build();

        try {
            kinesisAsyncClient.putRecord(request).get();
        } catch (InterruptedException e) {
            log.info("[Kinesis Producer] Interrupted, assuming shutdown.");
        } catch (ExecutionException e) {
            log.error("[Kinesis Producer] Exception while sending data to Kinesis. Will try again next cycle.", e);
        }
    }

    private byte[] toBytes(Object o) {
        try {
            return objectMapper.writeValueAsBytes(o);
        } catch (IOException e) {
            return null;
        }
    }
}
