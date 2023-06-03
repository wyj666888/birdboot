package com.birdboot.core;

import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;
import com.birdboot.controller.UserController;
import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    public void service(HttpServletRequest request, HttpServletResponse response) {
        //创建对象request.getRequestURI(),获取uri的?左侧的值,记为path
        String path = request.getRequestURI();
        System.out.println("请求路径:" + path);
        //判断path里包不包含业务处理,有则给出对应响应,
        Method method = HandlerMapping.getMethod(path);
        if (method!=null){
            try {
                //根据当前方法对象获取该方法所属的对象
                Class cls = method.getDeclaringClass();
                method.invoke(cls.newInstance(), request, response);
            } catch (Exception e) {
                //处理业务如果出现异常,则应该给浏览器响应异常500,告知
                response.setStatusCode(500);
                response.setStatusReason("Internal Server Error");
            }
        }else {
            //创建File(staticDir, path)对象,是staticDir下的path文件(这里指网页)
            File file = new File(staticDir, path);
            //判断文件是不是普通文件
            if (file.isFile()) {
                //发送状态码200,response调用setStatusCode()方法
                response.setStatusCode(200);
                //发送状态描述,response调用setStatusReason()方法
                response.setStatusReason("OK");
                //发送文件,response调用setStatusFile()方法
                response.setContentFile(file);
                //response调用addHeader()方法,将响应头存进Map类型的headers中
                response.addHeader("Server", "BirdWebServer");
            } else {
                response.setStatusCode(404);
                response.setStatusReason("NotFound");
                //发送404页面,response调用setStatusFile()方法
                file = new File(staticDir, "404.html");
                response.setContentFile(file);
                response.addHeader("Server", "BirdWebServer");
            }
        }
    }
    public static DispatcherServlet getInstance(){
        return instance;
    }
}
