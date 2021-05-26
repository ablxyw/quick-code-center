package cn.ablxyw.service.impl;

import cn.ablxyw.entity.SysScreenshotLogEntity;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.mapper.SysScreenshotLogMapper;
import cn.ablxyw.service.SysScreenshotLogService;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * html转图配置LogServiceImpl
 *
 * @author weiqiang
 * @date 2021-05-26 4:04 下午
 */
@Service("sysScreenshotLogService")
public class SysScreenshotLogServiceImpl implements SysScreenshotLogService {

    /**
     * html转图配置Mapper
     */
    @Resource
    private SysScreenshotLogMapper sysScreenshotLogMapper;

    /**
     * 删除所有
     *
     * @return ResultEntity
     */
    @Override
    public ResultEntity batchRemoveAll() {
        return ResultUtil.error(GlobalEnum.MsgOperationSuccess);
    }

    /**
     * 对象信息Map
     *
     * @param record 查询参数
     * @return Map
     */
    @Override
    public Map<String, SysScreenshotLogEntity> convertRecordToMap(SysScreenshotLogEntity record) {
        List<SysScreenshotLogEntity> screenshotEntityList = sysScreenshotLogMapper.selectList(convertWrapper(record));
        return screenshotEntityList.stream().filter(sysScreenshotEntity -> StringUtils.isNotBlank(sysScreenshotEntity.getLogId()))
                .collect(Collectors.toMap(SysScreenshotLogEntity::getShotId, Function.identity(), (o1, o2) -> o2));
    }

    /**
     * 删除对象
     *
     * @param pkIds 对象主键集合
     * @return ResultEntity
     */
    @Override
    public ResultEntity delete(List<String> pkIds) {
        return null;
    }

    /**
     * 增加对象
     *
     * @param records 对象参数
     * @return ResultEntity
     */
    @Override
    public ResultEntity insert(List<SysScreenshotLogEntity> records) {
        AtomicReference<Integer> insertCount = new AtomicReference<>(0);
        records.stream().forEach(sysScreenshotEntity -> {
            sysScreenshotEntity.setShotId(GlobalUtils.ordinaryId());
            sysScreenshotEntity.setCreateTime(new Date());
            sysScreenshotEntity.setUpdateTime(new Date());
            insertCount.updateAndGet(v -> v + sysScreenshotLogMapper.insert(sysScreenshotEntity));
        });
        return ResultUtil.msg(insertCount.get());
    }

    /**
     * 根据条件分页查询对象
     *
     * @param record    查询参数
     * @param pageNum   开始页数
     * @param pageSize  每页显示的数据条数
     * @param sortName  排序字段
     * @param sortOrder 排序顺序
     * @return ResultEntity
     */
    @Override
    public ResultEntity list(SysScreenshotLogEntity record, Integer pageNum, Integer pageSize, String sortName, String sortOrder) {
        PageHelper.startPage(pageNum, pageSize);
        String sort = GlobalUtils.changeColumn(sortName, sortOrder);
        record.setSort(sort);
        List<SysScreenshotLogEntity> screenshotEntityList = sysScreenshotLogMapper.selectList(convertWrapper(record));
        PageInfo pageInfo = new PageInfo(screenshotEntityList);
        return ResultUtil.success(GlobalEnum.QuerySuccess, pageInfo);
    }

    /**
     * 根据条件查询对象
     *
     * @param record 查询参数
     * @return ResultEntity
     */
    @Override
    public ResultEntity list(SysScreenshotLogEntity record) {
        List<SysScreenshotLogEntity> screenshotEntityList = sysScreenshotLogMapper.selectList(convertWrapper(record));
        return ResultUtil.success(GlobalEnum.QuerySuccess, screenshotEntityList);
    }

    /**
     * 更新对象
     *
     * @param records 更新参数
     * @return ResultEntity
     */
    @Override
    public ResultEntity update(List<SysScreenshotLogEntity> records) {
        long emptyPkId = records.stream()
                .filter(sysDatasourceConfig -> Objects.isNull(sysDatasourceConfig.getLogId()))
                .count();
        if (emptyPkId > 0) {
            return ResultUtil.error(GlobalEnum.PkIdEmpty);
        }
        AtomicReference<Integer> updateCount = new AtomicReference<>(0);
        records.stream().forEach(sysScreenshotEntity -> {
            sysScreenshotEntity.setUpdateTime(new Date());
            updateCount.updateAndGet(v -> v + sysScreenshotLogMapper.updateById(sysScreenshotEntity));
        });
        return ResultUtil.msg(updateCount.get());
    }

    /**
     * 转换请求参数
     *
     * @param sysScreenshotEntity html转图配置
     * @return QueryWrapper
     */
    private QueryWrapper<SysScreenshotLogEntity> convertWrapper(SysScreenshotLogEntity sysScreenshotEntity) {
        if (Objects.isNull(sysScreenshotEntity)) {
            sysScreenshotEntity = SysScreenshotLogEntity.builder().build();
        }
        QueryWrapper<SysScreenshotLogEntity> queryWrapper = new QueryWrapper<>(sysScreenshotEntity);
        return queryWrapper;
    }

    /**
     * 根据shotId获取生成日志信息
     *
     * @param shotId shotId
     * @return List
     */
    @Override
    public List<SysScreenshotLogEntity> listByShotId(String shotId) {
        if (StringUtils.isNotBlank(shotId)) {
            return Lists.newArrayList();
        }
        return sysScreenshotLogMapper.selectList(convertWrapper(SysScreenshotLogEntity.builder().shotId(shotId).build()));
    }
}
