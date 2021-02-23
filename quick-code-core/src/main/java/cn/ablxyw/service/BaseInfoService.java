package cn.ablxyw.service;

import cn.ablxyw.vo.ResultEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 基础服务接口
 *
 * @author weiQiang
 * @date 2020-01-10
 */
public interface BaseInfoService<T extends Serializable, E> {

    /**
     * 删除所有
     *
     * @return ResultEntity
     */
    ResultEntity batchRemoveAll();

    /**
     * 对象信息Map
     *
     * @param record 查询参数
     * @return Map
     */
    Map<E, T> convertRecordToMap(T record);

    /**
     * 删除对象
     *
     * @param pkIds 对象主键集合
     * @return ResultEntity
     */
    ResultEntity delete(List<E> pkIds);

    /**
     * 增加对象
     *
     * @param records 对象参数
     * @return ResultEntity
     */
    ResultEntity insert(List<T> records);


    /**
     * 根据条件分页查询对象
     *
     * @param record    查询参数
     * @param pageNum   开始页数
     * @param pageSize  每页显示的数据条数
     * @param sortName  排序字段
     * @param sortOrder 排序顺序
     * @return ResultEntity
     */
    ResultEntity list(T record, Integer pageNum, Integer pageSize, String sortName, String sortOrder);


    /**
     * 根据条件查询对象
     *
     * @param record 查询参数
     * @return ResultEntity
     */
    ResultEntity list(T record);


    /**
     * 更新对象
     *
     * @param records 更新参数
     * @return ResultEntity
     */
    ResultEntity update(List<T> records);


}
