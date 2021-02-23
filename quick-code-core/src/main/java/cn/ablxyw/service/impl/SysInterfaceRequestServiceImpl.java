package cn.ablxyw.service.impl;

import cn.ablxyw.entity.SysInterfaceRequestEntity;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.mapper.SysInterfaceRequestMapper;
import cn.ablxyw.service.SysInterfaceRequestService;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.PageResultUtil;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 接口请求日志ServiceImpl
 *
 * @author weiqiang
 * @email weiq0525@gmail.com
 * @date 2020-02-16 18:11:59
 */
@Service("sysInterfaceRequestService")
public class SysInterfaceRequestServiceImpl implements SysInterfaceRequestService {

    /**
     * 接口请求日志Mapper
     */
    @Resource
    private SysInterfaceRequestMapper sysInterfaceRequestMapper;

    /**
     * 删除所有
     *
     * @return ResultEntity
     */
    @Override
    public ResultEntity batchRemoveAll() {
        return ResultUtil.success(GlobalEnum.DeleteSuccess);
    }

    /**
     * 接口请求日志对象信息Map
     *
     * @param sysInterfaceRequest 查询参数
     * @return Map
     */
    @Override
    public Map<String, SysInterfaceRequestEntity> convertRecordToMap(SysInterfaceRequestEntity sysInterfaceRequest) {
        List<SysInterfaceRequestEntity> sysInterfaceRequestList = sysInterfaceRequestMapper.list(sysInterfaceRequest);
        Map<String, SysInterfaceRequestEntity> sysInterfaceRequestMap = sysInterfaceRequestList.stream().filter(info -> null != info.getRequestId())
                .
                        collect(Collectors.toMap(SysInterfaceRequestEntity::getRequestId, Function.identity(), (oldValue, newValue) -> newValue));
        return sysInterfaceRequestMap;
    }

    /**
     * 分页查询接口请求日志
     *
     * @param sysInterfaceRequest 接口请求日志
     * @param pageNum             初始页
     * @param pageSize            每页条数
     * @param sortName            排序信息
     * @param sortOrder           排序顺序
     * @return ResultEntity
     */
    @Override
    public ResultEntity list(SysInterfaceRequestEntity sysInterfaceRequest, Integer pageNum, Integer pageSize, String sortName, String sortOrder) {
        PageHelper.startPage(pageNum, pageSize);
        String sort = GlobalUtils.changeColumn(sortName, sortOrder);
        sysInterfaceRequest.setSort(sort);
        List<SysInterfaceRequestEntity> sysInterfaceRequestList = sysInterfaceRequestMapper.list(sysInterfaceRequest);
        PageInfo pageInfo = new PageInfo(sysInterfaceRequestList);
        return PageResultUtil.success(GlobalEnum.QuerySuccess, pageInfo);
    }

    /**
     * 查询接口请求日志
     *
     * @param sysInterfaceRequest 接口请求日志
     * @return ResultEntity
     */
    @Override
    public ResultEntity list(SysInterfaceRequestEntity sysInterfaceRequest) {
        List<SysInterfaceRequestEntity> sysInterfaceRequestList = sysInterfaceRequestMapper.list(sysInterfaceRequest);
        return ResultUtil.success(GlobalEnum.QuerySuccess, sysInterfaceRequestList);
    }

    /**
     * 新增接口请求日志
     *
     * @param sysInterfaceRequestList 接口请求日志集合
     * @return ResultEntity
     * @Transactional(rollbackFor = RuntimeException.class)
     */
    @Override
    public ResultEntity insert(List<SysInterfaceRequestEntity> sysInterfaceRequestList) {
        if (Objects.isNull(sysInterfaceRequestList) || sysInterfaceRequestList.isEmpty()) {
            return ResultUtil.error(GlobalEnum.DataEmpty);
        }
        sysInterfaceRequestList.stream()
                .filter(sysInterfaceRequestEntity -> Objects.isNull(sysInterfaceRequestEntity.getRequestId()))
                .forEach(sysQueryConfigEntity -> {
                    sysQueryConfigEntity.setRequestId(GlobalUtils.ordinaryId());
                    sysQueryConfigEntity.setServerIp(GlobalUtils.getHostIp());
                });
        Integer insertCount = sysInterfaceRequestMapper.batchInsert(sysInterfaceRequestList);
        return ResultUtil.msg(insertCount);
    }

    /**
     * 修改接口请求日志
     *
     * @param sysInterfaceRequestList 接口请求日志集合
     * @return ResultEntity
     * @Transactional(rollbackFor = RuntimeException.class)
     */
    @Override
    public ResultEntity update(List<SysInterfaceRequestEntity> sysInterfaceRequestList) {
        List<SysInterfaceRequestEntity> entities = sysInterfaceRequestList.stream()
                .filter(sysQueryConfig -> StringUtils.isNotBlank(sysQueryConfig.getRequestId()))
                .collect(Collectors.toList());
        if (entities.isEmpty()) {
            return ResultUtil.error(GlobalEnum.DataEmpty);
        }
        entities.forEach(sysQueryConfigEntity -> sysQueryConfigEntity.setServerIp(GlobalUtils.getHostIp()));
        Integer updateCount = sysInterfaceRequestMapper.batchUpdate(entities);
        return ResultUtil.msg(updateCount);
    }

    /**
     * 删除 接口请求日志
     *
     * @param requestIdList 主键集合
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity delete(List<String> requestIdList) {
        Integer deleteCount = sysInterfaceRequestMapper.batchDelete(requestIdList);
        return ResultUtil.msg(deleteCount);
    }

}
