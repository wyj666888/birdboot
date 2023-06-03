package com.birdboot.controller;
import com.birdboot.entity.User;
import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;
import jdk.management.resource.internal.inst.SocketOutputStreamRMHooks;

import java.io.*;

public class UserController {
    private static File userDir;

    static {
        userDir = new File("./users");
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
    }

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
}