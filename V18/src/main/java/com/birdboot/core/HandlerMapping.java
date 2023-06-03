package com.birdboot.core;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class HandlerMapping {
    private static Map<String, Method> mapping = new HashMap<>();

    static {
        initMapping();
    }
    private static void initMapping(){
        try {
            File dir = new File(
                    HandlerMapping.class.getClassLoader().getResource(".").toURI());
            File controllerDir = new File(dir, "com/birdboot/controller");
            File[] subs = controllerDir.listFiles(f -> f.getName().contains(".class"));
            for (File sub : subs) {
                int a = sub.getName().indexOf(".");
                String className = sub.getName().substring(0, a);
                //一个类的包名是指:代码编译后classes目录下开始到该类的上一级目录的路径为包名
                Class cls = Class.forName("com.birdboot.controller." + className);
                boolean mark = cls.isAnnotationPresent(Controller.class);
                if (mark) {
                    Method[] methods = cls.getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(RequestMapping.class)) {
                            RequestMapping arm = method.getAnnotation(RequestMapping.class);
                            String value = arm.value();
                            //将方法与其处理的请求路径分别作为Map的key,value存入mapping
                            mapping.put(value,method);
                        }
                    }
                }
            }
        } catch (URISyntaxException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据请求路径获取对应的处理方法.如果返回值为null说明该路径不是所求方法
     * @param path
     * @return
     */
    public static Method getMethod(String path){
        return mapping.get(path);
    }
}
