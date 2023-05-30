package com.birdboot.core;

import com.birdboot.http.HttpServletRequest;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
            File baseDir = new File(
                    ClientHandler.class.getClassLoader().getResource(".").toURI()
            );
            //定位类加载路径中的static目录
            File staticDir = new File(baseDir,"static");
            //定位static目录中的index.html页面
            File file = new File(staticDir,path);

            //3发送响应
            /*
                HTTP/1.1 200 OK(CRLF)
                Content-Type: text/html(CRLF)
                Content-Length: 2546(CRLF)(CRLF)
                1011101010101010101......
             */
            OutputStream out = socket.getOutputStream();
            //1发送状态行
            String line = "HTTP/1.1 200 OK";
            byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
            out.write(data);
            out.write(13);//单独发送了回车行
            out.write(10);//单独发送换行符

            //2发送响应头
            line = "Content-Type: text/html";
            data = line.getBytes(StandardCharsets.ISO_8859_1);
            out.write(data);
            out.write(13);
            out.write(10);

            //单独发送回车+换行表达响应头部分发送完了
            out.write(13);
            out.write(10);

            //3发送响应正文
            //创建文件输入流,输入file文件,记为fis
            FileInputStream fis = new FileInputStream(file);
            //定义变量len,并初始化
            int len;
            //创建块写数组byte[],记为buf
            byte[] buf = new byte[1024*10];
            //创建循环,对文件进行块读写,当读取的返回值不为-1,
            while ((len=fis.read(buf))!=-1){
                out.write(buf,0,len);
            }

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
