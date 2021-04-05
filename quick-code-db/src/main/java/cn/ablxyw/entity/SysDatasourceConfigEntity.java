package cn.ablxyw.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

import static cn.ablxyw.constants.GlobalConstants.DATE_TIME_FORMAT;


/**
 * 数据源配置
 *
 * @author weiqiang
 * @date 2020-01-14 11:28:44
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"datasourceId"})
@ApiModel("数据源配置")
@TableName("sys_datasource_config")
public class SysDatasourceConfigEntity extends QuickBaseUserInfo implements Serializable {

    private static final long serialVersionUID = 1840352483088563230L;
    /**
     * 数据源的id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "数据源的id")
    private String datasourceId;
    /**
     * 应用
     */
    @NotBlank(message = "所属应用不能为空!")
    @ApiModelProperty(value = "所属应用")
    private String appId;
    /**
     * 数据源名称
     */
    @ApiModelProperty(value = "数据源名称")
    @NotBlank(message = "数据源名称不能为空")
    @Length(max = 255, message = "数据源名称不能多于255个字符")
    private String datasourceName;

    /**
     * 连接信息
     */
    @NotBlank(message = "连接信息不能为空")
    @Length(max = 400, message = "连接信息不能多于400个字符")
    @ApiModelProperty(value = "连接信息")
    private String url;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Length(max = 255, message = "用户名不能多于255个字符")
    @ApiModelProperty(value = "用户名")
    private String userName;

    /**
     * 密码
     */
    @Length(max = 255, message = "密码不能多于255个字符")
    @ApiModelProperty(value = "密码")
    private String passWord;

    /**
     * 暂留字段
     */
    @Length(max = 255, message = "暂留字段不能多于255个字符")
    @ApiModelProperty(value = "暂留字段")
    private String code;

    /**
     * 数据库类型
     */
    @NotBlank(message = "数据库类型不能为空")
    @Length(max = 255, message = "数据库类型不能多于255个字符")
    @ApiModelProperty(value = "数据库类型")
    private String databaseType;

    /**
     * 初始化时建立物理连接的个数,初始化发生在显示调用init方法
     */
    @NotNull(message = "初始化时建立物理连接的个数不能为空")
    @Min(value = 1, message = "初始化时建立物理连接的个数不能小于1")
    @ApiModelProperty(value = "初始化时建立物理连接的个数")
    private Integer initialSize;

    /**
     * 最大连接池数量
     */
    @NotNull(message = "最大连接池数量不能为空")
    @Min(value = 10, message = "最大连接池数量不能小于10")
    @ApiModelProperty(value = "最大连接池数量")
    private Integer maxActive;

    /**
     * 最小连接池数量
     */
    @NotNull(message = "最小连接池数量不能为空")
    @Min(value = 5, message = "最小连接池数量不能小于5")
    @ApiModelProperty(value = "最小连接池数量")
    private Integer minIdle;

    /**
     * 获取连接时最大等待时间,单位毫秒
     */
    @NotNull(message = "获取连接时最大等待时间,单位毫秒不能为空!")
    @ApiModelProperty(value = "获取连接时最大等待时间,单位毫秒")
    private Integer maxWait;

    /**
     * 备注
     */
    @Length(max = 500, message = "备注不能多于500个字符")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 防火墙
     */
    @Length(max = 500, message = "防火墙不能多于500个字符")
    @ApiModelProperty(value = "防火墙")
    private String filters;

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

    /**
     * 驱动类
     */
    @TableField(exist = false)
    @JsonIgnore
    @ApiModelProperty(value = "驱动类", hidden = true)
    private String driverClassName;
}
