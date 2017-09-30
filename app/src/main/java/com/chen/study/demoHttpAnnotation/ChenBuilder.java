package com.chen.study.demoHttpAnnotation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PengChen on 2017/9/29.
 */

@SuppressWarnings("unchecked")
public class ChenBuilder {
    private ChenExecute service;
    private static final Map<Class, Object> cache = new HashMap<>();
    private IChenConvert convert;

    public <T> T builder(Class<T> cls, final IChenConvert convert) {
        this.convert = convert;
        if (this.convert == null)
            this.convert = ChenIOConfigs.getInstance().getConvert();
        if (service == null)
            service = ChenIOConfigs.getInstance().getService();

        if (cache.containsKey(cls))
            return (T) cache.get(cls);

        Object obj = Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return service.execute(ChenMethodContext.parse(method, ChenBuilder.this.convert, args));
            }
        });

        cache.put(cls, obj);

        return (T) obj;
    }

    public <T> T builder(Class<T> cls, IChenConvert convert, ChenExecute execute) {
        this.service = execute;
        return builder(cls, convert);
    }

    public static <T> T builder(Class<T> cls) {
        return new ChenBuilder().defaultBuilder(cls);
    }

    private <T> T defaultBuilder(Class<T> cls) {
        return builder(cls, ChenIOConfigs.getInstance().getConvert());
    }

    public static <T> T builder(Class<T> cls, ChenExecute execute) {
        return new ChenBuilder().builder(cls, ChenIOConfigs.getInstance().getConvert(), execute);
    }

    public static <T> T builder(Class<T> cls, ChenExecute execute, IChenConvert convert) {
        return new ChenBuilder().builder(cls, convert, execute);
    }
}
