package com.birdboot.core;

import com.birdboot.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
        //HTTP协议要求:在没有附件的情况下,浏览器发送来的全是文字,单字节文字(英文，数字，符号)
        try {
            //1解析请求
            HttpServletRequest request = new HttpServletRequest(socket);

            String path = request.getUri();
            System.out.println("请求路径:"+path);

            //2处理请求

            //3发送响应
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
