package cn.ablxyw.mapper;

import cn.ablxyw.config.BaseQueryBuilder;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 指标基础查询
 *
 * @author weiqiang
 * @date 2020-01-10
 */
@Mapper
public interface BaseQueryMapper {

    /**
     * 指标基础查询,有两种写法,推荐第二中,支持Mybatis语法,也能防止SQL注入
     *
     * @param jsonObject 查询参数
     * @return List
     * @Select(value = "<script>${querySql}</script>"),
     * @SelectProvider(type = BaseQueryBuilder.class, method = "baseQueryBuilder")
     */
    @SelectProvider(type = BaseQueryBuilder.class, method = "baseQueryBuilder")
    List<Map<String, Object>> baseQuery(Map<String, Object> jsonObject);

    /**
     * 插入数据
     *
     * @param jsonObject 增加数据参数
     * @return Integer
     */
    @InsertProvider(value = BaseQueryBuilder.class, method = "baseInsertBuilder")
    Integer insert(Map<String, Object> jsonObject);

    /**
     * 更新数据
     *
     * @param jsonObject 更新数据参数
     * @return Integer
     */
    @UpdateProvider(value = BaseQueryBuilder.class, method = "baseUpdateBuilder")
    Integer update(Map<String, Object> jsonObject);

    /**
     * 删除数据
     *
     * @param jsonObject 删除据参数
     * @return Integer
     */
    @DeleteProvider(value = BaseQueryBuilder.class, method = "baseDeleteBuilder")
    Integer delete(Map<String, Object> jsonObject);
}
