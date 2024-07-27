package cn.ablxyw.controller;

import cn.ablxyw.entity.SysScriptWorkbenchEntity;
import cn.ablxyw.service.SysScriptWorkbenchService;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ObjectInfo;
import cn.ablxyw.vo.ResultEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 脚本工作台
 *
 * @author weiqiang
 * @date 2020-12-10 23:25:51
 */
@Api(value = "脚本工作台Api", tags = "脚本工作台Api")
@RestController
@CrossOrigin
@RequestMapping("/sysScriptWorkbench")
public class SysScriptWorkbenchController {

    /**
     * 脚本工作台Service
     */
    @Autowired
    private SysScriptWorkbenchService sysScriptWorkbenchService;

    /**
     * 分页查询脚本工作台
     *
     * @param sysScriptWorkbench 脚本工作台
     * @param pageNumber         初始页
     * @param pageSize           每页条数
     * @param sortName           排序信息
     * @param sortOrder          排序顺序
     * @return ResultEntity
     */
    @ApiOperation("分页查询脚本工作台")
    @RequestMapping(value = "/listByPage", method = RequestMethod.GET)
    public ResultEntity list(SysScriptWorkbenchEntity sysScriptWorkbench, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "30") Integer pageSize, String sortName, String sortOrder) {
        return sysScriptWorkbenchService.list(sysScriptWorkbench, pageNumber, pageSize, sortName, sortOrder);
    }

    /**
     * 查询脚本工作台
     *
     * @param sysScriptWorkbench 脚本工作台
     * @return ResultEntity
     */
    @ApiOperation("查询脚本工作台")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultEntity list(SysScriptWorkbenchEntity sysScriptWorkbench) {
        return sysScriptWorkbenchService.list(sysScriptWorkbench);
    }

    /**
     * 新增脚本工作台
     *
     * @param sysScriptWorkbench 脚本工作台
     * @return ResultEntity
     */
    @ApiOperation("新增脚本工作台")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public ResultEntity insert(@Valid @RequestBody SysScriptWorkbenchEntity sysScriptWorkbench, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        List<SysScriptWorkbenchEntity> sysScriptWorkbenchList = new ArrayList<SysScriptWorkbenchEntity>(1) {{
            add(sysScriptWorkbench);
        }};
        return sysScriptWorkbenchService.insert(sysScriptWorkbenchList);
    }

    /**
     * 根据主键id查询脚本工作台详情
     *
     * @param id 主键
     * @return ResultEntity
     */
    @ApiOperation("根据主键id查询脚本工作台详情")
    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public ResultEntity listById(@PathVariable String id) {
        return sysScriptWorkbenchService.list(SysScriptWorkbenchEntity.builder().id(id).build());
    }

    /**
     * 修改脚本工作台
     *
     * @param sysScriptWorkbench 脚本工作台
     * @return ResultEntity
     */
    @ApiOperation("修改脚本工作台")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultEntity update(@Valid @RequestBody SysScriptWorkbenchEntity sysScriptWorkbench, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        List<SysScriptWorkbenchEntity> sysScriptWorkbenchList = new ArrayList<SysScriptWorkbenchEntity>(1) {{
            add(sysScriptWorkbench);
        }};
        return sysScriptWorkbenchService.update(sysScriptWorkbenchList);
    }

    /**
     * 删除脚本工作台
     *
     * @param id
     * @return ResultEntity
     */
    @ApiOperation("删除脚本工作台")
    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public ResultEntity batchRemove(@PathVariable String id) {
        List<String> idList = new ArrayList<String>(1) {{
            add(id);
        }};
        return sysScriptWorkbenchService.delete(idList);
    }

    /**
     * 根据主键集合脚本工作台
     *
     * @param objectInfo 主键集合
     * @return ResultEntity
     */
    @ApiOperation("根据主键集合删除脚本工作台")
    @RequestMapping(value = "deleteByIds", method = RequestMethod.POST)
    public ResultEntity delete(@Valid @RequestBody ObjectInfo<String> objectInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return sysScriptWorkbenchService.delete(objectInfo.getIds());
    }

}
