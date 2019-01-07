package com.candidxd.justchat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
  * TODO
  * @author yaozekai
  * @date 2018/9/28 10:02 AM
  * @return
  */
@SpringBootApplication(scanBasePackages = {"com.candidxd.justchat.controller", "com.candidxd.justchat.service", "config", "com.candidxd.justchat.logger", "com.candidxd.justchat.redis", "com.candidxd.justchat.socket", "com.candidxd.justchat.thread", "com.candidxd.justchat.rabbitmq"})
@MapperScan("com.candidxd.justchat.dao")
@EnableTransactionManagement
@EnableCaching
public class JustchatApplication {

    public static void main(String[] args) {
        SpringApplication.run(JustchatApplication.class, args);
    }
}
