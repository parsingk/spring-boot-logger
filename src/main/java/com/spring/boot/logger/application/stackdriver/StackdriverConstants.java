package com.spring.boot.logger.application.stackdriver;

import org.springframework.util.Assert;

public interface StackdriverConstants {
    String REQUEST_ID = "requestId";
    String LEVEL_NAME = "levelName";
    String LEVEL_VALUE = "levelValue";
    String LOGGER_NAME = "loggerName";
    String MESSAGE = "message";
    String TYPE = "@type";
    String MDC_FIELD_TRACE_ID = "X-B3-TraceId";
    String MDC_FIELD_SPAN_ID = "X-B3-SpanId";
    String MDC_FIELD_SPAN_EXPORT = "X-Span-Export";
    String TRACE_ID_ATTRIBUTE = "logging.googleapis.com/trace";
    String SPAN_ID_ATTRIBUTE = "logging.googleapis.com/spanId";
    String SERVICE_CONTEXT_ATTRIBUTE = "serviceContext";
    String SEVERITY_ATTRIBUTE = "severity";
    String TIMESTAMP_SECONDS_ATTRIBUTE = "timestampSeconds";
    String TIMESTAMP_NANOS_ATTRIBUTE = "timestampNanos";
    String USER_ID = "userId";

    /**
     * Composes the full trace name as expected by the Google Developers Console log viewer, to
     * enable trace correlation with log entries.
     *
     * @param projectId the GCP project ID
     * @param traceId   the trace ID
     * @return the trace name in the "projects/[GCP_PROJECT_ID]/trace/[TRACE_ID]" format
     */
    static String composeFullTraceName(String projectId, String traceId) {
        Assert.notNull(projectId, "The project ID can't be null.");
        Assert.notNull(traceId, "The trace ID can't be null.");
        return "projects/" + projectId + "/traces/" + traceId;
    }
}
