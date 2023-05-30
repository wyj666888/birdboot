package com.birdboot.core;

import com.birdboot.http.HttpServletRequest;

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

            String path = request.getUri();
            System.out.println("请求路径:"+path);

            //2处理请求
            /*
                后期开发中一个非常常用的相对路径:类加载路径
                如何定位类加载路径的位置:
                File baseDir = new File(
                    当前类.class.getClassLoader().getResource(".").toURI()
                )

                类加载路径对应的目录时包含我们当前项目所有包的那个目录。
                可以理解为是你项目中任意一个类上定义的包package中顶级包的上一级目录
                举例:以当前类ClientHandler为例
                ClientHandler上包定义:package com.birdboot.core;
                说明当前类在core包中，core在birdboot中，birdboot在com包中。因此com包
                就是当前类的顶级包，而类加载路径对应的目录就是包含com的那个目录


                由于JVM执行的是class文件，因此实际类加载路径应当是ClientHandler.class
                所在顶级包的上一级，也就是target/classes这个目录

             */
            //测试回复固定的index.html页面
            File baseDir = new File(
                    ClientHandler.class.getClassLoader().getResource(".").toURI()
            );
            //定位类加载路径中的static目录
            File staticDir = new File(baseDir,"static");
            //定位static目录中的index.html页面
//            File file = new File(staticDir,"index.html");
            /*
                http://localhost:8088/index.html
                path:/index.html
                可以在static目录中定位该文件

                下面两种情况都是404的现象

                http://localhost:8088/abc.html
                path:/abc.html
                在static目录中没有该文件

                http://localhost:8088
                path:/
                定位是static目录里

                404的响应:
                HTTP/1.1 404 NotFound(CRLF)
                Content-Type: text/html(CRLF)
                Content-Length: 2546(404页面长度)(CRLF)(CRLF)
                1011101010101010101......(404页面)

             */
            File file = new File(staticDir,path);

            int statusCode;//状态代码
            String statusReason;//状态描述
            if(file.isFile()){
                statusCode = 200;
                statusReason = "OK";
            }else{
                statusCode = 404;
                statusReason = "NotFound";
                file = new File(staticDir,"404.html");
            }
            //3发送响应
            //1发送状态行
            println("HTTP/1.1"+" "+statusCode+" "+statusReason);
            //2发送响应头
            println("Content-Type: text/html");
            println("Content-Length: " + file.length());
            println("");//用空串单独发回车+换行

            //3发送响应正文
            OutputStream out = socket.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            int len = 0;
            byte[] buf = new byte[1024*10];
            while((len = fis.read(buf))!=-1){
                out.write(buf,0,len);
            }

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

    public void println(String line) throws IOException {
        OutputStream out = socket.getOutputStream();
        byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
        out.write(data);
        out.write(13);//单独发送了回车符
        out.write(10);//单独发送换行符
    }
}
