package cn.ablxyw.service.impl;

import cn.ablxyw.entity.TableColumnInfoEntity;
import cn.ablxyw.entity.TableInfoEntity;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.mapper.TableColumnInfoMapper;
import cn.ablxyw.service.TableInfoService;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.PageResultUtil;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 表信息ServiceImpl
 *
 * @author weiqiang
 * @date 2020-03-14 4:26 下午
 */
@Service("tableInfoService")
public class TableInfoServiceImpl implements TableInfoService {

    /**
     * 表信息Mapper
     */
    @Resource
    private TableColumnInfoMapper tableColumnInfoMapper;

    /**
     * 表信息
     *
     * @param tableInfoEntity 表信息
     * @return ResultEntity
     */
    @Override
    public ResultEntity list(TableInfoEntity tableInfoEntity) {
        List<TableInfoEntity> tableInfoEntities = tableColumnInfoMapper.listTableInfo(tableInfoEntity);
        String queryTableName = null, tableSchema = null;

        if (Objects.nonNull(tableInfoEntity)) {
            queryTableName = tableInfoEntity.getTableName();
            tableSchema = tableInfoEntity.getTableSchema();
        }
        TableColumnInfoEntity tableColumnInfoEntity = TableColumnInfoEntity.builder().tableName(queryTableName).tableSchema(tableSchema).build();
        List<TableColumnInfoEntity> columnInfoEntities = tableColumnInfoMapper.list(tableColumnInfoEntity);
        Map<String, List<TableColumnInfoEntity>> tableColumnListMap = columnInfoEntities.stream().collect(Collectors.groupingBy(TableColumnInfoEntity::getTableName));
        tableInfoEntities.stream().forEach(tableInfo -> {
            String tableName = tableInfo.getTableName();
            if (!tableColumnListMap.isEmpty() && tableColumnListMap.containsKey(tableName)) {
                List<TableColumnInfoEntity> tableColumnInfoEntities = tableColumnListMap.getOrDefault(tableName, new ArrayList<>(0))
                        .stream()
                        .sorted(Comparator.comparing(TableColumnInfoEntity::getOrdinalPosition)).collect(Collectors.toList());
                tableInfo.setTableColumnInfoEntities(tableColumnInfoEntities);
            }
        });
        return ResultUtil.success(GlobalEnum.QuerySuccess, tableInfoEntities);
    }

    /**
     * 表列信息
     *
     * @param tableColumnInfoEntity 表列信息
     * @return ResultEntity
     */
    @Override
    public ResultEntity listColumnInfo(TableColumnInfoEntity tableColumnInfoEntity) {
        List<TableColumnInfoEntity> sysQueryConfigList = tableColumnInfoMapper.list(tableColumnInfoEntity);
        return ResultUtil.success(GlobalEnum.QuerySuccess, sysQueryConfigList);
    }

    /**
     * 表列信息
     *
     * @param tableColumnInfoEntity 表列信息
     * @param pageNum               开始页数
     * @param pageSize              每页显示的数据条数
     * @param sortName              排序字段
     * @param sortOrder             排序顺序
     * @return ResultEntity
     */
    @Override
    public ResultEntity listColumnInfo(TableColumnInfoEntity tableColumnInfoEntity, Integer pageNum, Integer pageSize, String sortName, String sortOrder) {
        PageHelper.startPage(pageNum, pageSize);
        String sort = GlobalUtils.changeColumn(sortName, sortOrder);
        tableColumnInfoEntity.setSort(sort);
        List<TableColumnInfoEntity> sysQueryConfigList = tableColumnInfoMapper.list(tableColumnInfoEntity);
        PageInfo pageInfo = new PageInfo(sysQueryConfigList);
        return PageResultUtil.success(GlobalEnum.QuerySuccess, pageInfo);
    }
}
