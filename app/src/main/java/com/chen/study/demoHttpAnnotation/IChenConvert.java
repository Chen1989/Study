package com.chen.study.demoHttpAnnotation;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by PengChen on 2017/9/29.
 */

public interface IChenConvert {
    <T> T convert(InputStream data) throws IOException;
}
