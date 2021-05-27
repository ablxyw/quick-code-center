package cn.ablxyw.service;

import cn.ablxyw.entity.SysScreenshotEntity;
import cn.ablxyw.vo.ResultEntity;

/**
 * html转图配置Service
 *
 * @author weiqiang
 * @date 2021-05-26 4:03 下午
 */
public interface SysScreenshotService extends BaseInfoService<SysScreenshotEntity, String> {

    /**
     * html转图片
     *
     * @param sysScreenshotEntity html转图配置
     * @return ResultEntity
     */
    ResultEntity execute(SysScreenshotEntity sysScreenshotEntity);
}
