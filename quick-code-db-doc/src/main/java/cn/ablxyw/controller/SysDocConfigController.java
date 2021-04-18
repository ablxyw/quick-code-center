package cn.ablxyw.controller;

import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.service.SysDocService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 接口文档Controller
 *
 * @author weiqiang
 * @email weiqiang@ablxyw.cn
 * @date 2020-01-14 11:28:44
 */
@Api(value = "接口文档Api", tags = "接口文档接口")
@RestController
@RequestMapping("/sysDocConfig")
public class SysDocConfigController {
    /**
     * 生成数据库设计文档
     */
    @Autowired
    private SysDocService sysDocService;

    /**
     * 生成数据库设计文档
     *
     * @return ResultEntity
     */
    @ApiOperation("生成数据库设计文档")
    @GetMapping(value = "databaseDoc")
    public ResponseEntity poiDoc(String tableSchema, String datasourceId) {
        return sysDocService.poiDoc(tableSchema, datasourceId);
    }


    /**
     * 生成数据库设计文档
     *
     * @return ResultEntity
     */
    @ApiOperation("生成数据库设计文档")
    @GetMapping(value = "databaseDocExcel")
    public ResponseEntity databaseDocExcel(String datasourceId, String tableSchema) {
        return sysDocService.databaseDocExcel(datasourceId, tableSchema);
    }
}
