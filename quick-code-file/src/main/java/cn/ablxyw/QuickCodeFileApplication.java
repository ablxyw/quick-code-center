package cn.ablxyw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

/**
 * @author bfb
 * @email bao_fubin@163.com
 * @date 2021-04-20 14:28:44
 */
@SpringBootApplication
@MapperScan(basePackages = "cn.ablxyw.mapper")
public class QuickCodeFileApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(QuickCodeFileApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
    }

}
