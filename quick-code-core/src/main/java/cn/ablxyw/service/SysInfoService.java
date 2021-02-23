package cn.ablxyw.service;

import cn.ablxyw.vo.ResultEntity;

/**
 * 系统Service
 *
 * @author weiqiang
 * @date 2021-01-19 下午4:14
 */
public interface SysInfoService {

    /**
     * 获取系统配置信息
     *
     * @return ResultEntity
     */
    ResultEntity sysInfo();

    /**
     * 服务器运行信息
     *
     * @return ResultEntity
     */
    ResultEntity systemRunInfoVo();
}
