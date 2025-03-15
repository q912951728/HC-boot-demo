package com.ztj.hcboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ztj.hcboot.mapper")
public class HcBootDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HcBootDemoApplication.class, args);
    }

}
