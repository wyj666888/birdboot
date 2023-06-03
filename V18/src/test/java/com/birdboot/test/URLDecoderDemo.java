package com.birdboot.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class URLDecoderDemo {
    public static void main(String[] args) {
        String line = ":GET /regUser?username=%E5%88%9B%E5%A5%87&password=123456&nickname=%E4%B8%83%E4%B8%83&age=25 HTTP/1.1";
        try {
          String a=  URLDecoder.decode(line,"UTF-8");
            System.out.println(a);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
