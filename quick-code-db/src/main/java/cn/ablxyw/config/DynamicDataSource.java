package cn.ablxyw.config;

import cn.ablxyw.entity.SysDatasourceConfigEntity;
import cn.ablxyw.util.DataBaseUtil;
import cn.ablxyw.utils.AesUtil;
import cn.ablxyw.utils.GlobalUtils;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.util.JdbcConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态数据源配置
 *
 * @author weiqiang
 * @date 2020-01-14 11:28:44
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {
    /**
     * 多数据源Mpa
     */
    private Map<Object, Object> dynamicTargetDataSources = new ConcurrentHashMap<>(16);
    /**
     * 数据源信息
     */
    private Object dynamicDefaultTargetDataSource;


    /**
     * Retrieve the current target DataSource
     *
     * @return Object
     */
    @Override
    protected Object determineCurrentLookupKey() {
        String datasource = DataBaseContextHolder.getDataSource();
        if (Objects.nonNull(dynamicTargetDataSources) && !dynamicTargetDataSources.isEmpty()) {
            log.warn("已经初始化的数据源:{}个", dynamicTargetDataSources.size());
        }
        if (!StringUtils.isEmpty(datasource)) {
            Map<Object, Object> dynamicTargetDataSources2 = this.dynamicTargetDataSources;
            if (dynamicTargetDataSources2.containsKey(datasource)) {
                log.info("当前数据源：{}", datasource);
            } else {
                log.info("不存在的数据源：");
                return null;
            }
        } else {
            log.info("当前数据源：默认数据源");
        }
        return datasource;
    }

    /**
     * Specify the map of target DataSources, with the lookup key as key.
     *
     * @param targetDataSources 目标数据源
     */
    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);
        this.dynamicTargetDataSources = targetDataSources;

    }

    /**
     * 初始化数据源
     *
     * @param dataSource 数据源
     * @param driveClass 驱动类
     * @return boolean
     */
    public boolean initDataSource(SysDatasourceConfigEntity dataSource, String driveClass) {
        String key = dataSource.getDatasourceId();
        try {
            // 排除连接不上的错误
            String dataSourceUrl = dataSource.getUrl();
            try {
                Class.forName(driveClass);
                //相当于连接数据库
                int maxWait = Objects.isNull(dataSource.getMaxWait()) ? 1 : dataSource.getMaxWait();
                DriverManager.setLoginTimeout(maxWait);
                DriverManager.getConnection(dataSourceUrl, dataSource.getUserName(), AesUtil.aesDecrypt(dataSource.getPassWord()));
            } catch (Exception e) {
                return false;
            }
            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setName(key);
            druidDataSource.setDriverClassName(driveClass);
            druidDataSource.setUrl(dataSourceUrl);
            druidDataSource.setUsername(dataSource.getUserName());
            druidDataSource.setPassword(AesUtil.aesDecrypt(dataSource.getPassWord()));
            //初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
            druidDataSource.setInitialSize(dataSource.getInitialSize());
            //最大连接池数量
            druidDataSource.setMaxActive(dataSource.getMaxActive());
            //获取连接时最大等待时间，单位毫秒。当链接数已经达到了最大链接数的时候，应用如果还要获取链接就会出现等待的现象，等待链接释放并回到链接池，如果等待的时间过长就应该踢掉这个等待，不然应用很可能出现雪崩现象
            druidDataSource.setMaxWait(dataSource.getMaxWait());
            //最小连接池数量
            druidDataSource.setMinIdle(dataSource.getMinIdle());
            String validationQuery = driveClass.contains(JdbcConstants.ORACLE) ? "select 1 from dual" : "select 1 ";
            //申请连接时执行validationQuery检测连接是否有效，这里建议配置为TRUE，防止取到的连接不可用
            druidDataSource.setTestOnBorrow(true);
            //建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
            druidDataSource.setTestWhileIdle(true);
            //用来检测连接是否有效的sql，要求是一个查询语句。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。
            druidDataSource.setValidationQuery(validationQuery);
            //属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有：监控统计用的filter:stat日志用的filter:log4j防御sql注入的filter:wall
            String filters = dataSource.getFilters();
            druidDataSource.setFilters(StringUtils.isBlank(filters) ? "stat" : filters);
            //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
            druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
            //配置一个连接在池中最小生存的时间，单位是毫秒，这里配置为3分钟180000
            druidDataSource.setMinEvictableIdleTimeMillis(180000);
            //打开druid.keepAlive之后，当连接池空闲时，池中的minIdle数量以内的连接，空闲时间超过minEvictableIdleTimeMillis，则会执行keepAlive操作，即执行druid.validationQuery指定的查询SQL，一般为select * from dual，只要minEvictableIdleTimeMillis设置的小于防火墙切断连接时间，就可以保证当连接空闲时自动做保活检测，不会被防火墙切断
            druidDataSource.setKeepAlive(true);
            //是否移除泄露的连接/超过时间限制是否回收。
            druidDataSource.setRemoveAbandoned(true);
            //泄露连接的定义时间(要超过最大事务的处理时间)；单位为秒。这里配置为1小时
            druidDataSource.setRemoveAbandonedTimeout(3600);
            //移除泄露连接发生是是否记录日志
            druidDataSource.setLogAbandoned(true);
            druidDataSource.init();
            this.dynamicTargetDataSources.put(key, druidDataSource);
            // 将map赋值给父类的TargetDataSources
            this.setTargetDataSources(this.dynamicTargetDataSources);
            // 将TargetDataSources中的连接信息放入resolvedDataSources管理
            super.afterPropertiesSet();
            log.info("数据源:{},初始化成功", key);
            log.debug("数据源:{},概况：{}", key, druidDataSource.dump());
            return true;
        } catch (Exception e) {
            log.error("创建数据源:{},发生错误:{}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 删除数据源
     *
     * @param datasourceId 数据源Id
     * @return boolean
     */
    public boolean delDatasource(String datasourceId) {
        Map<Object, Object> dynamicTargetDataSources2 = this.dynamicTargetDataSources;
        if (StringUtils.isNotBlank(datasourceId) && dynamicTargetDataSources2.containsKey(datasourceId)) {
            Set<DruidDataSource> druidDataSourceInstances = DruidDataSourceStatManager.getDruidDataSourceInstances();
            for (DruidDataSource l : druidDataSourceInstances) {
                if (Objects.equals(l.getName(), datasourceId)) {
                    log.info("删除数据源:{}", datasourceId);
                    dynamicTargetDataSources2.remove(datasourceId);
                    DruidDataSourceStatManager.removeDataSource(l);
                    // 将map赋值给父类的TargetDataSources
                    setTargetDataSources(dynamicTargetDataSources2);
                    // 将TargetDataSources中的连接信息放入resolvedDataSources管理
                    super.afterPropertiesSet();
                    return true;
                }
            }
        }
        log.error("删除数据源:{} 失败!", datasourceId);
        return false;
    }

    /**
     * 刷新数据源
     *
     * @param sysDatasourceConfigList 数据源配置集合
     * @return boolean
     */
    public void refreshDatasource(List<SysDatasourceConfigEntity> sysDatasourceConfigList) {
        sysDatasourceConfigList.stream().forEach(sysDatasourceConfigEntity -> {
            delDatasource(sysDatasourceConfigEntity.getDatasourceId());
            createDataSourceWithCheck(sysDatasourceConfigEntity);
        });
    }

    @Override
    public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        this.dynamicDefaultTargetDataSource = defaultTargetDataSource;
    }

    /**
     * @return the dynamicDefaultTargetDataSource
     */
    public Object getDynamicDefaultTargetDataSource() {
        return dynamicDefaultTargetDataSource;
    }

    /**
     * @param dynamicDefaultTargetDataSource the dynamicDefaultTargetDataSource to set
     */
    public void setDynamicDefaultTargetDataSource(Object dynamicDefaultTargetDataSource) {
        this.dynamicDefaultTargetDataSource = dynamicDefaultTargetDataSource;
    }

    /**
     * 正在检查数据源
     *
     * @param dataSource 数据源
     * @throws Exception
     */
    public void createDataSourceWithCheck(SysDatasourceConfigEntity dataSource) {
        String datasourceId = dataSource.getDatasourceId();
        log.info("正在检查数据源：{}", datasourceId);
        Map<Object, Object> dynamicTargetDataSources2 = this.dynamicTargetDataSources;
        if (dynamicTargetDataSources2.containsKey(datasourceId)) {
            log.info("数据源:{} 之前已经创建，准备测试数据源是否正常..", datasourceId);
            DruidDataSource druidDataSource = (DruidDataSource) dynamicTargetDataSources2.get(datasourceId);
            boolean rightFlag = true;
            Connection connection = null;
            try {
                log.info("数据源:{} 的概况->当前闲置连接数：{}", datasourceId, druidDataSource.getPoolingCount());
                long activeCount = druidDataSource.getActiveCount();
                log.info("数据源:{} 的概况->当前活动连接数：{}", datasourceId, activeCount);
                if (activeCount > 0) {
                    log.info("数据源:{} 的概况->活跃连接堆栈信息：{}", datasourceId, druidDataSource.getActiveConnectionStackTrace());
                }
                log.info("准备获取数据库连接...");
                connection = druidDataSource.getConnection();
                log.info("数据源:{} 正常", datasourceId);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                rightFlag = false;
                log.info("缓存数据源:{} 已失效，准备删除...", datasourceId);
                if (delDatasource(datasourceId)) {
                    log.info("缓存数据源删除成功");
                } else {
                    log.info("缓存数据源删除失败");
                }
            } finally {
                if (null != connection) {
                    try {
                        connection.close();
                    } catch (Exception e) {
                        log.error("关闭数据源连接发生错误:{}", e.getMessage());
                    }
                }
            }
            if (rightFlag) {
                log.info("不需要重新创建数据源");
            } else {
                log.info("准备重新创建数据源...");
                createDataSource(dataSource);
                log.info("重新创建数据源完成");
            }
        } else {
            createDataSource(dataSource);
        }

    }

    /**
     * 创建数据源
     *
     * @param dataSource 数据源
     */
    private void createDataSource(SysDatasourceConfigEntity dataSource) {
        String datasourceId = dataSource.getDatasourceId();
        log.info("准备创建数据源:{}", datasourceId);
        if (Objects.nonNull(dynamicTargetDataSources) && !dynamicTargetDataSources.isEmpty()) {
            log.warn("已经初始化的数据源:{}个", dynamicTargetDataSources.size());
        }
        String databaseType = dataSource.getDatabaseType();
        String username = dataSource.getUserName();
        String password = dataSource.getPassWord();
        String url = dataSource.getUrl();
        String driveClass = DataBaseUtil.getDriverClassNameByUrlOrDatabaseType(url, databaseType);
        if (GlobalUtils.testConnection(datasourceId, driveClass, url, username, password, dataSource.getMaxWait())) {
            boolean result = this.initDataSource(dataSource, driveClass);
            if (!result) {
                log.error("数据源:{},配置正确，但是创建失败", datasourceId);
            }
        } else {
            log.error("数据源配置有错误");
            throw new RuntimeException("数据源配置有错误");
        }
    }

}
