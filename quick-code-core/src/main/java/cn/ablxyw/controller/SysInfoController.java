package cn.ablxyw.controller;

import cn.ablxyw.service.SysInfoService;
import cn.ablxyw.vo.ResultEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统信息接口
 *
 * @author weiqiang
 * @date 2020-03-30 3:27 下午
 */
@Api(value = "系统信息接口Api", tags = "系统信息接口Api")
@RestController
@RequestMapping("/system")
public class SysInfoController {

    /**
     * 系统Service
     */
    @Autowired
    private SysInfoService sysInfoService;

    /**
     * 获取系统配置信息
     *
     * @return ResultEntity
     */
    @ApiOperation("系统配置信息")
    @RequestMapping(value = "sysInfo", method = RequestMethod.GET)
    public ResultEntity sysInfo() {
        return sysInfoService.sysInfo();
    }

    /**
     * 系统运行信息
     *
     * @return ResultEntity
     */
    @ApiOperation("系统运行信息")
    @RequestMapping(value = "/systemRunInfo", method = RequestMethod.GET)
    public ResultEntity systemRunInfoVo() {
        return sysInfoService.systemRunInfoVo();
    }

}
