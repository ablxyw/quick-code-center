package cn.ablxyw.service.impl;

import cn.ablxyw.entity.SysScreenshotEntity;
import cn.ablxyw.entity.SysScreenshotLogEntity;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.mapper.SysScreenshotMapper;
import cn.ablxyw.service.SysScreenshotLogService;
import cn.ablxyw.service.SysScreenshotService;
import cn.ablxyw.service.impl.factory.SysDriverChrome;
import cn.ablxyw.service.impl.factory.SysDriverEdge;
import cn.ablxyw.service.impl.factory.SysDriverFirefox;
import cn.ablxyw.service.impl.factory.SysDriverSafari;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * html转图配置ServiceImpl
 *
 * @author weiqiang
 * @date 2021-05-26 4:04 下午
 */
@Service("sysScreenshotService")
public class SysScreenshotServiceImpl implements SysScreenshotService {

    /**
     * html转图配置Mapper
     */
    @Resource
    private SysScreenshotMapper sysScreenshotMapper;

    /**
     * html转图配置Mapper
     */
    @Resource
    private SysScreenshotLogService sysScreenshotLogService;

    /**
     * SysDriverChrome
     */
    @Autowired
    private SysDriverChrome sysDriverChrome;
    /**
     * SysDriverEdge
     */
    @Autowired
    private SysDriverEdge sysDriverEdge;
    /**
     * SysDriverFirefox
     */
    @Autowired
    private SysDriverFirefox sysDriverFirefox;
    /**
     * SysDriverSafari
     */
    @Autowired
    private SysDriverSafari sysDriverSafari;

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
    public Map<String, SysScreenshotEntity> convertRecordToMap(SysScreenshotEntity record) {
        List<SysScreenshotEntity> screenshotEntityList = sysScreenshotMapper.selectList(convertWrapper(record));
        return screenshotEntityList.stream().filter(sysScreenshotEntity -> StringUtils.isNotBlank(sysScreenshotEntity.getShotId()))
                .collect(Collectors.toMap(SysScreenshotEntity::getShotId, Function.identity(), (o1, o2) -> o2));
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
    public ResultEntity insert(List<SysScreenshotEntity> records) {
        AtomicReference<Integer> insertCount = new AtomicReference<>(0);
        records.stream().forEach(sysScreenshotEntity -> {
            sysScreenshotEntity.setShotId(GlobalUtils.ordinaryId());
            sysScreenshotEntity.setCreateTime(new Date());
            sysScreenshotEntity.setUpdateTime(new Date());
            insertCount.updateAndGet(v -> v + sysScreenshotMapper.insert(sysScreenshotEntity));
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
    public ResultEntity list(SysScreenshotEntity record, Integer pageNum, Integer pageSize, String sortName, String sortOrder) {
        PageHelper.startPage(pageNum, pageSize);
        String sort = GlobalUtils.changeColumn(sortName, sortOrder);
        record.setSort(sort);
        List<SysScreenshotEntity> screenshotEntityList = sysScreenshotMapper.selectList(convertWrapper(record));
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
    public ResultEntity list(SysScreenshotEntity record) {
        List<SysScreenshotEntity> screenshotEntityList = sysScreenshotMapper.selectList(convertWrapper(record));
        screenshotEntityList = convertResult(screenshotEntityList);
        return ResultUtil.success(GlobalEnum.QuerySuccess, screenshotEntityList);
    }

    /**
     * 更新对象
     *
     * @param records 更新参数
     * @return ResultEntity
     */
    @Override
    public ResultEntity update(List<SysScreenshotEntity> records) {
        long emptyPkId = records.stream()
                .filter(sysDatasourceConfig -> Objects.isNull(sysDatasourceConfig.getShotId()))
                .count();
        if (emptyPkId > 0) {
            return ResultUtil.error(GlobalEnum.PkIdEmpty);
        }
        AtomicReference<Integer> updateCount = new AtomicReference<>(0);
        records.stream().forEach(sysScreenshotEntity -> {
            sysScreenshotEntity.setUpdateTime(new Date());
            updateCount.updateAndGet(v -> v + sysScreenshotMapper.updateById(sysScreenshotEntity));
        });
        return ResultUtil.msg(updateCount.get());
    }

    /**
     * 转换返回结果
     *
     * @param screenshotEntityList html转图配置
     * @return List
     */
    private List<SysScreenshotEntity> convertResult(List<SysScreenshotEntity> screenshotEntityList) {
        if (Objects.equals(screenshotEntityList.size(), 1)) {
            SysScreenshotEntity sysScreenshotEntity = screenshotEntityList.get(0);
            String shotId = sysScreenshotEntity.getShotId();
            List<SysScreenshotLogEntity> screenshotLogEntities = sysScreenshotLogService.listByShotId(shotId);
            sysScreenshotEntity.setSysScreenshotLogEntities(screenshotLogEntities);
            return Lists.newArrayList(sysScreenshotEntity);
        }
        return screenshotEntityList;
    }

    /**
     * 转换请求参数
     *
     * @param sysScreenshotEntity html转图配置
     * @return QueryWrapper
     */
    private QueryWrapper<SysScreenshotEntity> convertWrapper(SysScreenshotEntity sysScreenshotEntity) {
        if (Objects.isNull(sysScreenshotEntity)) {
            sysScreenshotEntity = SysScreenshotEntity.builder().build();
        }
        QueryWrapper<SysScreenshotEntity> queryWrapper = new QueryWrapper<>(sysScreenshotEntity);
        return queryWrapper;
    }

    /**
     * html转图片
     *
     * @param sysScreenshotEntity html转图配置
     * @return ResultEntity
     */
    @Override
    public ResultEntity execute(SysScreenshotEntity sysScreenshotEntity) {
        ResultEntity resultEntity;
        String driverType = sysScreenshotEntity.getDriverType();
        driverType = StringUtils.isBlank(driverType) ? "chrome" : driverType;
        switch (driverType) {
            case "firefox":
                resultEntity = sysDriverFirefox.execute(sysScreenshotEntity);
                break;
            case "edge":
                resultEntity = sysDriverEdge.execute(sysScreenshotEntity);
                break;
            case "safari":
                resultEntity = sysDriverSafari.execute(sysScreenshotEntity);
                break;
            default:
                resultEntity = sysDriverChrome.execute(sysScreenshotEntity);
                break;
        }
        return resultEntity;
    }
}
