package cn.ablxyw.vo;

import com.deepoove.poi.data.MiniTableRenderData;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 一体化实体
 *
 * @author weiqiang
 * @date 2019-12-23
 */
@Data
@Builder
public class ColumnCode implements Serializable {
    private static final long serialVersionUID = -1100850156010656770L;
    /**
     * 主题名称
     */
    private String key;

    /**
     * 一体化编码
     */
    private MiniTableRenderData renderData;
}
