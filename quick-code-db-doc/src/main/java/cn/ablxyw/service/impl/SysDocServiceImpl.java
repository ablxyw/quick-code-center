package cn.ablxyw.service.impl;

import cn.ablxyw.entity.TableColumnInfoEntity;
import cn.ablxyw.entity.TableInfoEntity;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.service.DynamicDataSourceService;
import cn.ablxyw.service.SysDocService;
import cn.ablxyw.service.TableInfoService;
import cn.ablxyw.utils.DocUtil;
import cn.ablxyw.utils.ExcelUtils;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static cn.ablxyw.constants.GlobalConstants.EMPTY_STRING;

/**
 * 生成数据库设计文档Impl
 *
 * @author weiqiang
 * @date 2021-04-16 上午8:53
 */
@Service("sysDocService")
public class SysDocServiceImpl implements SysDocService {

    /**
     * 表信息Service
     */
    @Autowired
    private TableInfoService tableInfoService;

    /**
     * 多数据源切换Service
     */
    @Autowired
    private DynamicDataSourceService dynamicDataSourceService;

    /**
     * 生成doc文档
     *
     * @param tableSchema  tableSchema
     * @param datasourceId 数据源ID
     * @return ResultEntity
     */
    @Override
    public ResponseEntity poiDoc(String tableSchema, String datasourceId) {
        if (StringUtils.isBlank(datasourceId)) {
            return ResponseEntity.ok(ResultUtil.error(GlobalEnum.DataEmpty));
        }
        try {
            dynamicDataSourceService.changeDb(datasourceId);
        } catch (Exception e) {
            return ResponseEntity.ok(ResultUtil.error(GlobalEnum.DataEmpty));
        }
        tableSchema = StringUtils.isBlank(tableSchema) ? EMPTY_STRING : tableSchema;
        ResultEntity resultEntity = tableInfoService.list(TableInfoEntity.builder().tableSchema(tableSchema).build());
        List<TableInfoEntity> tableInfoEntities = (List<TableInfoEntity>) resultEntity.getData();
        if (Objects.isNull(tableInfoEntities) || tableInfoEntities.size() < 1) {
            return ResponseEntity.ok(ResultUtil.error(GlobalEnum.DataEmpty));
        }
        tableSchema = StringUtils.isBlank(tableSchema) ? datasourceId : tableSchema;
        File downloadFile = null;
        try {
            downloadFile = DocUtil.poiTl(tableInfoEntities, tableSchema);
        } catch (Exception e) {
            return ResponseEntity.ok(ResultUtil.error(GlobalEnum.MsgOperationFailed));
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment;filename=" + downloadFile.getName())
                .contentLength(downloadFile.length())
                .body(new FileSystemResource(downloadFile));
    }

    /**
     * 生成数据库设计文档
     *
     * @param datasourceId 数据源ID
     * @param tableSchema  tableSchema
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity databaseDocExcel(String datasourceId, String tableSchema) {
        try {
            dynamicDataSourceService.changeDb(datasourceId);
        } catch (Exception e) {
            return ResponseEntity.ok(ResultUtil.error(GlobalEnum.DataEmpty));
        }
        ResultEntity resultEntity = tableInfoService.list(TableInfoEntity.builder().tableSchema(tableSchema).build());
        List<TableInfoEntity> tableInfoEntities = (List<TableInfoEntity>) resultEntity.getData();
        if (Objects.isNull(tableInfoEntities) || tableInfoEntities.size() < 1) {
            return ResponseEntity.ok(ResultUtil.error(GlobalEnum.DataEmpty));
        }
        final String yesCode = "是";
        final String noCode = "否";
        final String viewCode = "VIEW";
        final String tableCode = "表";
        List<TableColumnInfoEntity> columnInfoEntities = Lists.newArrayList();
        tableInfoEntities.stream().filter(tableInfoEntity -> !Objects.equals(viewCode, tableInfoEntity.getTableComment())).forEach(tableInfoEntity -> {
            String tableName = tableInfoEntity.getTableComment().replace(tableCode, EMPTY_STRING);
            tableInfoEntity.getTableColumnInfoEntities().forEach(tableColumnInfoEntity -> {
                tableColumnInfoEntity.setTableName(tableName);
                String columnName = tableColumnInfoEntity.getColumnName();
                String comment = tableColumnInfoEntity.getColumnComment();
                if (Objects.isNull(comment)) {
                    comment = columnName;
                }
                tableColumnInfoEntity.setColumnJavaName(GlobalUtils.lineToHump(columnName));
                String isNullable = tableColumnInfoEntity.getIsNullable();
                String type = tableColumnInfoEntity.getColumnType().toUpperCase();
                isNullable = Objects.equals(isNullable, "NO") ? yesCode : noCode;
                String key = StringUtils.isBlank(tableColumnInfoEntity.getColumnKey()) ? noCode : yesCode;
                if (Objects.equals(key, yesCode)) {
                    tableColumnInfoEntity.setIsNullable(noCode);
                } else {
                    tableColumnInfoEntity.setIsNullable(isNullable);
                }
                if (Objects.equals(isNullable, yesCode) && !Objects.equals(key, yesCode)) {
                    tableColumnInfoEntity.setIsNullableMessage(GlobalUtils.appendString(comment, "不能为空"));
                }
                Boolean emptyLength = false;
                Integer columnLength = 1;
                try {
                    columnLength = Integer.parseInt(type.replaceAll("[^0-9]", ""));
                    tableColumnInfoEntity.setColumnLength(columnLength);
                    if (columnLength > 0) {
                        tableColumnInfoEntity.setColumnLengthMessage(GlobalUtils.appendString(comment, "长度不能多于", columnLength.toString(), "个字符"));
                    }
                } catch (NumberFormatException e) {
                    emptyLength = true;
                }
                boolean boolFlag = (type.contains("BIT") || columnLength == 1) && !emptyLength;
                boolean strFlag = type.contains("VARCHAR") || type.contains("CHAR") || type.contains("TEXT");
                boolean intFlag = type.contains("INT") || type.contains("NUMBER") || type.contains("DOUBLE");
                boolean dateTimeFlag = type.contains("DATE") || type.contains("TIME");
                if (strFlag) {
                    tableColumnInfoEntity.setColumnType("字符串");
                } else if (intFlag) {
                    tableColumnInfoEntity.setColumnType("数字");
                } else if (boolFlag) {
                    tableColumnInfoEntity.setColumnType("布尔");
                } else if (dateTimeFlag) {
                    tableColumnInfoEntity.setColumnType("时间");
                }
                columnInfoEntities.add(tableColumnInfoEntity);
            });
        });
        tableSchema = StringUtils.isBlank(tableSchema) ? datasourceId : tableSchema;
        String fileName = DocUtil.OUT_BASE_PATH + tableSchema + "_" + GlobalUtils.ordinaryId() + ".xlsx";
        File downloadFile = new File(fileName);
        ExcelUtils.writeExcel(fileName, columnInfoEntities, TableColumnInfoEntity.class);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment;filename=" + downloadFile.getName())
                .contentLength(downloadFile.length())
                .body(new FileSystemResource(downloadFile));
    }
}
