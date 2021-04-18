package cn.ablxyw.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 表信息
 *
 * @author weiqiang
 * @date 2019-12-23
 */
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"tableName", "columnName"})
public class TableInfoEntity implements Serializable {
    private static final long serialVersionUID = -4982149992510377668L;

    /**
     * 表空间
     */
    @JsonIgnore
    @ApiModelProperty(value = "表空间", hidden = true)
    private String tableSchema;

    /**
     * 表名
     */
    @NotBlank(message = "表名不能为空")
    @Length(max = 64, message = "表名不能多于64个字符")
    @ApiModelProperty(value = "表名")
    private String tableName;

    /**
     * 表备注
     */
    @NotBlank(message = "表备注不能为空")
    @Length(max = 64, message = "表备注不能多于64个字符")
    @ApiModelProperty(value = "表备注")
    private String tableComment;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 表列信息
     */
    @ApiModelProperty(value = "表列信息")
    private List<TableColumnInfoEntity> tableColumnInfoEntities;
}
