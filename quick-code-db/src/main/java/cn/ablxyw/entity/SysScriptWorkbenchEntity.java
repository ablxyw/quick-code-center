package cn.ablxyw.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

import static cn.ablxyw.constants.GlobalConstants.DATE_TIME_FORMAT;


/**
 * 脚本工作台
 *
 * @author weiqiang
 * @date 2020-12-10 23:25:51
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ApiModel("脚本工作台")
@TableName("sys_script_workbench")
public class SysScriptWorkbenchEntity extends QuickBaseUserInfo implements Serializable {

    private static final long serialVersionUID = 6909106149091352752L;
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    @Length(max = 255, message = "名称不能多于255个字符")
    @ApiModelProperty(value = "名称")
    private String name;
    /**
     * 脚本内容
     */
    @NotBlank(message = "脚本内容不能为空")
    @Length(max = 65535, message = "脚本内容不能多于65535个字符")
    @ApiModelProperty(value = "脚本内容")
    private String content;
    /**
     * 脚本语言
     */
    @NotBlank(message = "脚本语言不能为空")
    @Length(max = 255, message = "脚本语言不能多于255个字符")
    @ApiModelProperty(value = "脚本语言")
    private String scriptMode;
    /**
     * 编辑后版本+1
     */
    @ApiModelProperty(value = "版本")
    private Integer curVersion;
    /**
     * 上一版本ID
     */
    @Length(max = 255, message = "上一版本ID不能多于255个字符")
    @ApiModelProperty(value = "上一版本ID")
    private String oriId;

    /**
     * 上一版本
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "上一版本")
    private String oriVersion;
    /**
     * 是否可用:0:不可用 ；1:可用
     */
    @ApiModelProperty(value = "是否可用:0:不可用 ；1:可用")
    private Boolean status;
    /**
     * 是否公共函数:0:不是 ；1:是
     */
    @ApiModelProperty(value = "是否公共函数:0:不是 ；1:是")
    private Boolean publicScript;
    /**
     * 备注
     */
    @Length(max = 500, message = "备注不能多于500个字符")
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = "GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = "GMT+8")
    private Date updateTime;

    /**
     * 排序信息:字段 顺序
     */
    @TableField(exist = false)
    @JsonIgnore
    @ApiModelProperty(value = "排序字段", hidden = true)
    private String sort;
}
