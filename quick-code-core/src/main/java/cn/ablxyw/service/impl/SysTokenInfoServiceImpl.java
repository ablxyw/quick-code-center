package cn.ablxyw.service.impl;

import cn.ablxyw.entity.SysTokenInfo;
import cn.ablxyw.entity.SysUserInfo;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.mapper.SysTokenInfoMapper;
import cn.ablxyw.service.SysTokenInfoService;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.ablxyw.constants.GlobalConstants.*;

/**
 * token信息ServiceImpl
 *
 * @author weiQiang
 * @date 2020/03/31
 */
@Slf4j
@Service(value = "tokenInfoService")
public class SysTokenInfoServiceImpl implements SysTokenInfoService {

    @Resource
    private SysTokenInfoMapper sysTokenInfoMapper;

    /**
     * 删除Token
     *
     * @param pkIds 主键
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity delete(List<String> pkIds) {
        if (null == pkIds || pkIds.size() < 1) {
            return ResultUtil.error(GlobalEnum.DataEmpty);
        }
        sysTokenInfoMapper.batchDelete(pkIds);
        return ResultUtil.success(GlobalEnum.DeleteSuccess);
    }

    /**
     * 删除token
     *
     * @param token token
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity deleteToken(String token) {
        SysTokenInfo sysTokenInfo = GlobalUtils.parseJwt(token);
        String id = sysTokenInfo.getId();
        if (StringUtils.isBlank(id)) {
            return ResultUtil.error(GlobalEnum.PkIdEmpty);
        }
        sysTokenInfoMapper.batchDelete(Lists.newArrayList(id));
        return ResultUtil.success(GlobalEnum.DeleteSuccess);
    }

    /**
     * 生成token
     *
     * @param request  请求
     * @param userInfo 用户信息
     * @param response 响应
     * @return ResultEntity
     */
    @Override
    public ResultEntity initToken(HttpServletRequest request, SysUserInfo userInfo, HttpServletResponse response) {
        String id = userInfo.getUserId();
        String ipAddress = GlobalUtils.getIpAddress(request);
        Map<String, Object> subjectMap = new HashMap<>(2);
        subjectMap.put(CLIENT_IP, ipAddress);
        subjectMap.put(CLIENT_INFO, userInfo);
        String subject = JSON.toJSONString(subjectMap, SerializerFeature.WriteMapNullValue);
        String token = GlobalUtils.createJwt(id, TOKEN_ISSUER, subject, TOKEN_TIME_OUT);
        SysTokenInfo sysTokenInfo = GlobalUtils.parseJwt(token);
        deleteToken(token);
        sysTokenInfoMapper.batchInsert(Lists.newArrayList(sysTokenInfo));
        response.setHeader(TOKEN_HEADER, token);
        return ResultUtil.success(GlobalEnum.InsertSuccess);
    }

    /**
     * 检测token是否有效
     *
     * @param token   token
     * @param request 请求
     * @return ResultEntity
     */
    @Override
    public ResultEntity tokenValid(String token, HttpServletRequest request) {
        String ipAddress = GlobalUtils.getIpAddress(request);
        SysTokenInfo sysTokenInfo;
        try {
            sysTokenInfo = GlobalUtils.parseJwt(token);
        } catch (Exception e) {
            return ResultUtil.error(e.getMessage());
        }
        List<SysTokenInfo> sysTokenInfos = sysTokenInfoMapper.list(SysTokenInfo.builder().id(sysTokenInfo.getId()).build());
        if (null == sysTokenInfos || sysTokenInfos.size() < 1) {
            return ResultUtil.error(GlobalEnum.TokenOvertime);
        }
        String subject = sysTokenInfo.getSubject();
        if (StringUtils.isBlank(subject)) {
            return ResultUtil.error(GlobalEnum.TokenSignError);
        }
        String clientIp = JSON.parseObject(subject).getString(CLIENT_IP);
        boolean success = Objects.equals(ipAddress, clientIp);
        success = success && Objects.equals(token, sysTokenInfos.get(0).getToken());
        String message = success ? GlobalEnum.QuerySuccess.getMessage() : GlobalEnum.UserLoginOtherIp.getMessage();
        return ResultEntity.builder().success(success).message(message).build();
    }

    /**
     * 更新token
     *
     * @param token token
     * @return String
     */
    @Override
    public String updateToken(String token) {
        SysTokenInfo sysTokenInfo = GlobalUtils.parseJwt(token);
        String newToken = GlobalUtils.createJwt(sysTokenInfo.getId(), TOKEN_ISSUER, sysTokenInfo.getSubject(), TOKEN_TIME_OUT);
        SysTokenInfo newSysTokenInfo = GlobalUtils.parseJwt(newToken);
        ResultEntity resultEntity = deleteToken(token);
        log.info("删除用户登陆token:{}", resultEntity);
        Integer updateCount = sysTokenInfoMapper.batchUpdate(Lists.newArrayList(newSysTokenInfo));
        if (updateCount > 0) {
            log.info("更新用户登陆Token:{}条", updateCount);
        }
        return newToken;
    }
}
