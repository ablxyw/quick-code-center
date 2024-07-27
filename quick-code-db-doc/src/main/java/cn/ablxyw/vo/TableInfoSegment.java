package cn.ablxyw.vo;

import com.deepoove.poi.data.DocxRenderData;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 表数据
 *
 * @author weiqiang
 * @date 2020-03-14 10:30 上午
 */
@Data
@Builder
public class TableInfoSegment implements Serializable {
    private static final long serialVersionUID = 4870356439710303905L;
    /**
     * 表格行数据
     */
    private DocxRenderData segment;

    /**
     * 表名
     */
    private String tableName;
}
