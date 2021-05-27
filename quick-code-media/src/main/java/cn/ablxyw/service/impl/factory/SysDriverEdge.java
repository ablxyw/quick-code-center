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
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.stereotype.Service;

/**
 * Edge浏览器转图片
 *
 * @author weiqiang
 * @date 2021-05-27 9:59 上午
 */
@Service("sysDriverEdge")
public class SysDriverEdge extends BaseDriverFactory {
    /**
     * 执行html转图片
     *
     * @param sysScreenshotEntity url转图片配置
     * @return ResultEntity
     */
    @Override
    public ResultEntity execute(SysScreenshotEntity sysScreenshotEntity) {
        //(1)设置driver驱动msedgedriver路径,下载地址:https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/，以下是mac形式，windows设置为具体盘符下的msedgedriver.exe文件位置
        if (StringUtils.isNotBlank(sysScreenshotEntity.getDriverPath())) {
            System.setProperty("webdriver.edge.driver", sysScreenshotEntity.getDriverPath());
        } else {
            if (GlobalUtils.isOsLinux()) {
                System.setProperty("webdriver.edge.driver", "/usr/local/bin/msedgedriver");
            } else {
                System.setProperty("webdriver.edge.driver", "C:\\Users\\Administrator\\AppData\\msedgedriver.exe");
            }
        }
        //(2)设置edge选项（取消浏览器弹窗）
        EdgeOptions options = new EdgeOptions();
        //(3)建立selenium 驱动
        WebDriver webDriver = new EdgeDriver(options);
        screenshot(sysScreenshotEntity, webDriver);
        return ResultUtil.success(GlobalEnum.MsgOperationSuccess, Lists.newArrayList(sysScreenshotEntity));
    }
}
