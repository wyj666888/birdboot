package com.birdboot.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 项目主启动类
 */
public class BirdBootApplication {
    //创建私有属性ServerSocket套接字 记为serverSocket
    private ServerSocket serverSocket;
    //创建线程池
    private ExecutorService threadPool;
    //创建构造方法
    public BirdBootApplication(){
        try {
            System.out.println("正在启动服务端...");
            //new ServerSocket(8088)表示创建一个ServerSocket对象，
            // 并指定该对象监听的端口号为8088。用于接收客户端的TCP连接请求
            // 这里的8088是一个整数类型的值，代表服务器监听的端口号，客户端需要使用这个端口号来连接服务器。
            serverSocket = new ServerSocket(8088);
            //线程池实例化,设置容量
            threadPool = Executors.newFixedThreadPool(50);
            System.out.println("服务端启动完毕!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        try {
            //一问一答实现后,可以循环接受客户端的多次请求了
            while (true) {
                System.out.println("等待客户端连接...");
                //accept()方法会一直阻塞等待客户端的连接请求，当有客户端连接请求到达时，accept()方法会返回一个Socket对象，
                //该Socket对象记为socket
                // 该对象代表了服务器和客户端之间的通信通道。服务器可以通过这个Socket对象与客户端进行通信。
                Socket socket = serverSocket.accept();
                System.out.println("一个客户连接了!!!");
                //ClientHandler对象会启动一个新的线程来处理客户端的请求，这样可以避免阻塞主线程，提高服务器的并发处理能力。
                //创建ClientHandler(socket)对象,记为handler
                ClientHandler handler = new ClientHandler(socket);
                //将handler交给线程池
                threadPool.execute(handler);
                //创建Thread(handler)线程对象,记为t
                Thread t = new Thread(handler);
                //线程t调用start方法
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //创建BirdBootApplication对象,记为application
        BirdBootApplication application = new BirdBootApplication();
        //对象 application调用start方法
        application.start();
    }
}
