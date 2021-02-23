package cn.ablxyw.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

import static cn.ablxyw.constants.GlobalConstants.DATE_TIME_FORMAT;

/**
 * 系统用户
 *
 * @author weiqiang
 * @date 2020-03-31 3:03 下午
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"userId"})
@ApiModel("系统用户")
public class SysUserInfo implements Serializable {
    private static final long serialVersionUID = 7226400711721264983L;
    /**
     * 用户主键
     */
    @ApiModelProperty(value = "用户主键")
    private String userId;
    /**
     * 登陆ip
     */
    @ApiModelProperty(value = "登陆ip")
    private String ipAddress;
    /**
     * 登陆次数
     */
    @ApiModelProperty(value = "登陆次数")
    private Integer loginCount;
    /**
     * 用户登陆名称
     */
    @ApiModelProperty(value = "用户登陆名称")
    @NotBlank(message = "用户登陆名称不能为空!")
    private String loginName;
    /**
     * 登陆密码
     */
    @ApiModelProperty(value = "登陆密码")
    @NotBlank(message = "登陆密码不能为空!")
    private String password;
    /**
     * 状态
     * 0：不可用
     * 1：可用
     */
    @ApiModelProperty(value = "状态")
    private Boolean status;
    /**
     * 首次登陆时间
     */
    @ApiModelProperty(value = "首次登陆时间")
    private Date firstLogin;
    /**
     * 添加时间
     */
    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = "GMT+8")
    @ApiModelProperty(value = "添加时间")
    private Date insertTime;
    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    /**
     * 用户类型
     * 0：管理员
     * 1：一般用户
     * 2：其他
     */
    @ApiModelProperty(value = "用户类型")
    private Integer userType;

    /**
     * 是否在线
     */
    @ApiModelProperty(value = "是否在线")
    private Boolean online;

    /**
     * 登录时间
     */
    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = "GMT+8")
    @ApiModelProperty(value = "登录时间")
    private Date issuedAt;

    /**
     * 排序字段
     */
    @JsonIgnore
    @ApiModelProperty(value = "排序字段", hidden = true)
    private String sort;

}
