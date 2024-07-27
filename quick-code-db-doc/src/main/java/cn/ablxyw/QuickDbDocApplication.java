package cn.ablxyw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

/**
 * 数据库设计文档
 *
 * @author weiqiang
 * @date 2021-04-16 下午9:23
 */
@SpringBootApplication
@MapperScan(basePackages = "cn.ablxyw.mapper")
public class QuickDbDocApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(QuickDbDocApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }
}
