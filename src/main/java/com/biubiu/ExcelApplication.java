package com.biubiu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Created by Haibiao.Zhang on 2019-03-26 08:40
 */
@MapperScan(basePackages = "com.biubiu.mapper")
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ExcelApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExcelApplication.class);
    }

}
