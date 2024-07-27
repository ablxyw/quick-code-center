package cn.ablxyw.config;

import cn.ablxyw.entity.SysDatasourceConfigEntity;
import cn.ablxyw.service.SysDatasourceConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始化指标配置
 *
 * @author weiqiang
 * @date 2020-01-10
 */
@Slf4j
@Component
public class QueryConfigDict {
    /**
     * 配置key
     */
    public static final String QUERY_CONFIG_KEY = "QUERY_CONFIG_DICT_MAP";
    /**
     * 数据源配置
     */
    public static List<SysDatasourceConfigEntity> SYS_DATASOURCE_CONFIG_LIST = new ArrayList<>(10);
    /**
     * 数据库连接信息
     */
    @Value("${spring.datasource.url}")
    private String dbUrl;
    /**
     * 用户名
     */
    @Value("${spring.datasource.username}")
    private String username;
    /**
     * 密码
     */
    @Value("${spring.datasource.password}")
    private String password;
    /**
     * pidFile
     */
    @Value("${spring.pid.file}")
    private String pidFile;
    /**
     * 请求记录日志数据库Id
     */
    @Value("${qFrame.logDbId:''}")
    private String logDbId;
    /**
     * 数据数据库Id
     */
    @Value("${qFrame.dataDbId:''}")
    private String dataDbId;
    /**
     * 数据数据库用户名
     */
    @Value("${qFrame.dataDb:''}")
    private String dataDb;
    /**
     * 数据数据库Id
     */
    @Value("${qFrame.dataUserName:''}")
    private String dataUserName;
    /**
     * 数据数据库密码
     */
    @Value("${qFrame.dataPassword:''}")
    private String dataPassword;
    /**
     * 数据源配置Service
     */
    @Autowired
    private SysDatasourceConfigService sysDatasourceConfigService;

    /**
     * 获取数据源配置
     *
     * @return List
     */
    public static List<SysDatasourceConfigEntity> getSysDatasourceConfigList() {
        return SYS_DATASOURCE_CONFIG_LIST;
    }

    /**
     * 设置数据源配置
     *
     * @param sysDatasourceConfigList 数据源配置
     */
    public static void setSysDatasourceConfigList(List<SysDatasourceConfigEntity> sysDatasourceConfigList) {
        SYS_DATASOURCE_CONFIG_LIST = sysDatasourceConfigList;
    }
}
