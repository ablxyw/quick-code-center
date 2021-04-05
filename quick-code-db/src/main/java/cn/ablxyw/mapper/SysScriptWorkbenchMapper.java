package cn.ablxyw.mapper;


import cn.ablxyw.entity.SysScriptWorkbenchEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 脚本工作台
 *
 * @author weiqiang
 * @date 2020-12-10 23:25:51
 */
public interface SysScriptWorkbenchMapper extends BaseMapper<SysScriptWorkbenchEntity> {

    /**
     * 查询版本名称以及主键
     *
     * @return List
     */
    @Select("SELECT concat(o.name,'(',o.cur_version,')') name ,o.id id from sys_script_workbench o ")
    List<SysScriptWorkbenchEntity> selectVersionNameAndId();

    /**
     * 批量删除
     *
     * @param ids 主键集合
     * @return Integer
     */
    Integer batchDelete(List<String> ids);
}
