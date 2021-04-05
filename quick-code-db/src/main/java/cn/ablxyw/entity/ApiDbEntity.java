package cn.ablxyw.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author weiqiang
 * @description:
 * @create: 2021-03-26
 **/
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiDbEntity implements Serializable {

    private static final long serialVersionUID = 8061547768198704813L;

    /**
     * 接口id
     */
    private String apiId;

    /**
     * 数据源id
     */
    private String dbId;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 解析函数id
     */
    private String scriptId;

    /**
     * api成功标识-key
     */
    private String successKey;

    /**
     * api成功标识-value
     */
    private String successValue;

    /**
     * 重试次数
     */
    private String retryCount;

    /**
     * 重试间隔 默认单位 分min
     */
    private String retryIntervalTime;

    /**
     * 插入模式: insert 插入、ignoreInsert 忽略插入、update 更新
     */
    private String insertModel;

    /**
     * api-表关系配置
     */
    private List<TableApiConfigs> tableApiConfigs;

}
