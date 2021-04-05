package cn.ablxyw.service;


import cn.ablxyw.entity.TableApiConfigs;

import java.util.List;

/**
 * @description:不同数据源操作
 * @author: yxp
 * @create: 2021-03-26
 **/
public abstract class AbstractDbService {

    /**
     * 批次字段
     */
    public static final String Q_BATCH_TIME = "q_batch_time";

    /**
     * 查询数据源所有表信息sql
     *
     * @return String
     */
    public abstract String getTablesSql();

    /**
     * 查询表所有列信息sql
     *
     * @param tableName 数据源表名
     * @return String
     */
    public abstract String getColumnsSql(String tableName);

    /**
     * 查询表样例数据sql
     *
     * @param tableName 数据源表名
     * @return String
     */
    public abstract String getDataSql(String tableName);

    /**
     * 添加批次字段
     *
     * @param tableName 数据源表名
     * @return String
     */
    public abstract String addBatchColumn(String tableName);

    /**
     * 批量添加数据
     *
     * @param tableName       数据源表名
     * @param tableApiConfigs 表接口配置
     * @param isIgnore        是否忽略插入
     * @return String
     */
    public abstract String batchInsertSql(String tableName, List<TableApiConfigs> tableApiConfigs, boolean isIgnore);

    /**
     * 批量更新数据
     *
     * @param tableName       数据源表名
     * @param tableApiConfigs 表接口配置
     * @return String
     */
    public abstract String batchUpdateSql(String tableName, List<TableApiConfigs> tableApiConfigs);

    /**
     * db主键表示枚举
     */
    public enum PrimaryKeyEnum {
        //mysql主键key
        MYSQL_KEY("PRI"),
        //oracle 主键key
        ORACLE_KEY("PRI");
        private final String msg;

        PrimaryKeyEnum(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }
    }


    /**
     * db数据导入模式枚举
     */
    public enum ModelInsertEnum {

        //插入模式
        INSERT("insert"),
        //忽略插入模式
        IGNORE_INSERT("ignoreInsert"),
        //更新模式
        UPDATE("update");

        private final String msg;

        ModelInsertEnum(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }
    }

}
