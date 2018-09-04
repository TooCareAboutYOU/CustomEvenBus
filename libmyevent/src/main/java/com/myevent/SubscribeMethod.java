package com.myevent;

import java.lang.reflect.Method;

/**
 * Created by zs on 2017/12/28.
 */

public class SubscribeMethod {
    private Method mMethod;
    private ThreadMode mThreadMode;
    private Class<?> eventType;

    public SubscribeMethod() {}

    public SubscribeMethod(Method method, ThreadMode threadMode, Class<?> eventType) {
        mMethod = method;
        mThreadMode = threadMode;
        this.eventType = eventType;
    }



    public Method getMethod() {
        return mMethod;
    }

    public void setMethod(Method method) {
        mMethod = method;
    }

    public ThreadMode getThreadMode() {
        return mThreadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        mThreadMode = threadMode;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }
}
