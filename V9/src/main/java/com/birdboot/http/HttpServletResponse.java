package com.birdboot.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpServletResponse {
    private Socket socket;
    //状态行相关信息
    private int statusCode;//状态代码
    private String statusReason;//状态描述

    //响应头相关信息
    //key:响应头的名字,value:
    private Map<String,String> headers = new HashMap<>();


    //响应正文相关信息
    private File contentFile;//正文对应的实体文件


    public HttpServletResponse(Socket socket){
        this.socket=socket;
    }
    /*
    响应方法
    该方法会将当前响应对象的内容按照标椎的HTTP响应格式发送给客户端
     */
    public void response() throws IOException {
        //3发送响应
        //1发送状态行
        sendStatusLine();
        //2发送响应头
        sendHeaders();
        //3发送响应正文
        sendContent();
    }
    //1发送状态行
    private void sendStatusLine() throws IOException {
        println("HTTP/1.1"+" "+statusCode+" "+statusReason);
    }
    //2发送响应头
    private void sendHeaders() throws IOException {
        //遍历headers将所有待发送的响应头发送给客户端
        Set<Map.Entry<String,String>> entrySet = headers.entrySet();
        for (Map.Entry<String,String> e : entrySet){
            String key = e.getKey();
            String value = e.getValue();
            println(key+": "+value);
        }
        println("");//用空串单独发回车+换行
    }
    //发送响应正文
    private void sendContent() throws IOException {
        OutputStream out = socket.getOutputStream();
        FileInputStream fis = new FileInputStream(contentFile);
        int len = 0;
        byte[] buf = new byte[1024*10];
        //
        while((len = fis.read(buf))!=-1){
            out.write(buf,0,len);
        }
    }

    public void println(String line) throws IOException {
        OutputStream out = socket.getOutputStream();
        byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
        out.write(data);
        out.write(13);//单独发送了回车符
        out.write(10);//单独发送换行符
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public File getContentFile() {
        return contentFile;
    }

    public void setContentFile(File contentFile) {
        this.contentFile = contentFile;
    }

    public void addHeader(String name,String value){
        headers.put(name,value);
    }

}
