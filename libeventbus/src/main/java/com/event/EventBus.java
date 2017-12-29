package com.event;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zs on 2017/12/28.
 */

public class EventBus {

    private Map<Object,List<SubscribeMethod>> cacheMap;
    private static EventBus instance;
    private Handler mHandler;
    private ExecutorService mExecutorService;
    public EventBus(){
        cacheMap=new HashMap<>();
        mHandler=new Handler(Looper.getMainLooper());
        mExecutorService= Executors.newCachedThreadPool();
    }
    public static synchronized EventBus getDefault(){
        if(instance==null){
            synchronized(EventBus.class){
                if(instance==null){
                    instance=new EventBus();
                }
            }
        }
        return instance;
    }

    /**
     * 注册
     * @param obj
     */
    public void register(Object obj){
        List<SubscribeMethod> list=cacheMap.get(obj);
        if (list==null) {
            List<SubscribeMethod> methods=findSubscribeMethod(obj);
            cacheMap.put(obj,methods);
        }
    }

    /**
     * 查询事务
     * @param obj
     * @return
     */
    private List<SubscribeMethod> findSubscribeMethod(Object obj) {
        /**
         * 线程安全
         *
         */
        List<SubscribeMethod> list=new CopyOnWriteArrayList<SubscribeMethod>();
        Class<?> clazz=obj.getClass();
        Method[] methods=clazz.getDeclaredMethods();
        //循环查找父类 接收方法
        while (clazz != null) {
            String name=clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                break;
            }
            for (Method method : methods){
                //发生线程
                Subscribe subscribe=method.getAnnotation(Subscribe.class);
                if (subscribe == null){
                    continue;
                }
                //拿到方法的参数数据
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length != 1) {
                    //使用者--》创造者
                    throw new RuntimeException("eventbus must be one parameter");
                }

                Class<?> paramClass=parameters[0];
                ThreadMode threadMode=subscribe.value();
                SubscribeMethod subscribeMethod=new SubscribeMethod(method,threadMode,paramClass);
                //把
                list.add(subscribeMethod);

            }
            clazz=clazz.getSuperclass();
        }
        return list;
    }


    public void unregister(Object obj){
        cacheMap.remove(obj);
    }

    /**
     *
     * @param obj
     */
    public void post(final Object obj){
        Set<Object> setObj=cacheMap.keySet();
        Iterator iterator=setObj.iterator();
        while (iterator.hasNext()) {
            //拿到事务
            final Object activity=iterator.next();
            //拿到事务集合
            List<SubscribeMethod> list=cacheMap.get(activity);
            for (final SubscribeMethod method : list) {
                if (method.getEventType().isAssignableFrom(obj.getClass())) {
                    //判断当前接受方法是在那个线程
                    switch (method.getThreadMode()){
                        case PostThread:{
                            invoke(activity,method,obj);
                            break;
                        }
                        case MainThread:{
                            //判断发送线程是处在哪个线程，  发送线程就是在主线程  需要1  不需要2
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                invoke(activity,method,obj);
                            }else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(activity,method,obj);
                                    }
                                });
                            }
                            break;
                        }
                        case BackgroundThread:{  // 指定接收方法发生在子线程
                            //判断发送线程是处在哪个线程，  发送线程就是在子线程  需要1  不需要2
                            if (Looper.myLooper() != Looper.getMainLooper()){
                                //发生在子线程
                                invoke(activity,method,obj);
                            }else {
                                mExecutorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(activity,method,obj);
                                    }
                                });
                            }
                            break;
                        }

                    }
                }
            }
        }
    }

    private void invoke(Object activity, SubscribeMethod method, Object obj) {
        try {
            method.getMethod().invoke(activity,obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }


}
