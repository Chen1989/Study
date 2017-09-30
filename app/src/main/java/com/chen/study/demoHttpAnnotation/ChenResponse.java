package com.chen.study.demoHttpAnnotation;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by PengChen on 2017/9/29.
 */

public class ChenResponse {
    private final Map<String, List<String>> headerFields;
    private int code;
    private InputStream entity;
    private String message="ok";

    public ChenResponse(int code, InputStream entity, Map<String, List<String>> headerFields,String message) {
        this.code = code;
        this.entity = entity;
        this.headerFields = headerFields;
        this.message = message;
    }

    public ChenResponse(int code, InputStream entity, Map<String, List<String>> headerFields) {
        this.code = code;
        this.entity = entity;
        this.headerFields=headerFields;
    }

    public int getCode() {
        return code;
    }

    public InputStream getEntity() {
        return entity;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, List<String>> getHeaderFields() {
        return headerFields;
    }
}
