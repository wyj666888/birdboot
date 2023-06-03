package com.birdboot.controller;
import com.birdboot.annotation.Controller;
import com.birdboot.annotation.RequestMapping;
import com.birdboot.entity.User;
import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    private static File userDir;

    static {
        userDir = new File("./users");
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
    }
    @RequestMapping("/regUser")
    public void reg(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("正在处理用户注册----------");
        //1获取表单数据
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr = request.getParameter("age");
        System.out.println(username + "," + password + "," + nickname + "," + ageStr);

        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty() ||
                nickname == null || nickname.isEmpty() ||
                ageStr == null || ageStr.isEmpty() ||
                !ageStr.matches("[0-9]+")
        ) {
            response.sendRedirect("/reg_info_error.html");
            return;
        }

        int age = Integer.parseInt(ageStr);

        User user = new User(username, password, nickname, age);

        File userFile = new File(userDir, username + ".obj");

        if (userFile.exists()) {
            response.sendRedirect("//have_user.html");
            return;
        }
        try (
                FileOutputStream fos = new FileOutputStream(userFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(user);
            System.out.println("111111");
            response.sendRedirect("/reg_success.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @RequestMapping("/loginUser")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("正在处理用户登录----------");
        //1获取表单数据
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println(username + "," + password);

        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty()
        ) {
            response.sendRedirect("/reg_info_error.html");
            return;
        }

        File userFile = new File(userDir, username + ".obj");

        if (userFile.exists()) {
            try (FileInputStream fis = new FileInputStream(userFile);
                 ObjectInputStream ois = new ObjectInputStream(fis);
            ) {

                User user = (User) ois.readObject();
                System.out.println(user);

                if (user.getUsername().equals(username) &&
                        user.getPassword().equals(password)) {
                    response.sendRedirect("/login_success.html");
                } else {
                    response.sendRedirect("/login_fail.html");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            response.sendRedirect("/login_info_error.html");
        }
    }
    /*@RequestMapping("/userList")
   public void userList(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始处理用户列表的动态页面!!!");
        //创建数组subs,存储userDir目录下得子类,以".obj"结尾的文件
        File[] subs = userDir.listFiles(f->f.getName().endsWith(".obj"));
        //创建ArrayList集合,User对象类型,名userList,使用List接口
        //这样可以使代码更加灵活，可以轻松替换为其他List实现类，而不会影响到其他代码。而且List是一个更一般化的接口，其定义更广泛，可支持更多的操作，因此使用List会更加通用。
        List<User> userList = new ArrayList<>();
        //遍历数组subs的File文件,
        for(File sub : subs){
            try(FileInputStream fis = new FileInputStream(sub);
                ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                User user = (User)ois.readObject();
                //将对象user添加到userList集合中
                userList.add(user);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println(userList);

        try {
            //跳转动态页面
            response.setContentType("text/html;charset=utf-8");
            *//*
             * 第一行代码是设置HTTP响应的Content-Type头部，告诉客户端响应内容的类型和编码格式。
             * 在这里，设置的Content-Type类型为"text/html"，编码格式为"utf-8"。也就是说，
             * 服务器响应的内容是HTML格式的文本，并且使用UTF-8编码格式。第二行代码是获取HTTP响应输出流PrintWriter对象。
             * PrintWriter是一个字符流，它允许写入字符数据到客户端响应的输出流中。通过这个输出流，
             * 可以将HTTP响应的HTML页面内容输出到客户端的浏览器中。通常，在输出HTML页面内容之前，
             * 需要设置Content-Type头部，告诉浏览器如何处理输出的数据。因此，
             * 这两行代码一起实现了设置HTTP响应的类型和编码格式，并将HTML页面内容输出到客户端的浏览器中*//*
            PrintWriter pw = response.getWriter();
            pw.println("<!DOCTYPE html>");
            pw.println("<html lang=\"en\">");
            pw.println("<head>");
            pw.println("<meta charset=\"UTF-8\">");
            pw.println("<title>用户列表</title>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("<center>");
            pw.println("<h1>用户列表</h1>");
            pw.println("<table border=\"1\">");
            pw.println("<tr><td>用户名</td><td>密码</td><td>昵称</td><td>年龄</td></tr>");
            //遍历对象集合
            for(User user : userList) {
                pw.println("<tr>");
                pw.println("<td>"+user.getUsername()+"</td>");
                pw.println("<td>"+user.getPassword()+"</td>");
                pw.println("<td>"+user.getNickname()+"</td>");
                pw.println("<td>"+user.getAge()+"</td>");
                pw.println("</tr>");
            }

            pw.println("</table>");
            pw.println("</center>");
            pw.println("</body>");
            pw.println("</html>");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


}