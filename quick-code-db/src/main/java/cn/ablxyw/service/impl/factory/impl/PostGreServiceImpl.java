package cn.ablxyw.service.impl.factory.impl;

import cn.ablxyw.entity.TableApiConfigs;
import cn.ablxyw.service.AbstractDbService;
import cn.hutool.core.date.DateUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * postGre数据源操作
 *
 * @author yxp
 * @date 2021-03-26
 **/
public class PostGreServiceImpl extends AbstractDbService {

    /**
     * 查询数据源所有表信息sql
     *
     * @return String
     */
    @Override
    public String getTablesSql() {
        String sql = "SELECT tablename AS tableName, obj_description ( relfilenode, 'pg_class' ) AS comments FROM pg_tables A, pg_class b  WHERE A.tablename = b.relname  AND A.tablename NOT LIKE'pg%'  AND A.tablename NOT LIKE'sql_%' ORDER BY A.tablename DESC";
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
        String sql = "SELECT DISTINCT a.attnum as num, a.attname as columnEn, format_type(a.atttypid, a.atttypmod) as dataType, a.attnotnull as notnull,  com.description as columnChn,  coalesce(i.indisprimary,false) as primaryKey,  def.adsrc as defaultValue " +
                "FROM pg_attribute a JOIN pg_class pgc ON pgc.oid = a.attrelid LEFT JOIN pg_index i ON  (pgc.oid = i.indrelid AND i.indkey[0] = a.attnum) LEFT JOIN pg_description com on   (pgc.oid = com.objoid AND a.attnum = com.objsubid) LEFT JOIN pg_attrdef def ON (a.attrelid = def.adrelid AND a.attnum = def.adnum) " +
                "WHERE a.attnum > 0 AND pgc.oid = a.attrelid AND pg_table_is_visible(pgc.oid) AND NOT a.attisdropped AND pgc.relname = '" + tableName + "'  ORDER BY a.attnum ";
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
        String sql = "select * from " + tableName + "  limit 1";
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
        String sql = "ALTER TABLE \"public\".\"" + tableName + "\" ADD \"" + Q_BATCH_TIME + "\" varchar(30);" +
                " COMMENT ON COLUMN \"public\".\"" + tableName + "\".\"" + Q_BATCH_TIME + "\" IS '数据批次';";
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
