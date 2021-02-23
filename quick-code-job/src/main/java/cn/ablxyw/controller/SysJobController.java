package cn.ablxyw.controller;

import cn.ablxyw.entity.SysJobConfigEntity;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.service.SysQuartzService;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ObjectInfo;
import cn.ablxyw.vo.ResultEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 分布式定时任务Controller
 *
 * @author weiqiang
 * @date 2021-01-29
 */
@RestController
@RequestMapping("/sysJobConfig")
@Api(value = "分布式定时任务Controller", tags = "分布式定时任务接口")
public class SysJobController {

    /**
     * 分布式定时任务Service
     */
    @Autowired
    private SysQuartzService sysQuartzService;


    /**
     * 创建定时任务并启动
     *
     * @param sysJobConfigEntity 定时任务
     * @return ResultEntity
     */
    @ApiOperation("创建定时任务并启动")
    @PostMapping(value = "insert")
    public ResultEntity insert(@Valid @RequestBody SysJobConfigEntity sysJobConfigEntity, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return sysQuartzService.insert(sysJobConfigEntity);
    }

    /**
     * 修改分布式定时任务配置
     *
     * @param sysJobConfig 分布式定时任务配置
     * @return ResultEntity
     */
    @ApiOperation("修改分布式定时任务配置")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultEntity update(@Valid @RequestBody SysJobConfigEntity sysJobConfig, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return sysQuartzService.update(sysJobConfig);
    }


    /**
     * 根据任务名称暂停定时任务
     *
     * @return ResultEntity
     */
    @ApiOperation("根据任务名称暂停定时任务")
    @GetMapping(value = {"/pauseJob/{jobName}", "/pauseJob/{jobName}/{jobGroup}"})
    public ResultEntity pauseJob(@PathVariable("jobName") String jobName, @PathVariable(required = false) String
            jobGroup) {
        try {
            return sysQuartzService.pauseScheduleJob(jobName, StringUtils.isNotEmpty(jobGroup) ? jobGroup : null);
        } catch (Exception e) {
            return ResultUtil.error(GlobalEnum.MsgOperationFailed);
        }
    }

    /**
     * 根据任务名称恢复定时任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务组
     * @return String
     */
    @ApiOperation("根据任务名称恢复定时任务")
    @GetMapping(value = {"/resume/{jobName}", "/resume/{jobName}/{jobGroup}"})
    public ResultEntity resume(@PathVariable("jobName") String jobName, @PathVariable(required = false) String
            jobGroup) {
        try {
            return sysQuartzService.resumeScheduleJob(jobName, StringUtils.isNotEmpty(jobGroup) ? jobGroup : null);
        } catch (Exception e) {
            return ResultUtil.error(GlobalEnum.MsgOperationFailed);
        }
    }

    /**
     * 根据任务名称立即运行一次定时任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务组
     * @return String
     */
    @ApiOperation("根据任务名称立即运行一次定时任务")
    @GetMapping(value = {"/runOnce/{jobName}", "/runOnce/{jobName}/{jobGroup}"})
    public ResultEntity runOnce(@PathVariable("jobName") String jobName, @PathVariable(required = false) String jobGroup) {
        return sysQuartzService.runOnce(jobName, StringUtils.isNotEmpty(jobGroup) ? jobGroup : null);
    }

    /**
     * 根据定时任务名称从调度器当中删除定时任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务组
     * @return String
     */
    @ApiOperation("根据定时任务名称从调度器当中删除定时任务")
    @DeleteMapping(value = {"/delete/{jobName}", "/delete/{jobName}/{jobGroup}"})
    public ResultEntity delete(@PathVariable("jobName") String jobName, @PathVariable(required = false) String
            jobGroup) {
        try {
            return sysQuartzService.deleteScheduleJob(jobName, StringUtils.isNotEmpty(jobGroup) ? jobGroup : null);
        } catch (Exception e) {
            return ResultUtil.error(GlobalEnum.DeleteError);
        }
    }

    /**
     * 根据定时任务名称来判断任务是否存在
     *
     * @param jobName  任务名称
     * @param jobGroup 任务组
     * @return String
     */
    @ApiOperation("根据定时任务名称来判断任务是否存在")
    @GetMapping(value = {"/check/{jobName}", "/check/{jobName}/{jobGroup}"})
    public String check(@PathVariable("jobName") String jobName, @PathVariable(required = false) String jobGroup) {
        try {
            if (sysQuartzService.checkExistsScheduleJob(jobName, StringUtils.isNotEmpty(jobGroup) ? jobGroup : null)) {
                return "存在定时任务：" + jobName;
            } else {
                return "不存在定时任务：" + jobName;
            }
        } catch (Exception e) {
            return "查询任务失败";
        }
    }

    /**
     * 定时任务状态
     *
     * @param jobName  任务名称
     * @param jobGroup 任务组
     * @return String
     */
    @ApiOperation("定时任务状态")
    @GetMapping(value = {"/status/{jobName}", "/status/{jobName}/{jobGroup}"})
    @ResponseBody
    public ResultEntity status(@PathVariable("jobName") String jobName, @PathVariable(required = false) String
            jobGroup) {
        try {
            return sysQuartzService.getScheduleJobStatus(jobName, StringUtils.isNotEmpty(jobGroup) ? jobGroup : null);
        } catch (Exception e) {
            return ResultUtil.error(GlobalEnum.QueryError);
        }
    }

    /**
     * 获取所有定时任务
     *
     * @param sysJobConfigEntity 分布式定时任务配置类
     * @return ResultEntity
     */
    @ApiOperation("获取所有定时任务")
    @GetMapping(value = "list")
    public ResultEntity list(SysJobConfigEntity sysJobConfigEntity) {
        try {
            return sysQuartzService.list(sysJobConfigEntity);
        } catch (Exception e) {
            return ResultUtil.error(GlobalEnum.QueryError);
        }
    }

    /**
     * 分页查询分布式定时任务配置
     *
     * @param sysJobConfig 分布式定时任务配置
     * @param pageNumber   初始页
     * @param pageSize     每页条数
     * @param sortName     排序信息
     * @param sortOrder    排序顺序
     * @return ResultEntity
     */
    @ApiOperation("分页查询分布式定时任务配置")
    @RequestMapping(value = "/listByPage", method = RequestMethod.GET)
    public ResultEntity list(SysJobConfigEntity sysJobConfig, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "30") Integer pageSize, String sortName, String sortOrder) {
        return sysQuartzService.list(sysJobConfig, pageNumber, pageSize, sortName, sortOrder);
    }

    /**
     * 删除分布式定时任务配置
     *
     * @param objectInfo 主键集合
     * @return ResultEntity
     */
    @ApiOperation("根据主键集合指标配置实体类(sys_query_config)")
    @RequestMapping(value = "deleteByIds", method = RequestMethod.POST)
    public ResultEntity delete(@Valid @RequestBody ObjectInfo<String> objectInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return sysQuartzService.delete(objectInfo.getIds());
    }
}
