package com.spring.boot.logger.application;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.spring.boot.logger.application.serializers.MultipartSerializer;

public class ObjectMapperBuilder {

    public static ObjectMapper build() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new MultipartSerializer());

        return new ObjectMapper()
                .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true)
                .registerModule(module);
    }
}
