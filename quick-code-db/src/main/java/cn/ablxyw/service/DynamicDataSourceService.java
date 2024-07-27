package cn.ablxyw.service;

import cn.ablxyw.entity.SysDatasourceConfigEntity;

import java.util.List;

/**
 * 多数据源切换Service
 *
 * @author weiqiang
 * @date 2020-01-14 11:28:44
 */
public interface DynamicDataSourceService {

    /**
     * 查询数据源配置
     *
     * @param sysDatasourceConfigEntity 数据源配置
     * @return List
     */
    List<SysDatasourceConfigEntity> list(SysDatasourceConfigEntity sysDatasourceConfigEntity);

    /**
     * 切换数据源是否成功
     *
     * @param datasourceId 数据源Id
     * @return boolean
     * @throws Exception
     */
    boolean changeDb(String datasourceId) throws Exception;
}
