package cn.ablxyw.utils;

import cn.ablxyw.entity.TableColumnInfoEntity;
import cn.ablxyw.entity.TableInfoEntity;
import cn.ablxyw.vo.ColumnCode;
import cn.ablxyw.vo.TableInfoSegment;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.DocxRenderData;
import com.deepoove.poi.data.MiniTableRenderData;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.Style;
import com.deepoove.poi.data.style.TableStyle;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文档工具类
 *
 * @author weiqiang
 * @date 2020-03-14 4:57 下午
 */
@Slf4j
public class DocUtil {
    /**
     * 文档路径与文档名称
     */
    public static final String BASE_PATH = System.getProperty("user.dir") + File.separator + "doc" + File.separator, fileName = "tableInfo";
    /**
     * 文档输出路径
     */
    public static final String OUT_BASE_PATH = System.getProperty("user.dir") + File.separator + "logs" + File.separator;

    /**
     * 生成数据库文档
     *
     * @param tableInfoEntities 表信息
     * @param tableSchema       表空间
     * @return File
     * @throws Exception
     */
    public static File poiTl(List<TableInfoEntity> tableInfoEntities, String tableSchema) throws Exception {
        long beginTime = System.currentTimeMillis();
        Style headTextStyle = new Style();
        headTextStyle.setFontFamily("STFangsong");
        headTextStyle.setColor("000000");
        headTextStyle.setBold(true);

        TableStyle headStyle = new TableStyle();
        headStyle.setBackgroundColor("B3B3B3");
        headStyle.setAlign(STJc.CENTER);

        TableStyle rowStyle = new TableStyle();
        rowStyle.setAlign(STJc.LEFT);

        Style bodyTextStyle = new Style();
        bodyTextStyle.setFontFamily("STFangsong");
        bodyTextStyle.setColor("000000");
        List<String> headerList = Lists.newArrayList("代码", "名称", "数据类型", "强制", "是键", "注释");
        TextRenderData[] textRenderData = new TextRenderData[headerList.size()];
        for (int i = 0; i < headerList.size(); i++) {
            textRenderData[i] = new TextRenderData(headerList.get(i), headTextStyle);
        }
        RowRenderData header = RowRenderData.build(textRenderData);
        header.setRowStyle(headStyle);
        List<ColumnCode> columnCodeList = new ArrayList<ColumnCode>() {{
            tableInfoEntities.stream().filter(tableInfoEntity -> StringUtils.isNotBlank(tableInfoEntity.getTableComment())).forEach(tableInfoEntity -> {
                String tableComment = tableInfoEntity.getTableComment();
                String key = GlobalUtils.appendString(StringUtils.isNotBlank(tableComment) ? tableComment : "", "(" + tableInfoEntity.getTableName().toUpperCase() + ")");
                List<TableColumnInfoEntity> columnInfoEntities = tableInfoEntity.getTableColumnInfoEntities();
                List<RowRenderData> bodyList = Lists.newLinkedList();
                columnInfoEntities.forEach(columnInfoEntity -> {
                    RowRenderData rowRenderData = RowRenderData.build(
                            new TextRenderData(columnInfoEntity.getColumnName().toUpperCase(), bodyTextStyle),
                            new TextRenderData(columnInfoEntity.getColumnComment(), bodyTextStyle),
                            new TextRenderData(columnInfoEntity.getColumnType().toUpperCase(), bodyTextStyle),
                            new TextRenderData(Objects.equals(columnInfoEntity.getIsNullable(), "NO") ? "FALSE" : "TRUE", bodyTextStyle),
                            new TextRenderData(StringUtils.isBlank(columnInfoEntity.getColumnKey()) ? "FALSE" : "TRUE", bodyTextStyle),
                            new TextRenderData(columnInfoEntity.getColumnComment(), bodyTextStyle)
                    );
                    rowRenderData.setRowStyle(rowStyle);
                    bodyList.add(rowRenderData);
                });
                add(ColumnCode.builder()
                        .key(key)
                        .renderData(new MiniTableRenderData(header, bodyList))
                        .build());
            });
        }};
        ZipSecureFile.setMinInflateRatio(-1.0d);
        DocxRenderData segment = new DocxRenderData(new File(BASE_PATH + "segment.docx"), columnCodeList);
        TableInfoSegment tableInfoSegment = TableInfoSegment.builder().segment(segment).build();
        XWPFTemplate template = XWPFTemplate.compile(BASE_PATH + "tableTemplate.docx").render(tableInfoSegment);
        String tableOutName = OUT_BASE_PATH + (StringUtils.isBlank(tableSchema) ? "" : tableSchema) + fileName + "_out.docx";
        Files.deleteIfExists(Paths.get(tableOutName));
        FileOutputStream out = new FileOutputStream(tableOutName);
        template.write(out);
        out.flush();
        out.close();
        template.close();
        log.info("{}ms", (System.currentTimeMillis() - beginTime));
        return new File(tableOutName);
    }
}
