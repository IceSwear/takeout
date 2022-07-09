package com.kk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@EnableAsync
@Slf4j
@EnableCaching
//scanner @webfilter
@ServletComponentScan
//@MapperScan(basePackages = "com.kiseki.model.persistence")
public class KkTakeoutApplication {
    public static void main(String[] args) throws IOException {
//        args[0]="--server.port=80";
        SpringApplication.run(KkTakeoutApplication.class, args);
        //for email
        System.getProperties().setProperty("mail.mime.splitlongparameters", "false");
        System.getProperties().setProperty("mail.mime.charset", "UTF-8");
        log.info("Project start successfully～～～～～");
    }
}
