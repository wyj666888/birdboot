package com.birdboot.test;
/**
 * 1.私有化构造器
 * 2:定义静态的私有的当前类的属性并实例化
 * 3:定义公开的静态的获取
 */
public class Singleton {
    private static Singleton instance = new Singleton();

    private Singleton(){}

    public static Singleton getInstance(){
        return instance;
    }
}
