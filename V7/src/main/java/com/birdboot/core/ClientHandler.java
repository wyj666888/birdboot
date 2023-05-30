package com.birdboot.core;

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
            String path = request.getUri();
            System.out.println("请求路径:"+path);
            File baseDir = new File(
                    ClientHandler.class.getClassLoader().getResource(".").toURI()
            );
            //定位类加载路径中的static目录
            File staticDir = new File(baseDir,"static");

            File file = new File(staticDir,path);

            if(file.isFile()){
                response.setStatusCode(200);
                response.setStatusReason("OK");
                response.setContentFile(file);
            }else{
                response.setStatusCode(404);
                response.setStatusReason("NotFound");
                response.setContentFile(new File(staticDir,"404.html"));
            }
            //3发送请求
            response.response();

        } catch (IOException |URISyntaxException e) {
            e.printStackTrace();
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
