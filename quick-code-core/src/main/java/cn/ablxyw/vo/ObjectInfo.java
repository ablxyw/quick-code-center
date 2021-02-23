package cn.ablxyw.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 对象模型
 *
 * @author weiQiang
 * @date 2018/10/2
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel("主键对象模型")
public class ObjectInfo<E> implements Serializable {
    private static final long serialVersionUID = -1018021034105955989L;

    /**
     * 主键集合
     */
    @Valid
    @NotNull(message = "主键ID不能为空!")
    @ApiModelProperty(value = "主键集合")
    private List<E> ids;
}
