package com.chen.study.demoHttpAnnotation;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by PengChen on 2017/9/29.
 */

public class ChenIOUtils {
    public static byte[] inputToBytes(InputStream inputStream) throws IOException {
        BufferedInputStream bis=new BufferedInputStream(inputStream);
        byte[] buffer=new byte[1024*4];
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        int readInt=bis.read(buffer,0,buffer.length);
        while (readInt!=-1){
            bos.write(buffer,0,readInt);
            readInt=bis.read(buffer,0,buffer.length);
        }

        return bos.toByteArray();
    }
}
