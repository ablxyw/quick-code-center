package cn.ablxyw.config;

import cn.ablxyw.utils.GlobalUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.ablxyw.constants.GlobalConstants.*;

/**
 * 系统启动配置
 *
 * @author weiqiang
 * @date 2020-08-26 7:28 下午
 */
@Slf4j
@Order(value = 1)
@Component
@Data
public class SysStartConfig implements CommandLineRunner {
    /**
     * serverPort
     */
    @Value("${server.port}")
    private String serverPort;

    /**
     * context-path
     */
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 核心线程数
     */
    @Value("${qFrame.thread.corePoolSize:5}")
    private Integer corePoolSize;

    /**
     * 指定最大线程数
     */
    @Value("${qFrame.thread.maxPoolSize:1024}")
    private Integer maxPoolSize;

    /**
     * 队列中最大的数目
     */
    @Value("${qFrame.thread.queueCapacity:1024}")
    private Integer queueCapacity;

    /**
     * 线程空闲后的最大存活时间
     */
    @Value("${qFrame.thread.keepAliveSeconds:30}")
    private Integer keepAliveSeconds;
    /**
     * 开启swagger
     */
    @Value("${common.swagger.code.enable:true}")
    private boolean enableSwagger;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
        String apiUrl = GlobalUtils.appendString(HTTP_CODE, GlobalUtils.getHostIp(), INTERVAL_COLON, serverPort, contextPath);
        log.info("页面链接:{}", apiUrl);
        if (enableSwagger) {
            log.info("接口文档:{}/{}", apiUrl, "doc.html");
        }
    }

    /**
     * 默认线程池线程池
     *
     * @return Executor
     */
    @Primary
    @Bean
    public ThreadPoolTaskExecutor defaultThreadPool() {
        RejectedExecutionHandler rejected = new ThreadPoolExecutor.CallerRunsPolicy();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数目
        executor.setCorePoolSize(corePoolSize);
        //指定最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //队列中最大的数目
        executor.setQueueCapacity(queueCapacity);
        //线程名称前缀
        executor.setThreadNamePrefix(SYS_NAME + "_thread_pool_");
        //rejection-policy：当pool已经达到max size的时候，如何处理新任务
        //CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        //对拒绝task的处理策略
        executor.setRejectedExecutionHandler(rejected);
        //线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(keepAliveSeconds);
        //加载
        executor.initialize();
        return executor;
    }

    /**
     * 安全设置
     */
    @Configuration
    public static class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.headers().frameOptions().disable();
            http.authorizeRequests().anyRequest().permitAll().and().csrf().disable();
        }
    }
}
