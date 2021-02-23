package cn.ablxyw.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * token信息
 *
 * @author weiQiang
 * @date 2020/03/31
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("token信息")
public class SysTokenInfo implements Serializable {
    private static final long serialVersionUID = 4855503467585352933L;
    /**
     * token主键
     */
    @ApiModelProperty(value = "token主键")
    private String id;
    /**
     * token拥有者
     */
    @ApiModelProperty(value = "token拥有者")
    private String issuer;
    /**
     * token内容
     */
    @ApiModelProperty(value = "token内容")
    private String subject;
    /**
     * token产生时间
     */
    @ApiModelProperty(value = "token产生时间")
    private Date issuedAt;
    /**
     * token过期时间
     */
    @ApiModelProperty(value = "token过期时间")
    private Date expiration;

    /**
     * 校验token状态
     */
    @ApiModelProperty(value = "校验token状态")
    private boolean success;
    /**
     * token
     */
    @ApiModelProperty(value = "token")
    private String token;
    /**
     * 校验token类型
     */
    @ApiModelProperty(value = "校验token类型")
    private String tokenType;
}
