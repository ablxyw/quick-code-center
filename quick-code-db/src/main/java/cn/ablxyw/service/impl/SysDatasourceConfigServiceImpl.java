package cn.ablxyw.service.impl;

import cn.ablxyw.config.DynamicDataSource;
import cn.ablxyw.config.QueryConfigDict;
import cn.ablxyw.entity.SysDatasourceConfigEntity;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.mapper.SysDatasourceConfigMapper;
import cn.ablxyw.service.SysDatasourceConfigService;
import cn.ablxyw.util.DataBaseUtil;
import cn.ablxyw.utils.AesUtil;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.DataSourcePassword;
import cn.ablxyw.vo.ResultEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.ablxyw.constants.GlobalConstants.INTERVAL_COLON;
import static cn.ablxyw.constants.GlobalConstants.INTERVAL_DB;

/**
 * 数据源配置ServiceImpl
 *
 * @author weiqiang
 * @date 2020-01-14 11:28:44
 */
@Slf4j
@Service("sysDatasourceConfigService")
public class SysDatasourceConfigServiceImpl implements SysDatasourceConfigService {

    /**
     * 数据源配置Mapper
     */
    @Resource
    private SysDatasourceConfigMapper sysDatasourceConfigMapper;

    /**
     * 动态数据源配置
     */
    @Resource
    private DynamicDataSource dynamicDataSource;

    /**
     * 删除所有
     *
     * @return ResultEntity
     */
    @Override
    public ResultEntity batchRemoveAll() {
        return ResultUtil.success(GlobalEnum.DeleteSuccess);
    }

    /**
     * 初始化数据源
     */
    @PostConstruct
    public void init() {
        QueryConfigDict.setSysDatasourceConfigList(convertRecordToMap(null).values().stream().collect(Collectors.toList()));
    }

    /**
     * 数据源配置对象信息Map
     *
     * @param sysDatasourceConfig 查询参数
     * @return Map
     */
    @Override
    public Map<String, SysDatasourceConfigEntity> convertRecordToMap(SysDatasourceConfigEntity sysDatasourceConfig) {
        List<SysDatasourceConfigEntity> sysDatasourceConfigList = sysDatasourceConfigMapper.selectList(convertWrapper(sysDatasourceConfig));
        Map<String, SysDatasourceConfigEntity> sysDatasourceConfigMap = sysDatasourceConfigList.stream().filter(info -> null != info.getDatasourceId())
                .collect(Collectors.toMap(SysDatasourceConfigEntity::getDatasourceId, Function.identity(), (oldValue, newValue) -> newValue));
        return sysDatasourceConfigMap;
    }

    /**
     * 分页查询数据源配置
     *
     * @param sysDatasourceConfig 数据源配置
     * @param pageNum             初始页
     * @param pageSize            每页条数
     * @param sortName            排序字段
     * @param sortOrder           排序顺序
     * @return ResultEntity
     */
    @Override
    public ResultEntity list(SysDatasourceConfigEntity sysDatasourceConfig, Integer pageNum, Integer pageSize, String sortName, String sortOrder) {
        PageHelper.startPage(pageNum, pageSize);
        String sort = GlobalUtils.changeColumn(sortName, sortOrder);
        sysDatasourceConfig.setSort(sort);
        List<SysDatasourceConfigEntity> sysDatasourceConfigEntities = sysDatasourceConfigMapper.selectList(convertWrapper(sysDatasourceConfig));
        PageInfo pageInfo = new PageInfo(sysDatasourceConfigEntities);
        return ResultUtil.success(GlobalEnum.QuerySuccess, pageInfo);
    }

    /**
     * 查询数据源配置
     *
     * @param sysDatasourceConfig 数据源配置
     * @return ResultEntity
     */
    @Override
    public ResultEntity list(SysDatasourceConfigEntity sysDatasourceConfig) {
        List<SysDatasourceConfigEntity> sysDatasourceConfigList = sysDatasourceConfigMapper.selectList(convertWrapper(sysDatasourceConfig));
        return ResultUtil.success(GlobalEnum.QuerySuccess, sysDatasourceConfigList);
    }

    /**
     * 新增数据源配置
     *
     * @param sysDatasourceConfigList 数据源配置集合
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity insert(List<SysDatasourceConfigEntity> sysDatasourceConfigList) {
        AtomicReference<Integer> insertCount = new AtomicReference<>(0);
        sysDatasourceConfigList.stream().forEach(sysDatasourceConfigEntity -> {
            sysDatasourceConfigEntity.setDatasourceId(GlobalUtils.appendString(INTERVAL_DB, GlobalUtils.ordinaryId()));
            sysDatasourceConfigEntity.setPassWord(AesUtil.aesEncrypt(StringUtils.trimToEmpty(sysDatasourceConfigEntity.getPassWord())));
            insertCount.updateAndGet(v -> v + sysDatasourceConfigMapper.insert(sysDatasourceConfigEntity));
        });

        return ResultUtil.msg(insertCount.get());
    }

    /**
     * 修改数据源配置
     *
     * @param sysDatasourceConfigList 数据源配置集合
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity update(List<SysDatasourceConfigEntity> sysDatasourceConfigList) {
        long emptyPkId = sysDatasourceConfigList.stream()
                .filter(sysDatasourceConfig -> Objects.isNull(sysDatasourceConfig.getDatasourceId()))
                .count();
        if (emptyPkId > 0) {
            return ResultUtil.error(GlobalEnum.PkIdEmpty);
        }
        AtomicReference<Integer> updateCount = new AtomicReference<>(0);
        sysDatasourceConfigList.stream().forEach(sysDatasourceConfigEntity -> {
            updateCount.updateAndGet(v -> v + sysDatasourceConfigMapper.updateById(sysDatasourceConfigEntity));
        });
        //修改成功之后动态刷新数据源配置信息
        if (updateCount.get() > 0) {
            dynamicDataSource.refreshDatasource(sysDatasourceConfigList);
        }
        return ResultUtil.msg(updateCount.get());
    }

    /**
     * 删除 数据源配置
     *
     * @param datasourceIdList 数据源的id集合
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity delete(List<String> datasourceIdList) {
        if (Objects.isNull(datasourceIdList) || datasourceIdList.isEmpty() || datasourceIdList.size() < 1) {
            return ResultUtil.error(GlobalEnum.PkIdEmpty);
        }
        Integer deleteCount = sysDatasourceConfigMapper.deleteBatchIds(datasourceIdList);
        if (deleteCount > 0) {
            datasourceIdList.stream().forEach(dynamicDataSource::delDatasource);
        }
        return ResultUtil.msg(deleteCount);
    }

    /**
     * 测试数据源连接
     *
     * @param sysDatasourceConfig 数据源配置
     * @return ResultEntity
     */
    @Override
    public ResultEntity testConnect(SysDatasourceConfigEntity sysDatasourceConfig) {
        String databaseType = sysDatasourceConfig.getDatabaseType();
        String username = sysDatasourceConfig.getUserName();
        String datasourceName = sysDatasourceConfig.getDatasourceName();
        String password = sysDatasourceConfig.getPassWord();
        String url = sysDatasourceConfig.getUrl();
        String driveClass = DataBaseUtil.getDriverClassNameByUrlOrDatabaseType(url, databaseType);
        boolean testDatasource = GlobalUtils.testConnection(datasourceName, driveClass, url, username, password, sysDatasourceConfig.getMaxWait());
        String msg = testDatasource ? GlobalEnum.TestConnectSuccess.getMessage() : GlobalEnum.TestConnectError.getMessage();
        return ResultUtil.booleanFlag(testDatasource, GlobalUtils.appendString(datasourceName, INTERVAL_COLON, msg));
    }

    /**
     * 修改数据源密码
     *
     * @param dataSourcePasswords 修改数据源密码
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity updatePassword(List<DataSourcePassword> dataSourcePasswords) {
        Map<String, SysDatasourceConfigEntity> datasourceConfigEntityMap = convertRecordToMap(null);
        if (datasourceConfigEntityMap.isEmpty() || Objects.isNull(datasourceConfigEntityMap) || datasourceConfigEntityMap.size() < 1) {
            log.error("数据源配置为空，不能修改密码");
            return ResultUtil.error(GlobalEnum.DataEmpty);
        }
        List<SysDatasourceConfigEntity> datasourceConfigEntities = dataSourcePasswords.stream().map(dataSourcePassword -> {
            String datasourceId = dataSourcePassword.getDatasourceId();
            if (!datasourceConfigEntityMap.containsKey(datasourceId) || Objects.isNull(datasourceConfigEntityMap.get(datasourceId))) {
                log.error("该:{}数据源配置找不到", datasourceId);
                GlobalUtils.convertMessage(GlobalEnum.OriDataEmpty);
            }
            SysDatasourceConfigEntity datasourceConfigEntity = datasourceConfigEntityMap.get(datasourceId);
            String oldPassWord = dataSourcePassword.getOldPassWord();
            String oriPassWord = datasourceConfigEntity.getPassWord();
            String datasourceName = datasourceConfigEntity.getDatasourceName();
            if (!Objects.equals(oriPassWord, AesUtil.aesEncrypt(oldPassWord))) {
                log.error("数据源:{},旧密码错误", datasourceName);
                GlobalUtils.convertMessage(GlobalEnum.OldPasswordError, datasourceName);
            }
            String passWord = StringUtils.stripToEmpty(dataSourcePassword.getPassWord());
            return SysDatasourceConfigEntity.builder().datasourceId(datasourceId).passWord(AesUtil.aesEncrypt(passWord)).build();
        }).collect(Collectors.toList());
        return update(datasourceConfigEntities);
    }

    /**
     * 执行SQL脚本
     *
     * @param sysDatasourceConfig 数据库配置
     * @param fileNames           数据库脚本文件
     * @return ResultEntity
     */
    @Override
    public ResultEntity runScript(SysDatasourceConfigEntity sysDatasourceConfig, String... fileNames) {
        try {
            String dbPath = "db" + File.separator;
            String username = sysDatasourceConfig.getUserName();
            String password = sysDatasourceConfig.getPassWord();
            String url = sysDatasourceConfig.getUrl();
            String driveClass = sysDatasourceConfig.getDriverClassName();
            Class.forName(driveClass);
            @Cleanup
            Connection connection = DriverManager.getConnection(url, username, password);
            ScriptRunner runner = new ScriptRunner(connection);
            //设置字符集,不然中文乱码插入错误
            Resources.setCharset(Charset.forName(StandardCharsets.UTF_8.name()));
            //设置是否输出日志
            PrintWriter printWriter = new PrintWriter(System.getProperty("user.dir") + File.separator + "logs" + File.separator + "runScript" + GlobalUtils.ordinaryId() + ".log");
            runner.setLogWriter(printWriter);
            @Cleanup
            Reader read = null;
            for (String fileName : fileNames) {
                // 从class目录下直接读取
                read = Resources.getResourceAsReader(ClassLoader.getSystemClassLoader(), dbPath + fileName);
                runner.runScript(read);
            }
            runner.closeConnection();
        } catch (Exception e) {
            log.error("执行SQL脚本发生错误:", e);
            return ResultUtil.error(e.getMessage());
        }
        return ResultUtil.success(GlobalEnum.MsgOperationSuccess);
    }

    /**
     * 转换请求参数
     *
     * @param sysDatasourceConfig 数据库配置
     * @return QueryWrapper
     */
    private QueryWrapper<SysDatasourceConfigEntity> convertWrapper(SysDatasourceConfigEntity sysDatasourceConfig) {
        if (Objects.isNull(sysDatasourceConfig)) {
            sysDatasourceConfig = SysDatasourceConfigEntity.builder().build();
        }
        QueryWrapper<SysDatasourceConfigEntity> queryWrapper = new QueryWrapper<>(sysDatasourceConfig);
        return queryWrapper;
    }
}
