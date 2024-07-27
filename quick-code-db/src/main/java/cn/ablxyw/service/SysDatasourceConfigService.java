package cn.ablxyw.service;


import cn.ablxyw.entity.SysDatasourceConfigEntity;
import cn.ablxyw.vo.DataSourcePassword;
import cn.ablxyw.vo.ResultEntity;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * 数据源配置Service
 *
 * @author weiqiang
 * @date 2020-01-14 11:28:44
 */
public interface SysDatasourceConfigService extends BaseInfoService<SysDatasourceConfigEntity, String> {
    /**
     * 测试数据源连接
     *
     * @param sysDatasourceConfig 数据源配置
     * @return ResultEntity
     */
    ResultEntity testConnect(SysDatasourceConfigEntity sysDatasourceConfig);

    /**
     * 修改数据源密码
     *
     * @param dataSourcePasswords 修改数据源密码
     * @return ResultEntity
     */
    @ApiOperation("修改数据源密码")
    ResultEntity updatePassword(List<DataSourcePassword> dataSourcePasswords);

    /**
     * 执行SQL脚本
     *
     * @param sysDatasourceConfig 数据库配置
     * @param fileNames           数据库脚本文件
     * @return ResultEntity
     */
    ResultEntity runScript(SysDatasourceConfigEntity sysDatasourceConfig, String... fileNames);
}
