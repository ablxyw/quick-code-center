package cn.ablxyw.service.impl.factory.impl;

import cn.ablxyw.entity.TableApiConfigs;
import cn.ablxyw.service.AbstractDbService;
import cn.hutool.core.date.DateUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * oracle数据源操作
 *
 * @author yxp
 * @date 2021-03-26
 **/
public class OracleServiceImpl extends AbstractDbService {

    /**
     * 查询数据源所有表信息sql
     *
     * @return String
     */
    @Override
    public String getTablesSql() {
        String sql = "SELECT T.TABLE_NAME tableName,T.COMMENTS comments FROM USER_TAB_COMMENTS T WHERE TABLE_TYPE = 'TABLE' ORDER BY T.TABLE_NAME ASC";
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
        String sql = "SELECT DECODE(p.constraint_name, 'TF_B_AIR_CONFIG_PK', 'PRI', '') primaryKey,T.COLUMN_NAME columnEn,c.comments columnChn, T.DATA_TYPE dataType,T.DATA_LENGTH dataLength,T.DATA_DEFAULT defaultValue FROM USER_TAB_COLUMNS T left join user_col_comments C on c.COLUMN_NAME=t.COLUMN_NAME and c.TABLE_NAME=t.TABLE_NAME left join user_cons_columns P on p.COLUMN_NAME=t.COLUMN_NAME and p.TABLE_NAME=t.TABLE_NAME WHERE T.TABLE_NAME=" + tableName + " ORDER BY t.COLUMN_NAME asc";
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
        String sql = "select * from (select * from " + tableName + ") where rownum = 1";
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
        String sql = "ALTER TABLE " + tableName + " ADD " + Q_BATCH_TIME + " varchar2(30) default '数据批次'";
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
        String ignoreKey = "";
        StringBuffer sb = new StringBuffer();
        sb.append("insert ");
        if (isIgnore) {
            sb.append("/*+ IGNORE_ROW_ON_DUPKEY_INDEX(").append(tableName).append("(").append(ignoreKey).append(")) */ ");
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
            if (PrimaryKeyEnum.ORACLE_KEY.getMsg().equalsIgnoreCase(config.getPrimaryKey())) {
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
