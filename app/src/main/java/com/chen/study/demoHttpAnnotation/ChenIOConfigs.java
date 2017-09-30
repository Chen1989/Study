package com.chen.study.demoHttpAnnotation;

import java.io.IOException;

/**
 * Created by PengChen on 2017/9/29.
 */

public class ChenIOConfigs {
    private static ChenIOConfigs instance=new ChenIOConfigs();

    private ChenIOConfigs() {
    }

    private String encode="utf-8";
    private IChenConvert convert=new IChenConvertBase() {
        @Override
        public <R> R convert(byte[] data) throws IOException {
            if (data==null)return null;
            return (R) new String(data,encode);
        }
    };
    private ChenExecute service=new ChenHttpService();

    public static ChenIOConfigs getInstance() {
        return instance;
    }

    public void setConvert(IChenConvert convert) {
        this.convert = convert;
    }

    public void setService(ChenExecute service) {
        this.service = service;
    }

    public IChenConvert getConvert() {
        return convert;
    }

    public ChenExecute getService() {
        return service;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public String getEncode() {
        return encode;
    }
}
