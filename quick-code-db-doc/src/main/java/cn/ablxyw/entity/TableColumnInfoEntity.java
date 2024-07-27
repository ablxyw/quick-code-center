package cn.ablxyw.entity;

import cn.ablxyw.annotation.ExcelColumn;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 表列信息
 *
 * @author weiqiang
 * @date 2019-12-23
 */
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"tableName", "columnName"})
public class TableColumnInfoEntity implements Serializable {
    private static final long serialVersionUID = -6064979413710701530L;

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
    @ExcelColumn(value = "功能模块名称", col = 1)
    private String tableName;

    /**
     * 列名
     */
    @NotBlank(message = "列名不能为空")
    @Length(max = 64, message = "列名不能多于64个字符")
    @ApiModelProperty(value = "列名")
    private String columnName;

    /**
     * 列名
     */
    @JsonIgnore
    @ExcelColumn(value = "字段名称", col = 3)
    private String columnJavaName;

    /**
     * 列顺序
     */
    @NotNull(message = "列顺序不能为空")
    @Length(max = 0, message = "列顺序不能多于0个字符")
    @ApiModelProperty(value = "列顺序")
    private Integer ordinalPosition;

    /**
     * 默认值
     */
    @Length(max = 500, message = "默认值不能多于500个字符")
    @ApiModelProperty(value = "默认值")
    private String columnDefault;

    /**
     * 是否为空
     */
    @NotBlank(message = "是否为空不能为空")
    @Length(max = 3, message = "是否为空不能多于3个字符")
    @ApiModelProperty(value = "是否为空")
    @ExcelColumn(value = "是否必填", col = 5)
    private String isNullable;

    /**
     * 是否必填的错误提示
     */
    @JsonIgnore
    @ExcelColumn(value = "是否必填的错误提示", col = 6)
    private String isNullableMessage;

    /**
     * 长度
     */
    @JsonIgnore
    @ExcelColumn(value = "长度", col = 7)
    private Integer columnLength;
    /**
     * 长度
     */
    @JsonIgnore
    @ExcelColumn(value = "长度错误提示", col = 8)
    private String columnLengthMessage;

    /**
     * 列类型
     */
    @NotBlank(message = "列类型不能为空")
    @Length(max = 500, message = "列类型不能多于500个字符")
    @ApiModelProperty(value = "列类型")
    @ExcelColumn(value = "类型", col = 4)
    private String columnType;

    /**
     * 是否主键
     */
    @Length(max = 3, message = "是否主键不能多于3个字符")
    @ApiModelProperty(value = "是否主键")
    private String columnKey;

    /**
     * 列描述
     */
    @NotBlank(message = "列描述不能为空")
    @Length(max = 1024, message = "列描述不能多于1024个字符")
    @ApiModelProperty(value = "列描述")
    @ExcelColumn(value = "字段中文名", col = 2)
    private String columnComment;

    /**
     * 排序信息
     */
    @JsonIgnore
    @ApiModelProperty(value = "排序信息", hidden = true)
    private String sort;
}
