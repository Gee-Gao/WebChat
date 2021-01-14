package com.gee;

import com.gee.utils.SpringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.gee.mapper")
public class ImBirdSysApplication {
    @Bean
    public SpringUtil getSpringUtil(){
        return new SpringUtil();
    }

    public static void main(String[] args) {
        SpringApplication.run(ImBirdSysApplication.class, args);
    }
}
