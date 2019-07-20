package com.qw.study.marvenwebsocket.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : qiongwei.ren
 * @since : 2019/1/14.
 */
public class SerializeUtils {

    /**
     * use for log
     */
    private static final Logger log  = LoggerFactory.getLogger(SerializeUtils.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T readObject(String json, Class<T> clazz) throws RuntimeException {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("read json error: {}", json, e);
            throw new RuntimeException("json error", e);
        }
    }

    public static <T> List<T> readList(String json, Class<T> clazz) throws Exception {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new Exception("json错误", e);
        }
    }

    public static String toJson(Object input) {
        try {
            return objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            log.error("", e);
        }
        return null;
    }


    public static void main(String[] args) {
        String[] x = new String[]{};

    }


}