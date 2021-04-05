package cn.ablxyw.util;

import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.vo.ResultEntity;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

/**
 * 数据源Util
 *
 * @author weiqiang
 * @date 2021-04-04 下午7:45
 */
@Slf4j
public class DataBaseUtil {
    /**
     * 根据数据库url或数据库类型获取驱动类
     *
     * @param url          数据库url
     * @param databaseType 数据库类型
     * @return 驱动类
     */
    public static String getDriverClassNameByUrlOrDatabaseType(String url, String databaseType) {
        log.info("数据库url:{}，数据库类型:{}", url, databaseType);
        String driveClass;
        try {
            driveClass = JdbcUtils.getDriverClassName(url);
        } catch (SQLException e) {
            log.error("通过url获取驱动类出错:{}", e.getMessage());
            driveClass = driveClass(databaseType);
        }
        return driveClass;
    }

    /**
     * 根据数据库类型获取驱动包
     *
     * @param databaseType 数据库类型
     * @return 驱动类
     */
    public static String driveClass(String databaseType) {
        String driveClass;
        switch (databaseType) {
            case JdbcConstants.ORACLE:
                driveClass = JdbcConstants.ORACLE_DRIVER2;
                break;
            case JdbcConstants.POSTGRESQL:
                driveClass = JdbcConstants.POSTGRESQL_DRIVER;
                break;
            default:
                driveClass = JdbcConstants.MYSQL_DRIVER_6;
                break;
        }
        return driveClass;
    }

    /**
     * 分页返回
     *
     * @param globalEnum 信息
     * @param pageInfo   分页信息
     * @return ResultEntity
     */
    public static ResultEntity success(GlobalEnum globalEnum, Page pageInfo) {
        return ResultEntity.builder().message(globalEnum.getMessage()).success(true).data(pageInfo.getRecords()).pageable(true).total(pageInfo.getTotal()).build();
    }
}
