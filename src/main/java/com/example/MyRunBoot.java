package com.example;

import com.example.boot.SpringApplication;
import org.apache.catalina.LifecycleException;

import javax.servlet.ServletException;

/**
 * @Author CaiWencheng
 * @Date 2021-10-12 23:38
 */
public class MyRunBoot {
    public static void main(String[] args) throws LifecycleException, InterruptedException, ServletException {

        SpringApplication.run();
    }
}
