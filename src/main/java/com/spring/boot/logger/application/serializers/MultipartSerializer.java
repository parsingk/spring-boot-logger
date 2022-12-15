package com.spring.boot.logger.application.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MultipartSerializer extends JsonSerializer<MultipartFile> {

    @Override
    public void serialize(MultipartFile value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject(value.getName());
        gen.writeStringField("fileName", value.getOriginalFilename());
        gen.writeNumberField("fileSize", value.getSize());
        gen.writeStringField("contentType", value.getContentType());
        gen.writeEndObject();
    }

    @Override
    public Class<MultipartFile> handledType() {
        return MultipartFile.class;
    }
}
