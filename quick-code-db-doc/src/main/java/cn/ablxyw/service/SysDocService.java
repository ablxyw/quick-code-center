package cn.ablxyw.service;

import org.springframework.http.ResponseEntity;

/**
 * 生成数据库设计文档
 *
 * @author weiqiang
 * @date 2021-04-16 上午8:51
 */
public interface SysDocService {

    /**
     * 生成doc文档
     *
     * @param tableSchema  tableSchema
     * @param datasourceId 数据源ID
     * @return ResultEntity
     */
    ResponseEntity poiDoc(String tableSchema, String datasourceId);

    /**
     * 生成数据库设计文档
     *
     * @param datasourceId 数据源ID
     * @param tableSchema  tableSchema
     * @return ResponseEntity
     */
    ResponseEntity databaseDocExcel(String datasourceId, String tableSchema);
}
