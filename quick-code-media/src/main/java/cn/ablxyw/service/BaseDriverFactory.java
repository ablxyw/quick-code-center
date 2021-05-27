package cn.ablxyw.service;

import cn.ablxyw.entity.SysScreenshotEntity;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.vo.ResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static cn.ablxyw.constants.GlobalConstants.*;

/**
 * html转图片
 *
 * @author weiqiang
 * @date 2021-05-27 9:51 上午
 */
@Slf4j
public abstract class BaseDriverFactory {
    /**
     * 文档路径
     */
    public static final String BASE_PATH = System.getProperty("user.dir") + File.separator + "logs" + File.separator;

    /**
     * 生成图片
     *
     * @param sysScreenshotEntity 配置信息
     * @param webDriver           驱动
     * @return SysScreenshotEntity
     */
    protected SysScreenshotEntity screenshot(SysScreenshotEntity sysScreenshotEntity, WebDriver webDriver) {
        String url = sysScreenshotEntity.getUrl();
        log.info("开始获取：{}", url);
        long beginTime = System.currentTimeMillis();
        //设置浏览器宽高
        if (Objects.nonNull(sysScreenshotEntity.getWidth()) && Objects.nonNull(sysScreenshotEntity.getHeight())) {
            org.openqa.selenium.Dimension dimension = new org.openqa.selenium.Dimension(sysScreenshotEntity.getWidth(), sysScreenshotEntity.getHeight());
            webDriver.manage().window().setSize(dimension);
        } else {
            webDriver.manage().window().maximize();
        }
        if (Objects.nonNull(sysScreenshotEntity.getFullscreen()) && sysScreenshotEntity.getFullscreen()) {
            webDriver.manage().window().fullscreen();
        }
        webDriver.manage().deleteAllCookies();
        // 与浏览器同步非常重要，必须等待浏览器加载完毕
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        webDriver.get(url);
        //2.设置等待时间
        String title = null;
        try {
            Thread.sleep(Objects.nonNull(sysScreenshotEntity.getSleepTimeout()) ? sysScreenshotEntity.getSleepTimeout() : 1000);
            title = webDriver.getTitle();
            //title多于20个字符之后截取
            title = StringUtils.isBlank(title) ? GlobalUtils.ordinaryId() : (title.length() > 20 ? GlobalUtils.appendString(title.substring(0, 20), UNDER_LINE, GlobalUtils.ordinaryId()) : title);
            File srcfile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(BASE_PATH + title + POINT + (StringUtils.isNotBlank(sysScreenshotEntity.getFileType()) ? sysScreenshotEntity.getFileType() : PNG_CODE));
            //TODO 存入文件服务器，返回文件服务地址
            FileUtils.copyFile(srcfile, destFile);
        } catch (Exception e) {
            log.error("生成图片错误:{}", e.getMessage());
        } finally {
            //3.关闭并退出
            webDriver.close();
            webDriver.quit();
            log.info("生成:{},结束,耗时:{}ms", url, (System.currentTimeMillis() - beginTime));
        }
        return sysScreenshotEntity;
    }

    /**
     * 执行html转图片
     *
     * @param screenshotEntity url转图片配置
     * @return ResultEntity
     */
    public abstract ResultEntity execute(SysScreenshotEntity screenshotEntity);

}
