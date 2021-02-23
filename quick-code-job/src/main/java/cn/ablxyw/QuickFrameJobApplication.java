package cn.ablxyw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

/**
 * 分布式定时任务
 *
 * @author weiqiang
 * @date 2021-01-29 下午2:34
 */
@SpringBootApplication
@MapperScan(basePackages = "cn.ablxyw.mapper")
public class QuickFrameJobApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(QuickFrameJobApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }

}
