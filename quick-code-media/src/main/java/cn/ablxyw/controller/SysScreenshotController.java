package cn.ablxyw.controller;

import cn.ablxyw.entity.SysScreenshotEntity;
import cn.ablxyw.service.SysScreenshotService;
import cn.ablxyw.vo.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weiqiang
 * @Description
 * @date 2021-05-26 4:34 下午
 */
@RestController
@RequestMapping(value = "sysScreenshot")
public class SysScreenshotController {

    @Autowired
    private SysScreenshotService sysScreenshotService;

    @GetMapping
    public ResultEntity list(SysScreenshotEntity sysScreenshotEntity) {
        return sysScreenshotService.list(sysScreenshotEntity);
    }
}
