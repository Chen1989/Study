package com.chen.study.demoHttpAnnotation;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by PengChen on 2017/9/29.
 */

public abstract class IChenConvertBase implements IChenConvert {
    @Override
    public <R> R convert(InputStream data) throws IOException {
        if (data==null)return null;
        return convert(ChenIOUtils.inputToBytes(data));
    }

    public abstract <R> R convert(byte[] data) throws IOException;
}
