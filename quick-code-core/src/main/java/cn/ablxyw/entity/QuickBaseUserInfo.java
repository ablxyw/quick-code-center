package cn.ablxyw.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * 公共用户字段,每个有权限的表都需要extends
 *
 * @author weiqiang
 * @date 2021-03-22 下午7:02
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("公共用户字段")
public class QuickBaseUserInfo implements Serializable {
    private static final long serialVersionUID = 580809123681208603L;

    /**
     * 创建者Id
     */
    @ApiModelProperty(value = "创建者Id")
    public String createId;

    /**
     * 更新者Id
     */
    @ApiModelProperty(value = "更新者Id")
    public String updateId;
}
