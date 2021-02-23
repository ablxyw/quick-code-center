package cn.ablxyw.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 更新用户密码用户对象
 *
 * @author weiQiang
 * @date 2018/10/12
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("更新用户密码用户对象")
public class SysUserVo implements Serializable {
    private static final long serialVersionUID = -2930406938495410966L;
    /**
     * 新密码
     */
    @ApiModelProperty(value = "新密码")
    @NotBlank(message = "新密码不能为空!")
    private String newPassword;
    /**
     * 旧密码
     */
    @ApiModelProperty(value = "旧密码")
    @NotBlank(message = "旧密码不能为空!")
    private String oldPassword;
    /**
     * 用户主键
     */
    @ApiModelProperty(value = "用户主键")
    @NotBlank(message = "用户主键不能为空!")
    private String userId;
}
