package cn.ablxyw.service.impl.factory.impl;

import cn.ablxyw.entity.TableApiConfigs;
import cn.ablxyw.service.AbstractDbService;
import cn.hutool.core.date.DateUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Mysql数据源操作
 *
 * @author yxp
 * @date 2021-03-26
 **/
public class MysqlServiceImpl extends AbstractDbService {

    /**
     * 查询数据源所有表信息sql
     *
     * @return String
     */
    @Override
    public String getTablesSql() {
        String sql = "SELECT TABLE_NAME tableName, TABLE_COMMENT comments FROM INFORMATION_SCHEMA.TABLES t WHERE t.TABLE_SCHEMA=(SELECT DATABASE()) AND t.TABLE_TYPE='BASE TABLE' ORDER BY t.CREATE_TIME DESC";
        return sql;
    }

    /**
     * 查询表所有列信息sql
     *
     * @param tableName 数据源表名
     * @return String
     */
    @Override
    public String getColumnsSql(String tableName) {
        String sql = "select t.COLUMN_KEY primaryKey,t.COLUMN_NAME columnEn,t.COLUMN_COMMENT columnChn,t.DATA_TYPE dataType,t.COLUMN_TYPE dataLength,t.COLUMN_DEFAULT defaultValue from INFORMATION_SCHEMA.Columns t where table_name='" + tableName + "' and TABLE_SCHEMA=(SELECT DATABASE()) ORDER BY t.ORDINAL_POSITION ASC";
        return sql;
    }

    /**
     * 查询表样例数据sql
     *
     * @param tableName 数据源表名
     * @return String
     */
    @Override
    public String getDataSql(String tableName) {
        String sql = "select * from " + tableName + " limit 1";
        return sql;
    }

    /**
     * 添加批次字段
     *
     * @param tableName 数据源表名
     * @return String
     */
    @Override
    public String addBatchColumn(String tableName) {
        String sql = "ALTER TABLE " + tableName + " ADD " + Q_BATCH_TIME + " varchar(30) COMMENT '数据批次'";
        return sql;
    }

    /**
     * 批量添加数据
     *
     * @param tableName       数据源表名
     * @param tableApiConfigs 表接口配置
     * @param isIgnore        是否忽略插入
     * @return String
     */
    @Override
    public String batchInsertSql(String tableName, List<TableApiConfigs> tableApiConfigs, boolean isIgnore) {
        StringBuffer sb = new StringBuffer();
        sb.append("insert ");
        if (isIgnore) {
            sb.append("ignore ");
        }
        sb.append("into ").append(tableName).append("(");
        tableApiConfigs.stream().forEach(config -> {
            if (config.isEnable()) {
                sb.append(config.getColumnEn()).append(",");
            }
        });
        //添加批次字段
        sb.append(Q_BATCH_TIME).append(") values <foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\">").append("(");
        tableApiConfigs.stream().forEach(config -> {
            sb.append("#{item.").append(config.getApiKey()).append("}").append(",");
        });
        final String now = DateUtil.now();
        sb.append("'").append(now).append("')</foreach>");
        return sb.toString();
    }

    /**
     * 批量更新数据
     *
     * @param tableName       数据源表名
     * @param tableApiConfigs 表接口配置
     * @return String
     */
    @Override
    public String batchUpdateSql(String tableName, List<TableApiConfigs> tableApiConfigs) {
        StringBuffer sb = new StringBuffer();
        //默认主键名=id
        AtomicReference<String> primaryKey = new AtomicReference<>("id");
        AtomicReference<String> primaryValue = new AtomicReference<>("id");
        sb.append("<foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\";\">UPDATE ")
                .append(tableName)
                .append(" <set> ");
        tableApiConfigs.stream().forEach(config -> {
            if (config.isEnable()) {
                sb.append(" <if test=\"item.").append(config.getApiKey()).append("!= null\"> `")
                        .append(config.getColumnEn()).append("` = #{item.").append(config.getApiKey()).append("},</if>");
            }
            if (PrimaryKeyEnum.MYSQL_KEY.getMsg().equalsIgnoreCase(config.getPrimaryKey())) {
                //重新取主键key
                primaryKey.set(config.getColumnEn());
                primaryValue.set(config.getApiKey());
            }
        });
        sb.append(" </set>");
        sb.append(" WHERE `").append(primaryKey.get()).append("`= #{item.").append(primaryValue.get()).append("}</foreach>");
        return sb.toString();
    }


}
