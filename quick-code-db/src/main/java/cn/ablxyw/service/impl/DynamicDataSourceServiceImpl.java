package cn.ablxyw.service.impl;

import cn.ablxyw.config.DataBaseContextHolder;
import cn.ablxyw.config.DynamicDataSource;
import cn.ablxyw.config.QueryConfigDict;
import cn.ablxyw.entity.SysDatasourceConfigEntity;
import cn.ablxyw.mapper.SysDatasourceConfigMapper;
import cn.ablxyw.service.DynamicDataSourceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 多数据源切换Service
 *
 * @author weiqiang
 * @date 2020-01-14 11:28:44
 */
@Slf4j
@Service("dynamicDataSourceService")
public class DynamicDataSourceServiceImpl implements DynamicDataSourceService {

    /**
     * 数据源配置Mapper
     */
    @Resource
    private SysDatasourceConfigMapper sysDatasourceConfigMapper;

    /**
     * 动态数据源配置
     */
    @Autowired
    private DynamicDataSource dynamicDataSource;

    /**
     * 查询数据源配置
     *
     * @param sysDatasourceConfigEntity 数据源配置
     * @return List
     */
    @Override
    public List<SysDatasourceConfigEntity> list(SysDatasourceConfigEntity sysDatasourceConfigEntity) {
        return sysDatasourceConfigMapper.selectList(convertWrapper(sysDatasourceConfigEntity));
    }

    /**
     * 切换数据源是否成功
     *
     * @param datasourceId 数据源Id
     * @return boolean
     * @throws Exception
     */
    @Override
    public boolean changeDb(String datasourceId) throws Exception {
        //如果数据源没有被初始化则先初始化
        boolean initFlag = QueryConfigDict.getSysDatasourceConfigList().isEmpty() || !(QueryConfigDict.getSysDatasourceConfigList().stream()
                .map(SysDatasourceConfigEntity::getDatasourceId)
                .distinct().collect(Collectors.toList())
                .contains(datasourceId));
        if (initFlag) {
            QueryConfigDict.setSysDatasourceConfigList(list(null));
        }
        //默认切换到主数据源,进行整体资源的查找
        DataBaseContextHolder.clearDataSource();
        List<SysDatasourceConfigEntity> dataSourcesList = QueryConfigDict.getSysDatasourceConfigList();
        for (SysDatasourceConfigEntity dataSource : dataSourcesList) {
            if (Objects.equals(datasourceId, dataSource.getDatasourceId())) {
                log.info("需要使用的的数据源已经找到,datasourceId是:{}", dataSource.getDatasourceId());
                //创建数据源连接&检查 若存在则不需重新创建
                dynamicDataSource.createDataSourceWithCheck(dataSource);
                //切换到该数据源
                DataBaseContextHolder.setDataSource(dataSource.getDatasourceId());
                return true;
            }
        }
        return false;
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
        return new QueryWrapper<>(sysDatasourceConfig);
    }


}
