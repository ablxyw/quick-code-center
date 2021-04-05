package cn.ablxyw.service.impl;

import cn.ablxyw.entity.SysScriptWorkbenchEntity;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.mapper.SysScriptWorkbenchMapper;
import cn.ablxyw.service.SysScriptWorkbenchService;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.ablxyw.constants.GlobalConstants.DEFAULT_PARENT_ID;
import static cn.ablxyw.constants.GlobalConstants.JAVA_SCRIPT;

/**
 * 脚本工作台ServiceImpl
 *
 * @author weiqiang
 * @date 2020-12-10 23:25:51
 */
@Service("sysScriptWorkbenchService")
public class SysScriptWorkbenchServiceImpl implements SysScriptWorkbenchService {

    /**
     * 脚本工作台Mapper
     */
    @Resource
    private SysScriptWorkbenchMapper sysScriptWorkbenchMapper;

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
     * 脚本工作台对象信息Map
     *
     * @param sysScriptWorkbench 查询参数
     * @return Map
     */
    @Override
    public Map<String, SysScriptWorkbenchEntity> convertRecordToMap(SysScriptWorkbenchEntity sysScriptWorkbench) {
        List<SysScriptWorkbenchEntity> sysScriptWorkbenchList = sysScriptWorkbenchMapper.selectList(convertWrapper(sysScriptWorkbench, true));
        sysScriptWorkbenchList = convertResult(sysScriptWorkbenchList);
        Map<String, SysScriptWorkbenchEntity> sysScriptWorkbenchMap = sysScriptWorkbenchList.stream().filter(info -> null != info.getId())
                .collect(Collectors.toMap(SysScriptWorkbenchEntity::getId, Function.identity(), (oldValue, newValue) -> newValue));
        return sysScriptWorkbenchMap;
    }

    /**
     * 分页查询脚本工作台
     *
     * @param sysScriptWorkbench 脚本工作台
     * @param pageNum            初始页
     * @param pageSize           每页条数
     * @param sortName           排序信息
     * @param sortOrder          排序顺序
     * @return ResultEntity
     */
    @Override
    public ResultEntity list(SysScriptWorkbenchEntity sysScriptWorkbench, Integer pageNum, Integer pageSize, String sortName, String sortOrder) {
        PageHelper.startPage(pageNum, pageSize);
        String sort = GlobalUtils.changeColumn(sortName, sortOrder);
        sysScriptWorkbench.setSort(sort);
        List<SysScriptWorkbenchEntity> sysScriptWorkbenchList = sysScriptWorkbenchMapper.selectList(convertWrapper(sysScriptWorkbench, true));
        PageInfo pageInfo = new PageInfo(sysScriptWorkbenchList);
        pageInfo.setList(convertResult(pageInfo.getList()));
        return ResultUtil.success(GlobalEnum.QuerySuccess, pageInfo);
    }

    /**
     * 查询脚本工作台
     *
     * @param sysScriptWorkbench 脚本工作台
     * @return ResultEntity
     */
    @Override
    public ResultEntity list(SysScriptWorkbenchEntity sysScriptWorkbench) {
        List<SysScriptWorkbenchEntity> sysScriptWorkbenchList = sysScriptWorkbenchMapper.selectList(convertWrapper(sysScriptWorkbench, !(Objects.nonNull(sysScriptWorkbench) && StringUtils.isNotBlank(sysScriptWorkbench.getId()))));
        sysScriptWorkbenchList = convertResult(sysScriptWorkbenchList);
        return ResultUtil.success(GlobalEnum.QuerySuccess, sysScriptWorkbenchList);
    }

    /**
     * 新增脚本工作台
     *
     * @param sysScriptWorkbenchList 脚本工作台集合
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity insert(List<SysScriptWorkbenchEntity> sysScriptWorkbenchList) {
        Map<String, SysScriptWorkbenchEntity> workbenchEntityMap = convertRecordToMap(null);
        AtomicReference<Integer> insertCount = new AtomicReference<>(0);
        sysScriptWorkbenchList.stream().forEach(workbench -> {
            String oriId = workbench.getOriId();
            workbench.setId(GlobalUtils.appendString("work_", GlobalUtils.ordinaryId()));
            if (StringUtils.isNotBlank(oriId) && workbenchEntityMap.containsKey(oriId)) {
                workbench.setCurVersion(workbenchEntityMap.getOrDefault(oriId, SysScriptWorkbenchEntity.builder().curVersion(1).build()).getCurVersion() + 1);
            } else {
                workbench.setOriId(DEFAULT_PARENT_ID);
                workbench.setCurVersion(1);
            }
            if (!Objects.equals(workbench.getScriptMode(), JAVA_SCRIPT)) {
                workbench.setPublicScript(false);
            }
            insertCount.updateAndGet(v -> v + sysScriptWorkbenchMapper.insert(workbench));
        });

        return ResultUtil.msg(insertCount.get());
    }

    /**
     * 修改脚本工作台
     *
     * @param sysScriptWorkbenchList 脚本工作台集合
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity update(List<SysScriptWorkbenchEntity> sysScriptWorkbenchList) {
        long emptyPkId = sysScriptWorkbenchList.stream()
                .filter(sysQueryConfig -> Objects.isNull(sysQueryConfig.getId()))
                .count();
        if (emptyPkId > 0) {
            return ResultUtil.error(GlobalEnum.PkIdEmpty);
        }
        AtomicReference<Integer> updateCount = new AtomicReference<>(0);
        sysScriptWorkbenchList.stream().forEach(workbench -> {
            if (!Objects.equals(workbench.getScriptMode(), JAVA_SCRIPT)) {
                workbench.setPublicScript(false);
            }
            updateCount.updateAndGet(v -> v + sysScriptWorkbenchMapper.updateById(workbench));
        });

        return ResultUtil.msg(updateCount.get());
    }

    /**
     * 删除 脚本工作台
     *
     * @param idList 主键集合
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity delete(List<String> idList) {
        Integer deleteCount = sysScriptWorkbenchMapper.batchDelete(idList);
        return ResultUtil.msg(deleteCount);
    }

    /**
     * 转换请求结果
     *
     * @param sysScriptWorkbenchEntities 脚本工作台
     * @return List
     */
    private List<SysScriptWorkbenchEntity> convertResult(List<SysScriptWorkbenchEntity> sysScriptWorkbenchEntities) {
        if (sysScriptWorkbenchEntities.isEmpty()) {
            return sysScriptWorkbenchEntities;
        }
        Map<String, SysScriptWorkbenchEntity> workbenchEntityMap = sysScriptWorkbenchMapper.selectVersionNameAndId().stream().collect(Collectors.toMap(SysScriptWorkbenchEntity::getId, Function.identity(), (o1, o2) -> o1));
        sysScriptWorkbenchEntities.forEach(sysScriptWorkbenchEntity -> {
            String oriId = sysScriptWorkbenchEntity.getOriId();
            if (StringUtils.isNotBlank(oriId) && workbenchEntityMap.containsKey(oriId)) {
                String versionAndName = workbenchEntityMap.get(oriId).getName();
                sysScriptWorkbenchEntity.setOriVersion(versionAndName);
            }
        });
        return sysScriptWorkbenchEntities;
    }

    /**
     * 转换请求参数
     *
     * @param sysScriptWorkbenchEntity 脚本工作台集合
     * @param ignoreFlag               是否忽略内容列
     * @return QueryWrapper
     */
    private QueryWrapper<SysScriptWorkbenchEntity> convertWrapper(SysScriptWorkbenchEntity sysScriptWorkbenchEntity, boolean ignoreFlag) {
        if (Objects.isNull(sysScriptWorkbenchEntity)) {
            sysScriptWorkbenchEntity = SysScriptWorkbenchEntity.builder().build();
        }
        QueryWrapper<SysScriptWorkbenchEntity> queryWrapper = new QueryWrapper<>(sysScriptWorkbenchEntity);
        if (ignoreFlag) {
            final String ignoreColumn = "content";
            queryWrapper.select(SysScriptWorkbenchEntity.class, info -> !Objects.equals(info.getColumn(), ignoreColumn));
        }
        return queryWrapper;
    }

}
