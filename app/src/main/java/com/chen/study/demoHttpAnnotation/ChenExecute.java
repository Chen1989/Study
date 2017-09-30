package com.chen.study.demoHttpAnnotation;

import java.io.IOException;

/**
 * Created by PengChen on 2017/9/29.
 */

public interface ChenExecute {
    <T> T execute(ChenMethodContext context) throws IOException, ChenNetException;
}
