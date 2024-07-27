package cn.ablxyw.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

import static cn.ablxyw.constants.GlobalConstants.DATE_TIME_FORMAT;

/**
 * html转图配置生成日志
 *
 * @author weiqiang
 * @date 2021-05-26 3:36 下午
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"logId"}, callSuper = false)
@ApiModel("html转图配置生成日志")
@TableName("sys_screenshot_log")
public class SysScreenshotLogEntity extends QuickBaseUserInfo implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "主键")
    private String logId;

    /**
     * 配置ID
     */
    @ApiModelProperty(value = "配置ID")
    private String shotId;
    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    @Length(max = 255, message = "名称不能多于255个字符")
    @ApiModelProperty(value = "名称")
    @TableField(value = "name", condition = SqlCondition.LIKE)
    private String name;
    /**
     * 链接
     */
    @NotBlank(message = "链接不能为空")
    @Length(max = 1000, message = "链接不能多于1000个字符")
    @ApiModelProperty(value = "链接")
    @TableField(value = "url", condition = SqlCondition.LIKE)
    private String url;
    /**
     * 保存文件地址,考虑截屏之后保存至文件服务
     */
    @ApiModelProperty(value = "保存文件地址")
    private String fileUrl;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = "GMT+8")
    private Date updateTime;

    /**
     * 排序字段
     */
    @TableField(exist = false)
    @JsonIgnore
    @ApiModelProperty(value = "排序字段", hidden = true)
    private String sort;

}
