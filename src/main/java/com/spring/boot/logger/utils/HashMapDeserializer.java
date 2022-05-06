package com.spring.boot.logger.utils;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This code is not applied, but you can use this code for custom deserialize.
 */

public class HashMapDeserializer implements JsonDeserializer<HashMap<String, Object>> {

    @Override
    public HashMap<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        Entry<String, JsonElement> entry;
        Iterator<Entry<String, JsonElement>> iterator = obj.entrySet().iterator();
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        while (iterator.hasNext()) {
            entry = iterator.next();
            resultMap.put(entry.getKey(), ParseObjectFromElement.SINGLETON.apply(entry.getValue()));
        }

        return resultMap;
    }

    public enum ParseObjectFromElement implements Function<JsonElement, Object> {
        SINGLETON;
        @Override
        public Object apply(JsonElement input) {
            Object value = null;
            if (input == null || input.isJsonNull()) {
                value = null;
            } else if (input.isJsonPrimitive()) {
                JsonPrimitive primitive = input.getAsJsonPrimitive();
                if (primitive.isNumber()) {
                    value = primitive.getAsLong();
                } else if (primitive.isBoolean()) {
                    value = primitive.getAsBoolean();
                } else {
                    value = primitive.getAsString();
                }
            } else if (input.isJsonArray()) {
                value = Lists.newArrayList(Iterables.transform(input.getAsJsonArray(), this));
            } else if (input.isJsonObject()) {
                value = Maps. newLinkedHashMap(
                        Maps.transformValues(JsonObjectAsMap.INSTANCE.apply(input.getAsJsonObject()), this));
            }
            return value;
        }
    }

    public enum JsonObjectAsMap implements Function<JsonObject, Object> {
        INSTANCE;

        private final Field members;

        JsonObjectAsMap() {
            try {
                members = JsonObject.class.getDeclaredField("members");
                members.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new UnsupportedOperationException("cannot access gson internals", e);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map apply(JsonObject in) {
            try {
                return (Map) members.get(in);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new UnsupportedOperationException("cannot access gson internals", e);
            }
        }
    }

}
