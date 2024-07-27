package cn.ablxyw.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.WebStatFilter;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * 配置默认的数据源，配置Druid数据库连接池，配置sql工厂加载mybatis的文件，扫描实体类
 *
 * @author weiqiang
 * @date 2020-01-14 11:28:44
 */
@Slf4j
@Configuration
@EnableTransactionManagement
public class DruidConfig {
    /**
     * 数据库连接信息
     */
    @Value("${spring.datasource.url}")
    private String dbUrl;
    /**
     * username
     */
    @Value("${spring.datasource.username}")
    private String username;
    /**
     * password
     */
    @Value("${spring.datasource.password}")
    private String password;
    /**
     * driver-class-name
     */
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    /**
     * 连接池连接信息
     */
    @Value("${spring.datasource.tomcat.initial-size}")
    private int initialSize;
    /**
     * min-idle
     */
    @Value("${spring.datasource.tomcat.min-idle}")
    private int minIdle;
    /**
     * max-active
     */
    @Value("${spring.datasource.tomcat.max-active}")
    private int maxActive;
    /**
     * max-wait
     */
    @Value("${spring.datasource.tomcat.max-wait}")
    private int maxWait;

    /**
     * filter
     */
    @Value("${spring.datasource.tomcat.filter:stat}")
    private String filter;

    /**
     * mybatis mapper
     */
    @Value("${mybatis.mapper-locations}")
    private String mybatisMapperPath;

    /**
     * 初始化主数据源
     *
     * @return DataSource
     * @throws SQLException
     */
    @Bean
    @Primary
    @Qualifier("mainDataSource")
    public DataSource dataSource() throws SQLException {
        DruidDataSource datasource = new DruidDataSource();
        //基础连接信息
        datasource.setUrl(dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        //连接池连接信息
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);

        //是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。
        datasource.setPoolPreparedStatements(true);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(50);

        //对于耗时长的查询sql，会受限于ReadTimeout的控制，单位毫秒
        datasource.setConnectionProperties("druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000");

        //申请连接时执行validationQuery检测连接是否有效，这里建议配置为TRUE，防止取到的连接不可用
        datasource.setTestOnBorrow(true);

        //建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
        datasource.setTestWhileIdle(true);

        String validationQuery = "select 1 from dual";
        //用来检测连接是否有效的sql，要求是一个查询语句。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。
        datasource.setValidationQuery(validationQuery);

        //属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有：监控统计用的filter:stat日志用的filter:log4j防御sql注入的filter:wall
        datasource.setFilters(filter);

        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        datasource.setTimeBetweenEvictionRunsMillis(60000);

        //配置一个连接在池中最小生存的时间，单位是毫秒，这里配置为3分钟180000
        datasource.setMinEvictableIdleTimeMillis(180000);

        //打开druid.keepAlive之后，当连接池空闲时，池中的minIdle数量以内的连接，空闲时间超过minEvictableIdleTimeMillis，则会执行keepAlive操作，即执行druid.validationQuery指定的查询SQL，一般为select * from dual，只要minEvictableIdleTimeMillis设置的小于防火墙切断连接时间，就可以保证当连接空闲时自动做保活检测，不会被防火墙切断
        datasource.setKeepAlive(true);

        //是否移除泄露的连接/超过时间限制是否回收。
        datasource.setRemoveAbandoned(true);

        //泄露连接的定义时间(要超过最大事务的处理时间)；单位为秒。这里配置为1小时
        datasource.setRemoveAbandonedTimeout(3600);

        //移除泄露连接发生是是否记录日志
        datasource.setLogAbandoned(true);
        return datasource;
    }

    /**
     * 注册一个：filterRegistrationBean   druid监控页面配置2-允许页面正常浏览
     *
     * @return filter registration bean
     */
    @Bean
    public FilterRegistrationBean druidStatFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        // 添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");
        // 添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    /**
     * 动态数据源
     *
     * @return DynamicDataSource
     * @throws SQLException
     */
    @Bean(name = "dynamicDataSource")
    @Qualifier("dynamicDataSource")
    public DynamicDataSource dynamicDataSource() throws SQLException {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        //配置缺省的数据源
        // 默认数据源配置 DefaultTargetDataSource
        dynamicDataSource.setDefaultTargetDataSource(dataSource());
        Map<Object, Object> targetDataSources = Maps.newHashMap();
        //额外数据源配置 TargetDataSources
        targetDataSources.put("mainDataSource", dataSource());
        dynamicDataSource.setTargetDataSources(targetDataSources);
        return dynamicDataSource;
    }

    /**
     * 构建 sqlSessionFactory
     *
     * @return sqlSessionFactory
     * @throws Exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
        //解决手动创建数据源后字段到bean属性名驼峰命名转换失效的问题
        sqlSessionFactoryBean.setConfiguration(configuration());
        //设置mybatis的主配置文件
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        //加载mapper下的xml文件
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(mybatisMapperPath));
        return sqlSessionFactoryBean.getObject();
    }


    /**
     * 读取驼峰命名设置
     *
     * @return Configuration
     */
    @Bean
    @ConfigurationProperties(prefix = "mybatis.configuration")
    public org.apache.ibatis.session.Configuration configuration() {
        return new MybatisConfiguration();
    }
}
