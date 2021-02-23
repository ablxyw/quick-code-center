package cn.ablxyw.controller;

import cn.ablxyw.entity.SysUserInfo;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.service.SysUserInfoService;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ObjectInfo;
import cn.ablxyw.vo.ResultEntity;
import cn.ablxyw.vo.SysUserVo;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static cn.ablxyw.constants.GlobalConstants.TOKEN_HEADER;

/**
 * (SysUserInfo)表控制层
 *
 * @author 魏强
 * @since 2018-10-12 16:54:15
 */
@RestController
@RequestMapping("user")
@CrossOrigin
@Api(value = "用户接口Api", tags = "用户接口Api")
public class SysUserInfoController {

    /**
     * 成绩管理系统用户信息
     */
    @Autowired
    private SysUserInfoService sysUserInfoService;

    /**
     * 根据主键集合删除用户(SysUserInfo)
     *
     * @param objectInfo 主键集合
     * @return ResultEntity
     */
    @ApiOperation("根据主键集合删除用户")
    @PostMapping(value = "deleteByIds")
    public ResultEntity delete(@Valid @RequestBody ObjectInfo objectInfo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return sysUserInfoService.delete(objectInfo.getIds());
    }

    /**
     * 删除用户(SysUserInfo)
     *
     * @param userId 主键
     * @return ResultEntity
     */
    @ApiOperation("根据主键集合删除用户")
    @DeleteMapping(path = "{userId}")
    public ResultEntity delete(@PathVariable String userId) {
        return sysUserInfoService.delete(Lists.newArrayList(userId));
    }

    /**
     * 增加用户(SysUserInfo)
     *
     * @param scoreUserInfos 插入参数
     * @param bindingResult  参数绑定校验
     * @return ResultEntity
     */
    @ApiOperation("增加用户")
    @PostMapping(value = "insert")
    public ResultEntity insert(@Valid @RequestBody List<SysUserInfo> scoreUserInfos, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return sysUserInfoService.insert(scoreUserInfos);
    }

    /**
     * 根据条件分页查询
     *
     * @param scoreUserInfo 查询参数
     * @param pageNum       开始页数
     * @param pageSize      每页显示的数据条数
     * @param sortName      排序字段
     * @param sortOrder     排序顺序
     * @return ResultEntity
     */
    @ApiOperation("根据条件分页查询")
    @GetMapping(value = "/listByPage")
    public ResultEntity list(SysUserInfo scoreUserInfo, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "30") Integer pageSize, String sortName, String sortOrder) {
        return sysUserInfoService.list(scoreUserInfo, pageNum, pageSize, sortName, sortOrder);
    }

    /**
     * 根据条件查询
     *
     * @param scoreUserInfo 查询参数
     * @return ResultEntity
     */
    @ApiOperation("根据条件查询")
    @GetMapping(value = {"", "list"})
    public ResultEntity list(SysUserInfo scoreUserInfo) {
        return sysUserInfoService.list(scoreUserInfo);
    }

    /**
     * 重置用户密码
     *
     * @param userVo        用户信息
     * @param bindingResult 校验信息
     * @param request       请求
     * @param response      响应
     * @return ResultEntity
     */
    @ApiOperation("重置用户密码")
    @PostMapping(value = {"resetPassword"})
    public ResultEntity resetPassword(@Valid @RequestBody SysUserVo userVo, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return sysUserInfoService.resetPassword(userVo, request, response);
    }

    /**
     * 更新用户(SysUserInfo)
     *
     * @param scoreUserInfos 更新参数
     * @param bindingResult  参数绑定校验
     * @return ResultEntity
     */
    @ApiOperation("更新用户")
    @PostMapping(value = "update")
    public ResultEntity update(@Valid @RequestBody List<SysUserInfo> scoreUserInfos, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return sysUserInfoService.update(scoreUserInfos);
    }

    /**
     * 用户登陆
     *
     * @param userInfo      用户信息
     * @param bindingResult 校验信息
     * @param request       请求
     * @param response      响应
     * @return ResultEntity
     */
    @ApiOperation("用户登陆")
    @PostMapping(value = "login")
    public ResultEntity userLogin(@Valid @RequestBody SysUserInfo userInfo, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return sysUserInfoService.login(userInfo, request, response);
    }

    /**
     * 用户登出
     *
     * @param request 请求
     * @return ResultEntity
     */
    @ApiOperation("用户登出")
    @GetMapping(value = "logout")
    public ResultEntity userLogout(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);
        return sysUserInfoService.logout(token, request);
    }

    /**
     * 管理员重置用户密码
     *
     * @param userVo  用户信息
     * @param request 请求
     * @return ResultEntity
     */
    @ApiOperation("管理员重置用户密码")
    @PatchMapping(value = "adminRestUserPassword")
    public ResultEntity adminRestUserPassword(@RequestBody SysUserVo userVo, HttpServletRequest request) {
        return sysUserInfoService.adminRestUserPassword(userVo, request);
    }

    /**
     * 管理员登出登录的用户
     *
     * @param userVo  用户信息
     * @param request 请求
     * @return ResultEntity
     */
    @ApiOperation("强制退出用户")
    @DeleteMapping(value = "adminLogoutUser")
    public ResultEntity adminLogoutUser(@RequestBody SysUserVo userVo, HttpServletRequest request) {
        return sysUserInfoService.adminLogoutUser(userVo, request);
    }

    /**
     * 当前登录用户信息
     *
     * @return ResultEntity
     */
    @ApiOperation("当前登录用户信息")
    @GetMapping(value = "loginUserInfo")
    public ResultEntity queryUserByToken() {
        SysUserInfo sysUserInfo = SysUserInfo.builder().build();
        try {
            sysUserInfo = sysUserInfoService.queryByToken();
        } catch (Exception e) {
        }
        return ResultUtil.success(GlobalEnum.QuerySuccess, Lists.newArrayList(sysUserInfo));
    }
}
