package cn.ablxyw.service;

import cn.ablxyw.entity.SysUserInfo;
import cn.ablxyw.vo.ResultEntity;
import cn.ablxyw.vo.SysUserVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author weiQiang
 * @date 2018/10/12
 */
public interface SysUserInfoService extends BaseInfoService<SysUserInfo, String> {

    /**
     * 对象信息Map
     *
     * @param sysUserInfo 查询参数
     * @return Map
     */
    Map<String, SysUserInfo> convertUserNameAndType(SysUserInfo sysUserInfo);

    /**
     * 通过登陆名称删除用户
     *
     * @param sysUserInfoList 用户信息
     * @return ResultEntity
     */
    ResultEntity deleteByLoginName(List<SysUserInfo> sysUserInfoList);

    /**
     * 用户登陆
     *
     * @param userInfo 用户信息
     * @param request  请求
     * @param response 响应
     * @return ResultEntity
     */
    ResultEntity login(SysUserInfo userInfo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 登出
     *
     * @param token   token
     * @param request 请求
     * @return ResultEntity
     */
    ResultEntity logout(String token, HttpServletRequest request);

    /**
     * 重置用户密码
     *
     * @param sysUserVo 用户信息
     * @param request   请求
     * @param response  响应
     * @return ResultEntity
     */
    ResultEntity resetPassword(SysUserVo sysUserVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 管理员重置用户密码
     *
     * @param userVo  用户信息
     * @param request 请求
     * @return ResultEntity
     */
    ResultEntity adminRestUserPassword(SysUserVo userVo, HttpServletRequest request);

    /**
     * 管理员登出登录的用户
     *
     * @param userVo  用户信息
     * @param request 请求
     * @return ResultEntity
     */
    ResultEntity adminLogoutUser(SysUserVo userVo, HttpServletRequest request);

    /**
     * 通过token获取登录用户信息
     *
     * @return SysUserInfo
     */
    SysUserInfo queryByToken();
}
