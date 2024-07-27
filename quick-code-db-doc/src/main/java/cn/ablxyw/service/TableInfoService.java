package cn.ablxyw.service;

import cn.ablxyw.entity.TableColumnInfoEntity;
import cn.ablxyw.entity.TableInfoEntity;
import cn.ablxyw.vo.ResultEntity;

/**
 * 表信息Service
 *
 * @author weiqiang
 * @date 2020-03-14 4:21 下午
 */
public interface TableInfoService {

    /**
     * 表信息
     *
     * @param tableInfoEntity 表信息
     * @return ResultEntity
     */
    ResultEntity list(TableInfoEntity tableInfoEntity);

    /**
     * 表列信息
     *
     * @param tableColumnInfoEntity 表列信息
     * @return ResultEntity
     */
    ResultEntity listColumnInfo(TableColumnInfoEntity tableColumnInfoEntity);

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
    ResultEntity listColumnInfo(TableColumnInfoEntity tableColumnInfoEntity, Integer pageNum, Integer pageSize, String sortName, String sortOrder);
}
