package cn.ablxyw.controller;

import cn.ablxyw.entity.SysDatasourceConfigEntity;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.service.SysDatasourceConfigService;
import cn.ablxyw.utils.AesUtil;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.DataSourcePassword;
import cn.ablxyw.vo.ObjectInfo;
import cn.ablxyw.vo.ResultEntity;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源配置
 *
 * @author weiqiang
 * @date 2020-01-14 11:28:44
 */
@CrossOrigin
@Api(value = "数据源配置Api", tags = "数据源配置接口")
@RestController
@RequestMapping("/sysDataSourceConfig")
public class SysDatasourceConfigController {

    /**
     * 数据源配置Service
     */
    @Autowired
    private SysDatasourceConfigService sysDatasourceConfigService;

    /**
     * 分页查询数据源配置
     *
     * @param sysDatasourceConfig 数据源配置
     * @param pageNumber          初始页
     * @param pageSize            每页条数
     * @param sortName            排序字段
     * @param sortOrder           排序顺序
     * @return ResultEntity
     */
    @ApiOperation("分页查询数据源配置")
    @RequestMapping(value = "/listByPage", method = RequestMethod.GET)
    public ResultEntity list(SysDatasourceConfigEntity sysDatasourceConfig, @RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "30") Integer pageSize, String sortName, String sortOrder) {
        return sysDatasourceConfigService.list(sysDatasourceConfig, pageNumber, pageSize, sortName, sortOrder);
    }

    /**
     * 查询数据源配置
     *
     * @param sysDatasourceConfig 数据源配置
     * @return ResultEntity
     */
    @ApiOperation("查询数据源配置")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultEntity list(SysDatasourceConfigEntity sysDatasourceConfig) {
        return sysDatasourceConfigService.list(sysDatasourceConfig);
    }

    /**
     * 新增数据源配置
     *
     * @param sysDatasourceConfig 数据源配置
     * @return ResultEntity
     */
    @ApiOperation("新增数据源配置")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public ResultEntity insert(@Valid @RequestBody SysDatasourceConfigEntity sysDatasourceConfig, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        List<SysDatasourceConfigEntity> sysDatasourceConfigList = new ArrayList<SysDatasourceConfigEntity>(1) {{
            add(sysDatasourceConfig);
        }};
        return sysDatasourceConfigService.insert(sysDatasourceConfigList);
    }

    /**
     * 根据主键datasourceId查询数据源配置详情
     *
     * @param datasourceId 数据源的id
     * @return ResultEntity
     */
    @ApiOperation("根据主键datasourceId查询数据源配置详情")
    @RequestMapping(path = "{datasourceId}", method = RequestMethod.GET)
    public ResultEntity listById(@PathVariable String datasourceId) {
        return sysDatasourceConfigService.list(SysDatasourceConfigEntity.builder().datasourceId(datasourceId).build());
    }

    /**
     * 修改数据源配置
     *
     * @param sysDatasourceConfig 数据源配置
     * @return ResultEntity
     */
    @ApiOperation("修改数据源配置")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultEntity update(@Valid @RequestBody SysDatasourceConfigEntity sysDatasourceConfig, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        List<SysDatasourceConfigEntity> sysDatasourceConfigList = new ArrayList<SysDatasourceConfigEntity>(1) {{
            add(sysDatasourceConfig);
        }};
        return sysDatasourceConfigService.update(sysDatasourceConfigList);
    }

    /**
     * 修改数据源密码
     *
     * @param dataSourcePassword 修改数据源密码
     * @return ResultEntity
     */
    @ApiOperation("修改数据源密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public ResultEntity updatePassword(@Valid @RequestBody DataSourcePassword dataSourcePassword, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        List<DataSourcePassword> dataSourcePasswords = Lists.newArrayList(dataSourcePassword);
        return sysDatasourceConfigService.updatePassword(dataSourcePasswords);
    }

    /**
     * 删除数据源配置
     *
     * @param datasourceId
     * @return ResultEntity
     */
    @ApiOperation("删除数据源配置")
    @RequestMapping(path = "{datasourceId}", method = RequestMethod.DELETE)
    public ResultEntity batchRemove(@PathVariable String datasourceId) {
        List<String> datasourceIdList = new ArrayList<String>(1) {{
            add(datasourceId);
        }};
        return sysDatasourceConfigService.delete(datasourceIdList);
    }

    /**
     * 根据主键集合数据源配置(sys_datasource_config)
     *
     * @param objectInfo 主键集合
     * @return ResultEntity
     */
    @ApiOperation("根据主键集合删除数据源配置")
    @RequestMapping(value = "deleteByIds", method = RequestMethod.POST)
    public ResultEntity delete(@Valid @RequestBody ObjectInfo<String> objectInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return sysDatasourceConfigService.delete(objectInfo.getIds());
    }

    /**
     * 测试数据源连接
     *
     * @param sysDatasourceConfig 数据源配置
     * @param bindingResult       校验结果
     * @return ResultEntity
     */
    @ApiOperation("测试数据源连接")
    @PostMapping(value = "testConnect")
    public ResultEntity testConnect(@Valid @RequestBody SysDatasourceConfigEntity sysDatasourceConfig, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return sysDatasourceConfigService.testConnect(sysDatasourceConfig);
    }

    /**
     * 加密密码
     *
     * @param password 密码
     * @return ResultEntity
     */
    @ApiOperation("密码加密")
    @GetMapping(value = "aesEncrypt")
    public ResultEntity aesEncrypt(String password) {
        if (StringUtils.isBlank(password)) {
            return ResultUtil.error(GlobalEnum.PasswordEmpty);
        }
        String aesEncrypt = AesUtil.aesEncrypt(password);
        Map<String, String> passwordMap = new HashMap<String, String>(2) {{
            put("ori", password);
            put("encrypt", aesEncrypt);
        }};
        List<Map<String, String>> passwordLists = Lists.newArrayList(passwordMap);
        return ResultUtil.success(GlobalEnum.QuerySuccess, passwordLists);
    }


}
