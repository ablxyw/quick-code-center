package cn.ablxyw.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @description:
 * @author: yxp
 * @create: 2021-03-26
 **/

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TableApiConfigs implements Serializable {

    private static final long serialVersionUID = -2896045670268888373L;

    /**
     * 是否可用
     */
    private boolean enable;
    /**
     * api接口key
     */
    private String apiKey;

    /**
     * 主键表示
     */
    private String primaryKey;
    /**
     * 表字段名
     */
    private String columnEn;
    /**
     * 表字段注释
     */
    private String columnChn;
    /**
     * 接口样例数据
     */
    private String apiData;
    /**
     * 表样例数据
     */
    private String tableData;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 脚本id
     */
    private String scriptId;


}
