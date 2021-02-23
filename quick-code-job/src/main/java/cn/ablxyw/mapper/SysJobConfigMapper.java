package cn.ablxyw.mapper;

import cn.ablxyw.entity.SysJobConfigEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 分布式定时任务配置
 *
 * @author weiqiang
 * @email weiq0525@gmail.com
 * @date 2021-01-30 17:51:50
 */
@Mapper
public interface SysJobConfigMapper extends BaseMapper<SysJobConfigEntity, String> {

    /**
     * 查询触发器详情
     *
     * @param paramMap 查询参数
     * @return List
     */
    List<Map<String, Object>> jobExecute(Map<String, Object> paramMap);
}
