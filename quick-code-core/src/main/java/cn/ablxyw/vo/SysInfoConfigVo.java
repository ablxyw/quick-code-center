package cn.ablxyw.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * 系统信息
 *
 * @author weiqiang
 * @date 2020-03-30 2:34 下午
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("系统信息实体类")
public class SysInfoConfigVo implements Serializable {
    private static final long serialVersionUID = -1317838874140610085L;

    /**
     * 系统名称
     */
    @ApiModelProperty(value = "系统名称")
    private String title;

    /**
     * 系统版本
     */
    @ApiModelProperty(value = "系统版本")
    private String version;

    /**
     * 公司链接地址
     */
    @ApiModelProperty(value = "公司链接地址")
    private String companyUrl;

    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 主题
     */
    @ApiModelProperty(value = "主题")
    private String theme;
}
