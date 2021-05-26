package cn.ablxyw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

/**
 * 快速媒体Application
 *
 * @author weiqiang
 * @date 2021-05-26 3:16 下午
 */
@SpringBootApplication
@MapperScan(basePackages = "cn.ablxyw.mapper")
public class QuickMediaApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(QuickMediaApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }
}
