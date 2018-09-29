package com.candidxd.justchat;

import config.WebMvcConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
  * TODO
  * @author yaozekai
  * @date 2018/9/28 10:02 AM
  * @return
  */
@SpringBootApplication(scanBasePackages = {"com.candidxd.justchat.controller", "com.candidxd.justchat.service","config","com.candidxd.justchat.logger","com.candidxd.justchat.redis","com.candidxd.justchat.socket"})
@MapperScan("com.candidxd.justchat.dao")
@EnableTransactionManagement
public class JustchatApplication {

    public static void main(String[] args) {
        SpringApplication.run(JustchatApplication.class, args);
    }
}
