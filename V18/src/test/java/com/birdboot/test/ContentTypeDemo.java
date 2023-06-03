package com.birdboot.test;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;

public class ContentTypeDemo {
    public static void main(String[] args) {
        MimetypesFileTypeMap mftm = new MimetypesFileTypeMap();
        File file = new File("demo.js");
        String contentType = mftm.getContentType(file);
        System.out.println(contentType);



    }
}
