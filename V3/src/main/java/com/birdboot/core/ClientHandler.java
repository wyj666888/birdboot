package com.birdboot.core;

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
            String line = this.readLine();
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

            Map<String,String> headers = new HashMap<>();

            while (true){
                line = readLine();
                if (line.isEmpty()){
                    break;
                }
                System.out.println("消息头:"+line);
                data = readLine().split(":\\s");
                headers.put(data[0],data[1]);
            }
            System.out.println("headers:"+headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLine() throws IOException {
        InputStream in = socket.getInputStream();
        char pre='a',cur='a';//pre表示上次读取的字符,cur表示本次读取的字符
        StringBuilder builder = new StringBuilder();//记录已读取的一行字符串的内容
        int d;//每次读取到的字节,记为d
        while ((d=in.read())!=-1){
            cur = (char)d;//本次读取的字符
            if(pre==13&&cur==10){//是否已经连续读取到了回车+换行
                break;
            }
            builder.append(cur);//将本次读取的字符拼接
            pre = cur;//再下次读取前,将本次读取的字符赋值给pre,记为上次读取的字符
        }
        //返回时用表达式比较优雅^_^
        return builder.toString().trim();
    }
}
