package cn.ablxyw.util;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * html转图片测试
 *
 * @author weiqiang
 * @date 2021-05-26 1:24 下午
 */
public class Html2ImageTests {
    /**
     * 文档路径
     */
    public static final String BASE_PATH = System.getProperty("user.dir") + File.separator + "logs" + File.separator;

    @Test
    public void webDriverTest() throws InterruptedException {
        //1.获取网页的浏览器driver
        //(1)定义url
        String url = "http://news.baidu.com";
        //(2)设置driver驱动chromedriver路径,下载地址:http://npm.taobao.org/mirrors/chromedriver/，以下是mac形式，windows设置为具体盘符下的chromedriver.exe文件位置
        System.setProperty("webdriver.chrome.driver", "/Users/${userName}/Downloads/chromedriver");
        //(3)设置chrome选项（取消浏览器弹窗）
        ChromeOptions options = new ChromeOptions();
        //(3)建立selenium 驱动
        WebDriver webDriver = new ChromeDriver();
        //设置浏览器宽高
        org.openqa.selenium.Dimension dimension = new org.openqa.selenium.Dimension(7680, 2160);
//        webDriver.manage().window().setSize(dimension);
        webDriver.manage().window().fullscreen();
        webDriver.manage().deleteAllCookies();
        // 与浏览器同步非常重要，必须等待浏览器加载完毕
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        webDriver.get(url);
        //2.设置等待时间
        Thread.sleep(1000);
        String title = webDriver.getTitle();
        File srcfile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(srcfile, new File(BASE_PATH + "test_" + title + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //3.关闭并退出
            webDriver.close();
            webDriver.quit();
            System.out.println(title);
        }

    }
}
