package com.birdboot.core;

import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.File;
import java.net.URISyntaxException;

public class DispatcherServlet {
    //这两个目录是固定的,因此定义为静态的,全局一份即可
    private static File baseDir;//类加载路径
    private static File staticDir;//类加载路径下的static目录
    private static DispatcherServlet instance = new DispatcherServlet();
    private DispatcherServlet(){}


    static{
        try {
            baseDir = new File(
                    ClientHandler.class.getClassLoader().getResource(".").toURI()
            );
            staticDir = new File(baseDir,"static");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    //实际上一个类继承了HttpServlet要重写方法:service
    public void service(HttpServletRequest request, HttpServletResponse response){
        String path = request.getUri();
        System.out.println("请求路径:"+path);
        File file = new File(staticDir,path);
        if(file.isFile()){
            response.setStatusCode(200);
            response.setStatusReason("OK");
            response.setContentFile(file);

            response.addHeader("Content-Type","text/html");
            response.addHeader("Content-Length",file.length()+"");
            response.addHeader("Server","BirdWebServer");

        }else{

            response.setStatusCode(404);
            response.setStatusReason("NotFound");
            file = new File(staticDir,"404.html");
            response.setContentFile(file);

            response.addHeader("Content-Type","text/html");
            response.addHeader("Content-Length",file.length()+"");
            response.addHeader("Server","BirdWebServer");
        }
    }
    public static DispatcherServlet getInstance(){
        return instance;
    }
}
