package com.birdboot.test;

import java.util.Arrays;

public class SplitDemo {
    public static void main(String[] args) {
        String line = "1=2=3=4=5=6=7=8===============";
        String[] data = line.split("=");
        System.out.println(Arrays.toString(data));

        data = line.split("=",2);
        System.out.println(Arrays.toString(data));

        data = line.split("=",15);
        System.out.println(Arrays.toString(data));

        data = line.split("=",100);
        System.out.println(Arrays.toString(data));

        data = line.split("=",-1);
        System.out.println(Arrays.toString(data));
    }
}
