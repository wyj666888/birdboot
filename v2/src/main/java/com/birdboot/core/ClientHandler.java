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
            //实现读取一行字符串的操作(浏览器发送过来的内容为请求，其中第一行应当是请求行)
            InputStream in = socket.getInputStream();

            char pre='a',cur='a';//pre表示上次读取的字符，cur表示本次读取的字符
            StringBuilder builder = new StringBuilder();//记录已读取的一行字符串的内容
            int d;//每次读取到的字节
            while((d = in.read()) != -1){
                cur = (char)d;//本次读取的字符
                if(pre==13 && cur==10){//是否已经连续读取到了回车+换行
                    break;
                }
                builder.append(cur);//将本次读取的字符拼接
                pre = cur;//再下次读取前，将本次读取的字符记为上次读取的字符
            }
            //trim的目的是将最后读取到的回车符去除
            String line = builder.toString().trim();
            System.out.println("请求行:"+line);

            //请求行相关信息
            String method;//请求方式
            String uri;//抽象路径
            String protocol;//协议版本
            //测试路径:http://localhost:8088/index.html

            String[] data = line.split("\\s");
            method = data[0];
            uri = data[1];
            protocol = data[2];

            System.out.println("method:"+method);//method:GET
            System.out.println("uri:"+uri);//uri:/index.html
            System.out.println("protocol:"+protocol);//protocol:HTTP/1.1


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
