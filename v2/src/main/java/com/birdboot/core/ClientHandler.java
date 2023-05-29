package com.birdboot.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
/**
 * 项目主启动类
 */
public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        //测试:读取来自浏览器发过来的内容
        //HTTP协议要求:在没有附件的情况下,浏览器发送来的全是文字,单字节
        try {
        InputStream in = socket.getInputStream();
        int d;
        while ((d = in.read())!= -1){
            System.out.print((char)d);
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
