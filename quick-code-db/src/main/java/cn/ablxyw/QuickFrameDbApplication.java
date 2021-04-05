package cn.ablxyw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

/**
 * 快速db
 *
 * @author weiqiang
 * @date 2021-04-04 下午7:25
 */
@SpringBootApplication
@MapperScan(basePackages = "cn.ablxyw.mapper", value = {"com.baomidou.mybatisplus.samples.quickstart.mapper"})
public class QuickFrameDbApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(QuickFrameDbApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }
}
