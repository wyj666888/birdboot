package com.birdboot.core;

import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;

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
            HttpServletRequest request = new HttpServletRequest(socket);
            HttpServletResponse response = new HttpServletResponse(socket);

            String path = request.getUri();
            System.out.println("请求路径:"+path);
            File baseDir = new File(
                    ClientHandler.class.getClassLoader().getResource(".").toURI()
            );
            File staticDir = new File(baseDir,"static");
            File file = new File(staticDir,path);

            if (file.isFile()){
                response.setStatusCode(200);
                response.setStatusReason("OK");
                response.setContentFile(file);
            }else{
                response.setStatusCode(404);
                response.setStatusReason("NotFound");
                response.setContentFile(new File(staticDir,"404.html"));
            }
            response.response();

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
