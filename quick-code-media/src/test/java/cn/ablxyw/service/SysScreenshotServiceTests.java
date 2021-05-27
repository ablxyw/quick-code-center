package cn.ablxyw.service;

import cn.ablxyw.entity.SysScreenshotEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * html转图配置ServiceTests
 *
 * @author weiqiang
 * @date 2021-05-27 3:41 下午
 */
@Slf4j
@SpringBootTest
public class SysScreenshotServiceTests {

    /**
     * html转图配置Service
     */
    @Autowired
    private SysScreenshotService sysScreenshotService;

    /**
     * 测试生成
     */
    @Test
    public void execute() {
        SysScreenshotEntity sysScreenshotEntity = SysScreenshotEntity.builder()
                .url("https://blog.csdn.net/wtl1992/article/details/102833729")
                .driverPath("/Users/weiqiang/Downloads/chromedriver")
                .fullscreen(true)
                .build();
        sysScreenshotService.execute(sysScreenshotEntity);
    }
}
