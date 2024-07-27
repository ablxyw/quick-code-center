package cn.ablxyw.controller;

import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.service.impl.ApiDbService;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 获取数据源信息
 *
 * @author weiqiang
 * @date 2021-03-25
 **/
@CrossOrigin
@Api(value = "返回结果列配置Api", tags = "返回结果列配置接口Api")
@RestController
@RequestMapping("/apiDb")
public class ApiDbController {

    /**
     * 第三方数接口入库service
     */
    @Autowired
    private ApiDbService dbService;

    /**
     * 查询数据源所有表
     *
     * @param dataSourceId 数据源Id
     * @return ResultEntity
     */
    @ApiOperation("查询数据源所有表")
    @RequestMapping(value = "/tables", method = RequestMethod.GET)
    public ResultEntity tables(@RequestParam String dataSourceId) {
        if (StringUtils.isBlank(dataSourceId)) {
            return ResultUtil.error(GlobalEnum.DataEmpty);
        }
        return dbService.getTables(dataSourceId);
    }

    /**
     * 查询表所有字段
     *
     * @param dataSourceId 数据源Id
     * @param tableName    表名
     * @return ResultEntity
     */
    @ApiOperation("查询表所有字段")
    @RequestMapping(value = "/columns", method = RequestMethod.GET)
    public ResultEntity columns(@RequestParam String dataSourceId, @RequestParam String tableName) {
        if (StringUtils.isBlank(dataSourceId) || StringUtils.isBlank(tableName)) {
            return ResultUtil.error(GlobalEnum.DataEmpty);
        }
        return dbService.getColumns(dataSourceId, tableName);
    }

    /**
     * 根据函数ID获取样例数据
     *
     * @param functionId 函数ID
     * @return ResultEntity
     */
    @ApiOperation("根据函数ID获取样例数据")
    @RequestMapping(value = "/apiData", method = RequestMethod.GET)
    public ResultEntity apiData(@RequestParam String functionId) {
        return dbService.apiData(functionId);
    }
}
