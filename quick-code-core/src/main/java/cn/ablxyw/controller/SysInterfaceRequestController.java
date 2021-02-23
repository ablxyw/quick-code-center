package cn.ablxyw.controller;

import cn.ablxyw.entity.SysInterfaceRequestEntity;
import cn.ablxyw.service.SysInterfaceRequestService;
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
 * 接口请求日志
 *
 * @author weiqiang
 * @email weiq0525@gmail.com
 * @date 2020-02-16 18:11:59
 */
@Api(value = "接口请求日志Api", tags = "接口请求日志Api")
@RestController
@RequestMapping("/interfaceRequest")
public class SysInterfaceRequestController {

    /**
     * 接口请求日志Service
     */
    @Autowired
    private SysInterfaceRequestService sysInterfaceRequestService;

    /**
     * 分页查询接口请求日志
     *
     * @param sysInterfaceRequest 接口请求日志
     * @param pageNumber          初始页
     * @param pageSize            每页条数
     * @param sortName            排序信息
     * @param sortOrder           排序顺序
     * @return ResultEntity
     */
    @ApiOperation("分页查询接口请求日志")
    @RequestMapping(value = "/listByPage", method = RequestMethod.GET)
    public ResultEntity list(SysInterfaceRequestEntity sysInterfaceRequest, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "30") Integer pageSize, String sortName, String sortOrder) {
        return sysInterfaceRequestService.list(sysInterfaceRequest, pageNumber, pageSize, sortName, sortOrder);
    }

    /**
     * 查询接口请求日志
     *
     * @param sysInterfaceRequest 接口请求日志
     * @return ResultEntity
     */
    @ApiOperation("查询接口请求日志")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultEntity list(SysInterfaceRequestEntity sysInterfaceRequest) {
        return sysInterfaceRequestService.list(sysInterfaceRequest);
    }

    /**
     * 新增接口请求日志
     *
     * @param sysInterfaceRequest 接口请求日志
     * @return ResultEntity
     */
    @ApiOperation("新增接口请求日志")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public ResultEntity insert(@Valid @RequestBody SysInterfaceRequestEntity sysInterfaceRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        List<SysInterfaceRequestEntity> sysInterfaceRequestList = new ArrayList<SysInterfaceRequestEntity>(1) {{
            add(sysInterfaceRequest);
        }};
        return sysInterfaceRequestService.insert(sysInterfaceRequestList);
    }

    /**
     * 根据主键requestId查询接口请求日志详情
     *
     * @param requestId 主键
     * @return ResultEntity
     */
    @ApiOperation("根据主键requestId查询接口请求日志详情")
    @RequestMapping(path = "{requestId}", method = RequestMethod.GET)
    public ResultEntity listById(@PathVariable String requestId) {
        return sysInterfaceRequestService.list(SysInterfaceRequestEntity.builder().requestId(requestId).build());
    }

    /**
     * 修改接口请求日志
     *
     * @param sysInterfaceRequest 接口请求日志
     * @return ResultEntity
     */
    @ApiOperation("修改接口请求日志")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultEntity update(@Valid @RequestBody SysInterfaceRequestEntity sysInterfaceRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        List<SysInterfaceRequestEntity> sysInterfaceRequestList = new ArrayList<SysInterfaceRequestEntity>(1) {{
            add(sysInterfaceRequest);
        }};
        return sysInterfaceRequestService.update(sysInterfaceRequestList);
    }

    /**
     * 删除接口请求日志
     *
     * @param requestId
     * @return ResultEntity
     */
    @ApiOperation("删除接口请求日志")
    @RequestMapping(path = "{requestId}", method = RequestMethod.DELETE)
    public ResultEntity batchRemove(@PathVariable String requestId) {
        List<String> requestIdList = new ArrayList<String>(1) {{
            add(requestId);
        }};
        return sysInterfaceRequestService.delete(requestIdList);
    }

    /**
     * 根据主键集合接口请求日志
     *
     * @param objectInfo 主键集合
     * @return ResultEntity
     */
    @ApiOperation("根据主键集合接口请求日志")
    @RequestMapping(value = "deleteByIds", method = RequestMethod.POST)
    public ResultEntity delete(@Valid @RequestBody ObjectInfo<String> objectInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return sysInterfaceRequestService.delete(objectInfo.getIds());
    }

}
