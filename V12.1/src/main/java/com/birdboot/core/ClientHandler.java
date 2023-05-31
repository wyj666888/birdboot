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
    private Socket socket;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    public void run() {
        try {
            //1解析请求
            HttpServletRequest request = new HttpServletRequest(socket);
            HttpServletResponse response = new HttpServletResponse(socket);
            //2处理请求
            DispatcherServlet.getInstance().service(request,response);
            //3发送请求
            response.response();

        } catch (IOException e) {
            //对异常信息打桩输出
            e.printStackTrace();
        } catch (EmptyRequestException e) {
            //忽略异常,不进行异常处理
        } finally {
            try {
                //按照HTTP1.0协议规则，一问一答后断开链接
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
