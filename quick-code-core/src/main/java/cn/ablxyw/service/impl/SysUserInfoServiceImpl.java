package cn.ablxyw.service.impl;


import cn.ablxyw.entity.SysTokenInfo;
import cn.ablxyw.entity.SysUserInfo;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.mapper.SysUserInfoMapper;
import cn.ablxyw.service.SysTokenInfoService;
import cn.ablxyw.service.SysUserInfoService;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.PageResultUtil;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import cn.ablxyw.vo.SysUserVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.ablxyw.aspect.CommonAspect.REQUEST_INFO;
import static cn.ablxyw.constants.GlobalConstants.*;

/**
 * @author weiQiang
 * @date 2018/10/12
 */
@Slf4j
@Service(value = "sysUserInfoService")
public class SysUserInfoServiceImpl implements SysUserInfoService {
    /**
     * private RedisTemplate redisTemplate;
     */
    @Autowired
    private SysTokenInfoService sysTokenInfoService;

    @Resource
    private SysUserInfoMapper sysUserInfoMapper;

    /**
     * 是否开启登录
     */
    @Value("${qFrame.login:true}")
    private boolean enableLogin;

    /**
     * 删除所有
     *
     * @return ResultEntity
     */
    @Override
    public ResultEntity batchRemoveAll() {
        return ResultUtil.error(GlobalEnum.DeleteNoSupport);
    }

    /**
     * 对象信息Map
     *
     * @param sysUserInfo 查询参数
     * @return Map
     */
    @Override
    public Map<String, SysUserInfo> convertRecordToMap(SysUserInfo sysUserInfo) {
        sysUserInfo = convertQueryParam(sysUserInfo);
        return sysUserInfoMapper.list(sysUserInfo).stream().filter(info -> StringUtils.isNotBlank(info.getUserId()))
                .collect(Collectors.toMap(SysUserInfo::getUserId, Function.identity(), (oldValue, newValue) -> newValue));
    }

    /**
     * 转换请求参数
     *
     * @param sysUserInfo 用户信息
     * @return SysUserInfo
     */
    private SysUserInfo convertQueryParam(SysUserInfo sysUserInfo) {
        if (null == sysUserInfo) {
            sysUserInfo = SysUserInfo.builder().build();
        }
        return sysUserInfo;
    }

    /**
     * 删除对象
     *
     * @param pkIds 对象主键集合
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity delete(List<String> pkIds) {
        if (null == pkIds || pkIds.size() < 1) {
            return ResultUtil.error(GlobalEnum.DataEmpty);
        }
        SysUserInfo sysUserInfo = queryByToken();
        if (!Objects.equals(sysUserInfo.getUserType(), USER_TYPE_ADMIN)) {
            return ResultUtil.error(GlobalEnum.NoAuthority);
        }
        //如果剩余最后一个管理员则不允许删除
        List<SysUserInfo> sysUserInfoList = sysUserInfoMapper.list(SysUserInfo.builder().userType(USER_TYPE_ADMIN).build());
        List<String> allAdminUserIds = sysUserInfoList.stream().map(SysUserInfo::getUserId).collect(Collectors.toList());
        List<String> removeUserIds = allAdminUserIds.stream()
                .filter(allAdminUserId -> pkIds.contains(allAdminUserId))
                .collect(Collectors.toList());
        if (Objects.equals(removeUserIds, allAdminUserIds) || Objects.equals(removeUserIds.size(), allAdminUserIds.size())) {
            return ResultUtil.error(GlobalEnum.LastAdminAccount);
        }
        sysUserInfoMapper.batchDelete(pkIds);
        sysTokenInfoService.delete(pkIds);
        return ResultUtil.success(GlobalEnum.DeleteSuccess);
    }

    /**
     * 增加对象
     *
     * @param sysUserInfos 对象参数
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity insert(List<SysUserInfo> sysUserInfos) {
        return insertOrUpdateSysUserInfo(sysUserInfos, OPERATE_TYPE_INSERT);
    }

    /**
     * 增加或更新用户信息
     *
     * @param sysUserInfos 用户信息
     * @param operateType  操作类型
     * @return
     */
    private ResultEntity insertOrUpdateSysUserInfo(List<SysUserInfo> sysUserInfos, String operateType) {
        if (null == sysUserInfos || sysUserInfos.size() < 1) {
            return ResultUtil.error(GlobalEnum.DataEmpty);
        }
        Map<String, SysUserInfo> sysUserInfoMap = convertUserNameAndType(SysUserInfo.builder().build());
        Map<String, SysUserInfo> userInfoMap = sysUserInfoMap.values().stream().collect(Collectors.toList()).stream()
                .filter(sysUserInfo -> StringUtils.isNotBlank(sysUserInfo.getUserId()))
                .collect(Collectors.toMap(SysUserInfo::getUserId, Function.identity(), (oldValue, newValue) -> newValue));
        List<SysUserInfo> insertSysUserInfos = new ArrayList<>();
        List<SysUserInfo> updateSysUserInfos = new ArrayList<>();
        //非管理员不能修改其他用的信息
        SysUserInfo loginUserInfo = queryByToken();
        sysUserInfos.stream().forEach(sysUserInfo -> {
            String loginName = sysUserInfo.getLoginName();
            String password = sysUserInfo.getPassword();
            Integer userType = sysUserInfo.getUserType();
            checkUserOperation(loginUserInfo, sysUserInfo.getUserId(), userType, operateType);
            if (Objects.equals(OPERATE_TYPE_INSERT, operateType)) {
                if (StringUtils.isBlank(loginName)) {
                    GlobalUtils.convertMessage(GlobalEnum.UserLoginNameEmpty);
                }
                if (StringUtils.isBlank(password)) {
                    GlobalUtils.convertMessage(GlobalEnum.UserPasswordEmpty);
                }
                if (sysUserInfoMap.containsKey(loginName)) {
                    GlobalUtils.convertMessage(GlobalEnum.UserNameInUsed, loginName);
                }
                sysUserInfo.setUserId("user_" + GlobalUtils.ordinaryId());
                sysUserInfo.setPassword(GlobalUtils.md5(password, TOKEN_ISSUER));
                sysUserInfo.setStatus(null != sysUserInfo.getStatus() && sysUserInfo.getStatus());
                insertSysUserInfos.add(sysUserInfo);
            } else {
                String userId = sysUserInfo.getUserId();
                if (StringUtils.isBlank(userId)) {
                    GlobalUtils.convertMessage(GlobalEnum.PkIdEmpty);
                }
                adminAndUserCheck(SysUserVo.builder().userId(userId).build());
                if (!userInfoMap.containsKey(userId)) {
                    GlobalUtils.convertMessage(GlobalEnum.UserInfoEmpty, loginName);
                }
                if (sysUserInfoMap.containsKey(loginName) && !Objects.equals(sysUserInfoMap.get(loginName).getUserId(), userId)) {
                    GlobalUtils.convertMessage(GlobalEnum.UserNameInUsed, loginName);
                }
                sysUserInfo.setPassword(null);
                updateSysUserInfos.add(sysUserInfo);
            }
        });
        if (null != insertSysUserInfos && insertSysUserInfos.size() > 0) {
            Integer updateCount = sysUserInfoMapper.batchInsert(insertSysUserInfos);
            log.info("增加用户:{}条", updateCount);
            if (updateCount > 0) {
                return ResultUtil.success(GlobalEnum.UpdateSuccess, insertSysUserInfos);
            }
        }
        if (null != updateSysUserInfos && updateSysUserInfos.size() > 0) {
            Integer updateCount = sysUserInfoMapper.batchUpdate(updateSysUserInfos);
            log.info("更新用户:{}条", updateCount);
            if (updateCount > 0) {
                return ResultUtil.success(GlobalEnum.UpdateSuccess, updateSysUserInfos);
            }
        }
        return ResultUtil.success(GlobalEnum.UpdateError, sysUserInfos);
    }

    /**
     * 非管理员不能增加用户，不能修改其他用户信息
     *
     * @param loginUserInfo 登录用户信息
     * @param userId        当前操作用户Id
     * @param userType      当前操作用户类型
     * @param operateType   操作类型
     */
    private void checkUserOperation(SysUserInfo loginUserInfo, String userId, Integer userType, String operateType) {
        String loginUserId = loginUserInfo.getUserId();
        Integer loginUserType = loginUserInfo.getUserType();
        Map<String, SysUserInfo> sysUserInfoMap = convertRecordToMap(SysUserInfo.builder().build());
        if (!Objects.equals(loginUserType, USER_TYPE_ADMIN)) {
            if (Objects.equals(operateType, OPERATE_TYPE_INSERT)) {
                GlobalUtils.convertMessage(GlobalEnum.NoAuthority);
            } else {
                if (!Objects.equals(loginUserId, userId)) {
                    GlobalUtils.convertMessage(GlobalEnum.NoAuthority);
                }
                SysUserInfo sysUserInfo = sysUserInfoMap.get(userId);
                Integer oldUserType = sysUserInfo.getUserType();
                if (!Objects.equals(sysUserInfo.getUserId(), userId) || !Objects.equals(userType, oldUserType)) {
                    GlobalUtils.convertMessage(GlobalEnum.NoAuthority);
                }
            }
        }
    }

    /**
     * 对象信息Map
     *
     * @param sysUserInfo 查询参数
     * @return Map
     */
    @Override
    public Map<String, SysUserInfo> convertUserNameAndType(SysUserInfo sysUserInfo) {
        return sysUserInfoMapper.list(sysUserInfo).stream().filter(info -> StringUtils.isNotBlank(info.getLoginName()))
                .collect(Collectors.toMap(SysUserInfo::getLoginName, Function.identity(), (oldValue, newValue) -> newValue));
    }

    /**
     * 通过登陆名称删除用户
     *
     * @param sysUserInfoList 用户信息
     * @return ResultEntity
     */
    @Override
    public ResultEntity deleteByLoginName(List<SysUserInfo> sysUserInfoList) {
        if (null == sysUserInfoList || sysUserInfoList.size() < 1) {
            return ResultUtil.error(GlobalEnum.DataEmpty);
        }
        Map<String, SysUserInfo> sysUserInfoMap = convertUserNameAndType(SysUserInfo.builder().build());
        List<String> pkIds = new ArrayList<>();
        sysUserInfoList.stream().forEach(sysUserInfo -> {
            String loginName = sysUserInfo.getLoginName();
            Integer userType = sysUserInfo.getUserType();
            if (StringUtils.isBlank(loginName)) {
                GlobalUtils.convertMessage(GlobalEnum.UserLoginNameEmpty);
            }
            if (Objects.isNull(userType)) {
                GlobalUtils.convertMessage(GlobalEnum.UserRoleEmpty);
            }
            if (!sysUserInfoMap.containsKey(loginName)) {
                GlobalUtils.convertMessage(GlobalEnum.UserInfoEmpty, loginName);
            } else {
                pkIds.add(sysUserInfoMap.get(loginName).getUserId());
            }
        });
        return delete(pkIds);
    }

    /**
     * 用户登陆
     *
     * @param userInfo 用户信息
     * @param request  请求
     * @param response 响应
     * @return ResultEntity
     */
    @Override
    public ResultEntity login(SysUserInfo userInfo, HttpServletRequest request, HttpServletResponse response) {
        userInfo = convertQueryParam(userInfo);
        String loginName = userInfo.getLoginName();
        String password = userInfo.getPassword();
        String realIpAddress = checkFrequentlyLogin(request, userInfo);
        Map<String, SysUserInfo> userInfoMap = convertUserNameAndType(SysUserInfo.builder().loginName(loginName).build());
        if (userInfoMap.isEmpty() || !userInfoMap.containsKey(loginName)) {
            return ResultUtil.error(GlobalUtils.convertMsg(GlobalEnum.UserNameError, loginName));
        }
        SysUserInfo sysUserInfo = userInfoMap.get(loginName);
        if (!sysUserInfo.getStatus()) {
            return ResultUtil.error(GlobalUtils.convertMsg(GlobalEnum.UserNoLogin, loginName));
        }
        String infoPassword = sysUserInfo.getPassword();
        boolean verify = false;
        try {
            verify = GlobalUtils.verify(password, TOKEN_ISSUER, infoPassword);
        } catch (Exception e) {
            return ResultUtil.error(GlobalEnum.PasswordError);
        }
        if (!verify) {
            return ResultUtil.error(GlobalEnum.PasswordError);
        }
        sysTokenInfoService.initToken(request, sysUserInfo, response);
        //redisTemplate.delete(realIpAddress);
        log.info("登录Ip:{}", realIpAddress);
        return ResultUtil.success(GlobalEnum.LoginSuccess, Lists.newArrayList(sysUserInfo));
    }

    /**
     * 检测是否频繁登陆
     *
     * @param request     请求
     * @param sysUserInfo 用户信息
     */
    private String checkFrequentlyLogin(HttpServletRequest request, SysUserInfo sysUserInfo) {
        String ipAddress = GlobalUtils.getIpAddress(request);
        String realIpAddress = ipAddress.replaceAll(INTERVAL_POINT, "");
        /*
         ValueOperations<String, SysUserInfo> valueOperations = redisTemplate.opsForValue();
         SysUserInfo info = null;
         if (null != valueOperations.get(realIpAddress)) {
         info = valueOperations.get(realIpAddress);
         }
         Date current = new Date();
         boolean frequentlyLogin = false;
         if (null != info) {
         Integer loginCount = info.getLoginCount();
         Date firstLogin = info.getFirstLogin();
         long currentTime = current.getTime();
         long firstLoginTime = firstLogin.getTime();
         long frequentlyTime = currentTime - firstLoginTime;
         loginCount += 1;
         frequentlyLogin = (FREQUENTLY_LOGIN_COUNT <= loginCount && frequentlyTime < SECONDS_TWO_MINUTE) || FREQUENTLY_MAX_LOGIN_ERROR_COUNT <= loginCount;
         info.setLoginCount(loginCount);
         } else {
         info = sysUserInfo;
         info.setFirstLogin(current);
         info.setLoginCount(1);
         }
         info.setIpAddress(ipAddress);
         valueOperations.set(realIpAddress, info);
         if (frequentlyLogin) {
         GlobalUtils.convertMessage(GlobalEnum.FrequentlyLogin, sysUserInfo.getLoginName(), info.getLoginCount().toString());
         }
         */
        return realIpAddress;
    }

    /**
     * 登出
     *
     * @param token   token
     * @param request 请求
     * @return ResultEntity
     */
    @Override
    public ResultEntity logout(String token, HttpServletRequest request) {
        ResultEntity resultEntity = sysTokenInfoService.tokenValid(token, request);
        if (!resultEntity.isSuccess()) {
            return resultEntity;
        }
        resultEntity = sysTokenInfoService.deleteToken(token);
        if (resultEntity.isSuccess()) {
            resultEntity.setMessage(GlobalEnum.LogoutSuccess.getMessage());
        }
        return resultEntity;
    }

    /**
     * 重置用户密码
     *
     * @param sysUser  用户信息
     * @param request  请求
     * @param response 响应
     * @return ResultEntity
     */
    @Override
    public ResultEntity resetPassword(SysUserVo sysUser, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(TOKEN_HEADER);
        SysTokenInfo sysTokenInfo = GlobalUtils.parseJwt(token);
        String userId = sysUser.getUserId();
        String id = sysTokenInfo.getId();
        if (!Objects.equals(userId, id)) {
            return ResultUtil.error(GlobalEnum.NoResetPassword);
        }
        ResultEntity resultEntity = sysTokenInfoService.tokenValid(token, request);
        if (!resultEntity.isSuccess()) {
            return resultEntity;
        }
        List<SysUserInfo> sysUserInfos = sysUserInfoMapper.list(SysUserInfo.builder().userId(userId).build());
        Map<String, SysUserInfo> userInfoMap = sysUserInfos.stream()
                .collect(Collectors.toMap(SysUserInfo::getUserId, Function.identity(), (oldValue, newValue) -> newValue));
        if (userInfoMap.isEmpty() || !userInfoMap.containsKey(userId)) {
            GlobalUtils.convertMessage(GlobalEnum.UserInfoEmpty, userId);
        }
        SysUserInfo sysUserInfo = userInfoMap.get(userId);
        String password = sysUserInfo.getPassword();
        String oldPassword = sysUser.getOldPassword();
        boolean verify;
        try {
            verify = GlobalUtils.verify(oldPassword, TOKEN_ISSUER, password);
        } catch (Exception e) {
            return ResultUtil.error(GlobalEnum.PasswordError);
        }
        if (!verify) {
            return ResultUtil.error(GlobalEnum.UserOldPasswordError);
        }
        sysUserInfo.setPassword(GlobalUtils.md5(sysUser.getNewPassword(), TOKEN_ISSUER));
        List<SysUserInfo> updateSysUserInfos = new ArrayList<SysUserInfo>() {{
            add(sysUserInfo);
        }};
        Integer updateCount = sysUserInfoMapper.batchUpdate(updateSysUserInfos);
        log.info("更新用户:{}条", updateCount);
        if (updateCount > 0) {
            String updateToken = sysTokenInfoService.updateToken(token);
            response.setHeader(TOKEN_NEW_HEADER, updateToken);
            return ResultUtil.success(GlobalEnum.UpdateSuccess, updateSysUserInfos);
        }
        return ResultUtil.error(GlobalEnum.UpdateError);
    }

    /**
     * 管理员重置用户密码
     *
     * @param userVo  用户信息
     * @param request 请求
     * @return ResultEntity
     */
    @Override
    public ResultEntity adminRestUserPassword(SysUserVo userVo, HttpServletRequest request) {
        SysUserInfo userInfo = adminAndUserCheck(userVo);
        String newPassword = Objects.isNull(userVo.getNewPassword()) ? userInfo.getLoginName() : userVo.getNewPassword();
        newPassword = GlobalUtils.md5(newPassword, TOKEN_ISSUER);
        userInfo.setPassword(newPassword);
        Integer updateCount = sysUserInfoMapper.batchUpdate(Lists.newArrayList(userInfo));
        return ResultUtil.msg(updateCount);
    }

    /**
     * 管理员登出登录的用户
     *
     * @param userVo  用户信息
     * @param request 请求
     * @return ResultEntity
     */
    @Override
    public ResultEntity adminLogoutUser(SysUserVo userVo, HttpServletRequest request) {
        SysUserInfo sysUserInfo = adminAndUserCheck(userVo);
        ResultEntity resultEntity = sysTokenInfoService.delete(Lists.newArrayList(sysUserInfo.getUserId()));
        if (resultEntity.isSuccess()) {
            return ResultUtil.success(GlobalEnum.LogoutSuccess);
        } else {
            return ResultUtil.error(GlobalEnum.LogoutError);
        }
    }

    /**
     * 通过token获取登录用户信息
     *
     * @return SysUserInfo
     */
    @Override
    public SysUserInfo queryByToken() {
        if (!enableLogin) {
            GlobalUtils.convertMessage(GlobalEnum.LoginNoOpen);
        }
        HttpServletRequest request = REQUEST_INFO.get();
        if (Objects.isNull(request)) {
            GlobalUtils.convertMessage(GlobalEnum.NoLogin);
        }
        String token = request.getHeader(TOKEN_HEADER);
        ResultEntity resultEntity = sysTokenInfoService.tokenValid(token, request);
        if (!resultEntity.isSuccess()) {
            GlobalUtils.convertMessage(resultEntity.getMessage());
        }
        SysTokenInfo sysTokenInfo = GlobalUtils.parseJwt(token);
        if (Objects.isNull(sysTokenInfo) || Objects.isNull(sysTokenInfo.getId()) || StringUtils.isBlank(sysTokenInfo.getId())) {
            GlobalUtils.convertMessage(GlobalEnum.UserLoginNameEmpty);
        }
        SysUserInfo adminUserInfo = sysUserInfoMapper.findById(sysTokenInfo.getId());
        if (Objects.isNull(adminUserInfo) || StringUtils.isBlank(adminUserInfo.getUserId())) {
            GlobalUtils.convertMessage(GlobalEnum.DataEmpty);
        }
        return adminUserInfo;
    }

    /**
     * 管理员和用户验证
     *
     * @param userVo 其他用户信息
     */
    private SysUserInfo adminAndUserCheck(SysUserVo userVo) {
        if (!enableLogin) {
            GlobalUtils.convertMessage(GlobalEnum.LoginNoOpen);
        }
        SysUserInfo loginUserInfo = queryByToken();
        if (Objects.isNull(userVo) || Objects.isNull(userVo.getUserId()) || StringUtils.isBlank(userVo.getUserId())) {
            GlobalUtils.convertMessage(GlobalEnum.UserLoginNameEmpty);
        }
        String userId = userVo.getUserId();
        //非管理员不可以操作,当前登录用户仅能操作自己的信息
        boolean adminOrSelfUserFlag = (!Objects.equals(loginUserInfo.getUserType(), USER_TYPE_ADMIN) || !loginUserInfo.getStatus()) && !Objects.equals(loginUserInfo.getUserId(), userId);
        if (adminOrSelfUserFlag) {
            GlobalUtils.convertMessage(GlobalEnum.NoAuthority);
        }
        SysUserInfo sysUserInfo = sysUserInfoMapper.findById(userId);
        if (Objects.isNull(sysUserInfo)) {
            GlobalUtils.convertMessage(GlobalEnum.UserLoginNameEmpty);
        }
        return sysUserInfo;
    }

    /**
     * 根据条件分页查询对象
     *
     * @param sysUserInfo 查询参数
     * @param pageNum     开始页数
     * @param pageSize    每页显示的数据条数
     * @param sortName    排序信息
     * @param sortOrder   排序顺序
     * @return ResultEntity
     */
    @Override
    public ResultEntity list(SysUserInfo sysUserInfo, Integer pageNum, Integer pageSize, String sortName, String sortOrder) {
        sysUserInfo = convertQueryParam(sysUserInfo);
        PageHelper.startPage(pageNum, pageSize);
        String sort = GlobalUtils.changeColumn(sortName, sortOrder);
        sysUserInfo.setSort(sort);
        List<SysUserInfo> sysUserInfos = sysUserInfoMapper.list(sysUserInfo);
        PageInfo pageInfo = new PageInfo(sysUserInfos);
        return PageResultUtil.success(GlobalEnum.QuerySuccess, pageInfo);
    }

    /**
     * 根据条件查询对象
     *
     * @param sysUserInfo 查询参数
     * @return ResultEntity
     */
    @Override
    public ResultEntity list(SysUserInfo sysUserInfo) {
        sysUserInfo = convertQueryParam(sysUserInfo);
        List<SysUserInfo> sysUserInfos = sysUserInfoMapper.list(sysUserInfo);
        return ResultUtil.success(GlobalEnum.QuerySuccess, sysUserInfos);
    }

    /**
     * 更新对象
     *
     * @param sysUserInfos 更新参数
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity update(List<SysUserInfo> sysUserInfos) {
        return insertOrUpdateSysUserInfo(sysUserInfos, OPERATE_TYPE_UPDATE);
    }
}
