package com.birdboot.core;

import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.File;
import java.net.URISyntaxException;
/**
 * 该类是SpringMVC框架与Tomcat整合时的一个关键类
 * Tomcat处理业务原生的都是调用继承了HttpServlet的类来完成，此时需要进行很多配置
 * 以及使用时要作很多重复性劳动。
 * SpringMVC框架提供的该类也是继承了HttpServlet的，使用它来接收处理请求的工作。
 */
public class DispatcherServlet {
    //这两个目录是固定的,因此定义为静态的,全局一份即可
    private static File baseDir;//类加载路径
    private static File staticDir;//类加载路径下的static目录
    private static DispatcherServlet instance = new DispatcherServlet();
    private DispatcherServlet(){}
    static{
        try {
//这行代码是在ClientHandler类的构造方法中获取当前类所在的根目录，用于指定服务器端文件的根目录。
//具体来说，getClassLoader()方法返回当前类的类加载器对象,
//getResource(".")方法会返回当前类所在的根目录的URL对象，最后通过toURI()方法将URL对象转换成URI对象,
//然后再通过File类的构造方法将URI对象转换成File对象，从而获取当前类所在的根目录。
            baseDir = new File(
                    ClientHandler.class.getClassLoader().getResource(".").toURI()
            );
            //创建File(baseDir,"static")对象,赋值给staticDir
            staticDir = new File(baseDir,"static");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    //实际上,一个类继承了HttpServlet,要重写方法:service()
    //service()需传参HttpServletRequest request, HttpServletResponse response
    public void service(HttpServletRequest request, HttpServletResponse response){
        //创建对象
        String path = request.getUri();
        System.out.println("请求路径:"+path);
        File file = new File(staticDir,path);
        if(file.isFile()){
            response.setStatusCode(200);
            response.setStatusReason("OK");
            response.setContentFile(file);
            response.addHeader("Server","BirdWebServer");

        }else{
            response.setStatusCode(404);
            response.setStatusReason("NotFound");
            file = new File(staticDir,"404.html");
            response.setContentFile(file);
            response.addHeader("Server","BirdWebServer");
        }
    }
    public static DispatcherServlet getInstance(){
        return instance;
    }
}
