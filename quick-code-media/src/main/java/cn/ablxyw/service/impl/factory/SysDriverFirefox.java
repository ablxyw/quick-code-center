package cn.ablxyw.service.impl.factory;

import cn.ablxyw.entity.SysScreenshotEntity;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.service.BaseDriverFactory;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.stereotype.Service;

/**
 * Firefox浏览器转图片
 *
 * @author weiqiang
 * @date 2021-05-27 9:59 上午
 */
@Service("sysDriverFirefox")
public class SysDriverFirefox extends BaseDriverFactory {
    /**
     * 执行html转图片
     *
     * @param sysScreenshotEntity url转图片配置
     * @return ResultEntity
     */
    @Override
    public ResultEntity execute(SysScreenshotEntity sysScreenshotEntity) {
        //(1)设置driver驱动geckodriver路径,下载地址:https://github.com/mozilla/geckodriver/releases，以下是mac形式，windows设置为具体盘符下的geckodriver.exe文件位置
        if (StringUtils.isNotBlank(sysScreenshotEntity.getDriverPath())) {
            System.setProperty("webdriver.chrome.driver", sysScreenshotEntity.getDriverPath());
        } else {
            if (GlobalUtils.isOsLinux()) {
                System.setProperty("webdriver.chrome.driver", "/usr/local/bin/geckodriver");
            } else {
                System.setProperty("webdriver.chrome.driver", "C:\\Users\\Administrator\\AppData\\geckodriver.exe");
            }
        }
        //(2)设置Firefox选项（取消浏览器弹窗）
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("headless");
        //(3)建立selenium 驱动
        WebDriver webDriver = new FirefoxDriver(options);
        screenshot(sysScreenshotEntity, webDriver);
        return ResultUtil.success(GlobalEnum.MsgOperationSuccess, Lists.newArrayList(sysScreenshotEntity));
    }
}
