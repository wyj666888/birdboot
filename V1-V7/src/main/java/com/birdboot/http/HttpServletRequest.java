package com.birdboot.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequest {
    private Socket socket;
    private String method;
    private String uri;
    private String protocol;

    private Map<String,String> headers = new HashMap<>();

    public HttpServletRequest(Socket socket) throws IOException {
        this.socket = socket;
        parseRequestLine();
        parseHeaders();
        parseContent();
    }
    //解析请求行
    private void parseRequestLine() throws IOException {
        String line = readLine();
        System.out.println("请求行:"+line);
        String[] data = line.split("\\s");
        method = data[0];
        uri = data[1];
        protocol = data[2];

        System.out.println("method:"+method);
        System.out.println("uri:"+uri);
        System.out.println("protocol:"+protocol);
    }
    //解析消息头
    private void parseHeaders() throws IOException {
        while (true){
            String line = readLine();
            if (line.isEmpty()){
                break;
            }
            System.out.println("消息头:"+line);
            String[] data = line.split(":\\s");
            headers.put(data[0],data[1]);
        }
        System.out.println("headers:"+headers);
    }
    //解析消息正文
    private void parseContent(){

    }
    //读取一行
    private String readLine() throws IOException {
        InputStream in = socket.getInputStream();
        char pre='a',cur='a';
        StringBuilder builder = new StringBuilder();
        int d;
        while ((d=in.read())!=-1){
            cur=(char)d;
            if (pre==13&&cur==10){
                break;
            }
            builder.append(cur);
            pre=cur;
        }
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

    public String getHeaders(String name) {
        return headers.get(name);
    }
}
