package com.vienna.jaray;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * @author Jaray
 * @date 2020年09月10日 22:29
 * @description: 项目启动类
 */
@SpringBootApplication(exclude={
        org.activiti.spring.boot.SecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class
})
public class JarayProjectWebApplication {
    /**
     * 项目启动方法
     * @param args 参数
     */
    public static void main(String[] args) {
        SpringApplication.run(JarayProjectWebApplication.class, args);
    }

}
