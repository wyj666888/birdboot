package com.birdboot.controller;
import com.birdboot.entity.User;
import com.birdboot.http.HttpServletRequest;
import com.birdboot.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class UserController {
    private static File userDir;

    static {
        userDir = new File("./users");
        if (!userDir.exists()){
            userDir.mkdirs();
        }
    }

    public void reg(HttpServletRequest request, HttpServletResponse response){
        System.out.println("正在处理用户注册----------");
        //1获取表单数据
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr  = request.getParameter("age");
        System.out.println(username+","+password+","+nickname+","+ageStr);

        if(username==null||username.isEmpty()||
                password==null||password.isEmpty()||
                nickname==null||nickname.isEmpty()||
                ageStr==null||ageStr.isEmpty()||
                !ageStr.matches("[0-9]+")
        ){
            response.sendRedirect("/reg_info_error.html");
            return;
        }

        int age = Integer.parseInt(ageStr);

        User user = new User(username, password,nickname,age);

        File userFile = new File(userDir,username+".obj");

        if (userFile.exists()){
            response.sendRedirect("//have_user.html");
            return;
        }
        try(
        FileOutputStream fos = new FileOutputStream(userFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        ){
            oos.writeObject(user);
            System.out.println("111111");
            response.sendRedirect("/reg_success.html");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
