package cn.ablxyw.mapper;

import java.io.Serializable;
import java.util.List;

/**
 * 基础Mapper
 *
 * @author weiQiang
 * @date 2020-03-09
 */
public interface QuickBaseMapper<T extends Serializable, E> {

    /**
     * 批量删除
     *
     * @param id 主键集合
     * @return Integer
     */
    Integer batchDelete(List<E> id);


    /**
     * 批量新增
     *
     * @param items 参数s
     * @return Integer
     */
    Integer batchInsert(List<T> items);


    /**
     * 批量更新
     *
     * @param items 参数s
     * @return Integer
     */
    Integer batchUpdate(List<T> items);


    /**
     * 统计
     *
     * @param item 参数
     * @return Integer
     */
    Integer countTotal(T item);


    /**
     * 查询列表
     *
     * @param item 参数
     * @return T
     */
    List<T> list(T item);

    /**
     * 通过主键获取实体
     *
     * @param id 主键
     * @return T
     */
    T findById(E id);


}
