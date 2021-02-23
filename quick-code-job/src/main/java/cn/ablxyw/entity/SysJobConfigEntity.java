package cn.ablxyw.entity;

import cn.ablxyw.constants.GlobalConstants;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.quartz.JobDataMap;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;


/**
 * 分布式定时任务配置类
 *
 * @author weiqiang
 * @program: SysQuartzEntity
 * @create: 2021-01-29
 **/
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"jobGroup", "jobName"})
@ApiModel("分布式定时任务配置类")
public class SysJobConfigEntity implements Serializable {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    private String id;

    /**
     * 任务名称
     */
    @Length(max = 255, message = "任务名称不能多于255个字符")
    @NotBlank(message = "任务名称不能为空!")
    @ApiModelProperty(value = "任务名称")
    private String jobName;

    /**
     * 任务组
     */
    @Length(max = 255, message = "任务组不能多于255个字符")
    @NotBlank(message = "任务组不能为空!")
    @ApiModelProperty(value = "任务组")
    private String jobGroup;

    /**
     * 任务执行类
     */
    @Length(max = 255, message = "任务执行类不能多于255个字符")
    @NotBlank(message = "任务执行类不能为空!")
    @ApiModelProperty(value = "任务执行类")
    private String jobClass;

    /**
     * 任务状态 启动还是暂停
     */
    @ApiModelProperty(value = "任务状态:启动或暂停")
    private Integer status;

    /**
     * 任务开始时间
     */
    @ApiModelProperty(value = "任务开始时间")
    @DateTimeFormat(pattern = GlobalConstants.DATE_TIME_FORMAT)
    @JsonFormat(pattern = GlobalConstants.DATE_TIME_FORMAT, timezone = "GMT+8")
    private Date startTime;

    /**
     * 任务循环间隔-单位：分钟
     */
    @ApiModelProperty(value = "任务循环间隔(单位:分钟)")
    private Integer interval;

    /**
     * 任务结束时间
     */
    @ApiModelProperty(value = "任务开始时间")
    @DateTimeFormat(pattern = GlobalConstants.DATE_TIME_FORMAT)
    @JsonFormat(pattern = GlobalConstants.DATE_TIME_FORMAT, timezone = "GMT+8")
    private Date endTime;

    /**
     * 任务运行时间表达式
     */
    @Length(max = 255, message = "任务运行时间表达式不能多于255个字符")
    @ApiModelProperty(value = "任务运行时间表达式")
    private String cronExpression;

    /**
     * 任务数据Map
     */
    @ApiModelProperty(value = "任务数据Map")
    private String jobDataMap;

    /**
     * 任务数据Map
     */
    @ApiModelProperty(value = "任务数据Map")
    private JobDataMap dataMap;

    /**
     * 备注
     */
    @Length(max = 500, message = "备注不能多于500个字符")
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 插入时间
     */
    @DateTimeFormat(pattern = GlobalConstants.DATE_TIME_FORMAT)
    @JsonFormat(pattern = GlobalConstants.DATE_TIME_FORMAT, timezone = "GMT+8")
    @ApiModelProperty(value = "插入时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = GlobalConstants.DATE_TIME_FORMAT)
    @JsonFormat(pattern = GlobalConstants.DATE_TIME_FORMAT, timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 排序信息:字段 顺序
     */
    @JsonIgnore
    @ApiModelProperty(value = "排序字段", hidden = true)
    private String sort;

    /**
     * 运行状态
     */
    @ApiModelProperty(value = "运行状态")
    private String runStatus;

    /**
     * 上次执行时间
     */
    @ApiModelProperty(value = "上次执行时间")
    private Long prevFireTime;
    /**
     * 下次执行时间
     */
    @ApiModelProperty(value = "下次执行时间")
    private Long nextFireTime;
    /**
     * 重试次数
     */
    @ApiModelProperty(value = "重试次数")
    private Long repeatCount;
    /**
     * 执行次数
     */
    @ApiModelProperty(value = "执行次数")
    private Long timesTriggered;

    /**
     * 获取JobDataMap
     *
     * @return JobDataMap
     */
    public JobDataMap getDataMap() {
        JobDataMap map = new JobDataMap();
        if (StringUtils.isNotBlank(this.getJobDataMap())) {
            Map jsonObject = JSON.parseObject(this.getJobDataMap(), Map.class);
            map.putAll(jsonObject);
        }
        return map;
    }

    /**
     * getJobGroup
     *
     * @return String
     */
    public String getJobGroup() {
        return StringUtils.isNotEmpty(jobGroup) ? jobGroup : null;
    }
}
