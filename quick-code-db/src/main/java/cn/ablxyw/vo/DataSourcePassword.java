package cn.ablxyw.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 数据源密码
 *
 * @author weiqiang
 * @date 2020-02-11 8:57 下午
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"datasourceId"})
@ApiModel("数据源密码")
public class DataSourcePassword implements Serializable {
    private static final long serialVersionUID = 3152470443765917682L;

    /**
     * 数据源id
     */
    @NotBlank(message = "数据源id不能为空")
    @ApiModelProperty(value = "数据源id")
    private String datasourceId;

    /**
     * 旧密码
     */
    @Length(max = 255, message = "旧密码不能多于255个字符")
    @ApiModelProperty(value = "旧密码")
    private String oldPassWord;
    /**
     * 密码
     */
    @Length(max = 255, message = "密码不能多于255个字符")
    @ApiModelProperty(value = "密码")
    private String passWord;
}
