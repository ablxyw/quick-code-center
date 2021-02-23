package cn.ablxyw.service;

import cn.ablxyw.entity.SysUserInfo;
import cn.ablxyw.vo.ResultEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * token信息Service
 *
 * @author weiQiang
 * @date 2020/03/31
 */
public interface SysTokenInfoService {

    /**
     * 删除Token
     *
     * @param pkIds 主键
     * @return ResultEntity
     */
    ResultEntity delete(List<String> pkIds);

    /**
     * 删除token
     *
     * @param token token
     * @return ResultEntity
     */
    ResultEntity deleteToken(String token);

    /**
     * 生成token
     *
     * @param request     请求
     * @param sysUserInfo 用户信息
     * @param response    响应
     * @return ResultEntity
     */
    ResultEntity initToken(HttpServletRequest request, SysUserInfo sysUserInfo, HttpServletResponse response);

    /**
     * 检测token是否有效
     *
     * @param token   token
     * @param request 请求
     * @return ResultEntity
     */
    ResultEntity tokenValid(String token, HttpServletRequest request);

    /**
     * 更新token
     *
     * @param token
     * @return String
     */
    String updateToken(String token);
}
