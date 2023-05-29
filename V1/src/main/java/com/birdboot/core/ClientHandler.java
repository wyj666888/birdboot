package com.birdboot.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * 该线程任务负责与指定的客户端(浏览器)完成一次HTTP交互
 * HTTP协议要求浏览器与服务端采取一问一答的模式，因此这里的交互流程分为三步：
 * 1：解析请求
 * 2：处理请求
 * 3：发送响应
 */
public class ClientHandler implements Runnable{
    private Socket socket;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    public void run() {
        try {
            //测试:读取来自浏览器发送过来的内容
            //HTTP协议要求，在没有附件的情况下， 浏览器发送过来的全是文字，单字节文字(英文，数字，符号)
            InputStream in = socket.getInputStream();
            int d;
            while((d = in.read()) != -1){
                System.out.print((char)d);
            }



        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
