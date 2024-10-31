package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableAsync
@EnableWebMvc
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        // 获取 Environment 对象
        Environment environment = context.getEnvironment();

        // 打印激活的 profile
        String activeProfiles = String.join(", ", environment.getActiveProfiles());
        System.out.println("Active Profiles: " + (activeProfiles.isEmpty() ? "No active profiles" : activeProfiles));
        System.out.println("record server is running……");
    }
}
