package cn.ablxyw.controller;

import cn.ablxyw.entity.SysScreenshotEntity;
import cn.ablxyw.service.SysScreenshotService;
import cn.ablxyw.vo.ResultEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * html转图配置Controller
 *
 * @author weiqiang
 * @date 2021-05-26 4:34 下午
 */
@Api(value = "html转图接口文档Api", tags = "html转图接口文档接口")
@CrossOrigin
@RestController
@RequestMapping(value = "sysScreenshot")
public class SysScreenshotController {

    /**
     * html转图配置Service
     */
    @Autowired
    private SysScreenshotService sysScreenshotService;

    /**
     * 查询所有的html转图配置
     *
     * @param sysScreenshotEntity html转图配置
     * @return ResultEntity
     */
    @ApiOperation("查询所有的html转图配置")
    @GetMapping
    public ResultEntity list(SysScreenshotEntity sysScreenshotEntity) {
        return sysScreenshotService.list(sysScreenshotEntity);
    }
}
