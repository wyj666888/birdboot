package com.birdboot.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequest {
    /**
     * 将类的属性声明为private可以实现封装的概念
     * 如果属性不被声明为private，那么其他类就可以直接访问和修改属性的值，
     * 这样可能会破坏类的封装性并造成潜在的安全风险
     */
    private Socket socket;
    //请求行相关信息
    private String method;//请求方式
    private String uri;//抽象路径
    private String protocol;//协议版本
    private Map<String,String> headers = new HashMap<>();

    public HttpServletRequest(Socket socket) throws IOException {
        this.socket = socket;
        String line = this.readLine();
        System.out.println("请求行:"+line);

        String[] data = line.split("\\s");
        method = data[0];
        uri = data[1];
        protocol = data[2];

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
    }

    public void run(){

    }
    private String readLine() throws IOException {
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

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }
    //一般Map的返回值不会是Map
    //根据消息头的名字获取对应的值,
    public String getHeader(String name) {
        return headers.get(name);
    }
}
