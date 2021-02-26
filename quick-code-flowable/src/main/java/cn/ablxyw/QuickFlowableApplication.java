package cn.ablxyw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.ApplicationPidFileWriter;

/**
 * 工作流启动类
 *
 * @author weiqiang
 * @date 2021-02-25 下午1:04
 */
@SpringBootApplication(scanBasePackages = {"cn.ablxyw"}, exclude = {SecurityAutoConfiguration.class})
@MapperScan(basePackages = "cn.ablxyw.mapper")
public class QuickFlowableApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(QuickFlowableApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }
}
