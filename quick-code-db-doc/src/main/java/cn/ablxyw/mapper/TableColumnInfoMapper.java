package cn.ablxyw.mapper;

import cn.ablxyw.entity.TableColumnInfoEntity;
import cn.ablxyw.entity.TableInfoEntity;

import java.util.List;

/**
 * 表信息Mapper
 *
 * @author weiqiang
 * @email weiqiang@ablxyw.cn
 * @date 2020-03-14 10:59:59
 */
public interface TableColumnInfoMapper extends QuickBaseMapper<TableColumnInfoEntity, String> {

    /**
     * 查询表信息
     *
     * @param tableInfoEntity 表信息
     * @return List<TableInfoEntity>
     */
    List<TableInfoEntity> listTableInfo(TableInfoEntity tableInfoEntity);
}
