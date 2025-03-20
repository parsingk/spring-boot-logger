package com.spring.boot.logger.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.logger.ILoggerBean;
import com.spring.boot.logger.application.ApplicationLogger;
import com.spring.boot.logger.utils.InputValidator;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
public class AwsKinesisDataProducer {

    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final KinesisAsyncClient kinesisAsyncClient;
    private final String streamName;

    private final AwsKinesisLogType validLogType;
    private static AwsKinesisDataProducer producer = null;
    private final ApplicationLogger logger = new ApplicationLogger();

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
        this.validLogType = config.getValidLogType();
    }

    public void putRecord(Map<String, Object> sendObject) {
        int logType = Integer.parseInt(sendObject.getOrDefault(ILoggerBean.LOG_TYPE, -1).toString());

        if (!isValidLogType(logType)) {
            return;
        }

        byte[] bytes = this.toBytes(sendObject);

        if (InputValidator.isNull(bytes)) { return; }

        PutRecordRequest request = PutRecordRequest.builder()
                .partitionKey(this.getRandomPartitionKey())
                .streamName(streamName)
                .data(SdkBytes.fromByteArray(bytes))
                .build();

        kinesisAsyncClient.putRecord(request).handleAsync((putRecordResponse, throwable) -> {
            if (throwable != null) {
                logger.error(throwable.getMessage(), throwable);
            }

            return putRecordResponse;
        }).join();
    }

    public void putRecord(ILoggerBean sendObject) {
        int logType = sendObject.getLogtype();

        if (!isValidLogType(logType)) {
            return;
        }

        byte[] bytes = this.toBytes(sendObject);

        if (InputValidator.isNull(bytes)) { return; }

        PutRecordRequest request = PutRecordRequest.builder()
                .partitionKey(this.getRandomPartitionKey())
                .streamName(streamName)
                .data(SdkBytes.fromByteArray(bytes))
                .build();

        kinesisAsyncClient.putRecord(request).handleAsync((putRecordResponse, throwable) -> {
            if (throwable != null) {
                logger.error(throwable.getMessage(), throwable);
            }

            return putRecordResponse;
        }).join();
    }

    public void putRecords(List<ILoggerBean> sendObjects) {
        if (sendObjects == null || sendObjects.isEmpty()) return;

        List<PutRecordsRequestEntry> entries = new ArrayList<>();
        for (ILoggerBean sendObject : sendObjects) {
            int logType = sendObject.getLogtype();

            if (!isValidLogType(logType)) {
                continue;
            }

            byte[] bytes = this.toBytes(sendObject);

            if (InputValidator.isNull(bytes)) { continue; }

            PutRecordsRequestEntry entry = PutRecordsRequestEntry.builder()
                    .partitionKey(this.getRandomPartitionKey())
                    .data(SdkBytes.fromByteArray(bytes))
                    .build();

            entries.add(entry);
        }

        PutRecordsRequest request = PutRecordsRequest.builder()
                .streamName(streamName)
                .records(entries)
                .build();

        kinesisAsyncClient.putRecords(request).handleAsync((putRecordsResponse, throwable) -> {
            if (throwable != null) {
                logger.error(throwable.getMessage(), throwable);
            }

            return putRecordsResponse;
        }).join();
    }

//    public void putRecordAsync(JSONObject sendObject) {
//        int logType = Integer.parseInt(sendObject.getOrDefault(ILoggerBean.LOG_TYPE, -1).toString());
//
//        if (!isValidLogType(logType)) {
//            return;
//        }
//
//        byte[] bytes = this.toBytes(sendObject);
//
//        if (InputValidator.isNull(bytes)) { return; }
//
//        PutRecordRequest request = PutRecordRequest.builder()
//                .partitionKey(this.getRandomPartitionKey())
//                .streamName(streamName)
//                .data(SdkBytes.fromByteArray(bytes))
//                .build();
//
//        kinesisAsyncClient.putRecord(request).whenCompleteAsync((putRecordResponse, throwable) -> {
//            if (throwable != null) {
//                log.error("[Kinesis Producer] Exception while sending data to Kinesis.", throwable);
//            }
//            log.warn("sent message: " + putRecordResponse.sequenceNumber());
//        }).join();
//    }

//    private void retryPutRecord(PutRecordRequest request) {
//        kinesisAsyncClient.putRecord(request).whenCompleteAsync((putRecordResponse, throwable) -> {
//            if (throwable != null) {
//                this.retryPutRecord(request);
//            }
//        });
//    }

    private boolean isValidLogType(int logType) {
        if (validLogType == AwsKinesisLogType.BOTH) {
            return true;
        }

        return validLogType == AwsKinesisLogType.getTypeByType(logType);
    }
    private byte[] toBytes(Object o) {
        try {
            return objectMapper.writeValueAsBytes(o);
        } catch (IOException e) {
            return null;
        }
    }

    private String getRandomPartitionKey() {
        return UUID.randomUUID().toString().toLowerCase();
    }
}
