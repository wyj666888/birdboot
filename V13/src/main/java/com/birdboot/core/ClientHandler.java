package com.birdboot.core;

import com.birdboot.http.EmptyRequestException;
import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * 该线程任务负责与指定的客户端(浏览器)完成一次HTTP交互
 * HTTP协议要求浏览器与服务端采取一问一答的模式，因此这里的交互流程分为三步：
 * 1：解析请求
 * 2：处理请求
 * 3：发送响应
 */
public class ClientHandler implements Runnable{
    //创建私有属性Socket,记为socket
    private Socket socket;
    //创建构造方法
    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    public void run() {
        try {
            //1解析请求
            //创建HttpServletRequest(socket)对象,记为request
            HttpServletRequest request = new HttpServletRequest(socket);
            //创建HttpServletResponse(socket)对象,记为response
            HttpServletResponse response = new HttpServletResponse(socket);
            //2处理请求
            //使用DispatcherServlet对象调用.getInstance()方法再调用service(request,response)
            DispatcherServlet.getInstance().service(request,response);
            //3发送请求
            //对象response调用response()方法
            response.response();
        } catch (IOException e) {
            //对异常信息打桩输出
            e.printStackTrace();
        } catch (EmptyRequestException e) {
            //忽略异常,不进行异常处理
        } finally {
            try {
                //按照HTTP1.0协议规则，一问一答后断开链接
                //对象socket调用close()方法
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
