package cn.ablxyw.service;

import cn.ablxyw.entity.SysScreenshotLogEntity;

import java.util.List;

/**
 * html转图配置LogService
 *
 * @author weiqiang
 * @date 2021-05-26 4:03 下午
 */
public interface SysScreenshotLogService extends BaseInfoService<SysScreenshotLogEntity, String> {

    /**
     * 根据shotId获取生成日志信息
     *
     * @param shotId shotId
     * @return List
     */
    List<SysScreenshotLogEntity> listByShotId(String shotId);
}
