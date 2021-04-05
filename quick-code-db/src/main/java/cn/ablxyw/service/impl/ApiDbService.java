package cn.ablxyw.service.impl;

import cn.ablxyw.entity.ApiDbEntity;
import cn.ablxyw.entity.SysDatasourceConfigEntity;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.mapper.BaseQueryMapper;
import cn.ablxyw.service.AbstractDbService;
import cn.ablxyw.service.DynamicDataSourceService;
import cn.ablxyw.service.SysDatasourceConfigService;
import cn.ablxyw.service.SysScriptWorkbenchService;
import cn.ablxyw.service.impl.factory.DbFactory;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static cn.ablxyw.constants.GlobalConstants.EMPTY_STRING;
import static cn.ablxyw.constants.GlobalConstants.QUERY_SQL_KEY;
import static cn.ablxyw.service.AbstractDbService.Q_BATCH_TIME;

/**
 * 第三方数接口入库service
 *
 * @author yxp
 * @date 2021-03-26
 **/
@Slf4j
@Service("apiDbService")
public class ApiDbService {
    /**
     * 列英文名
     */
    public static final String COLUMN_EN = "columnEn";
    /**
     * 多数据源切换Service
     */
    @Autowired
    private DynamicDataSourceService datasourceService;

    /**
     * 数据源配置Service
     */
    @Resource
    private SysDatasourceConfigService datasourceConfigService;

    /**
     * 脚本工作台Service
     */
    @Autowired
    private SysScriptWorkbenchService sysScriptWorkbenchService;
    /**
     * 指标基础查询
     */
    @Resource
    private BaseQueryMapper baseQueryMapper;

    /**
     * 根据数据源查询所有表
     *
     * @param dataSourceId 数据源配置
     * @return ResultEntity
     */
    public ResultEntity getTables(String dataSourceId) {
        final SysDatasourceConfigEntity datasourceConfig = datasourceConfigService.convertRecordToMap(SysDatasourceConfigEntity.builder().datasourceId(dataSourceId).build()).get(dataSourceId);
        final AbstractDbService db = DbFactory.getDb(datasourceConfig.getDatabaseType());
        String sql = db.getTablesSql();
        final List<Map<String, Object>> baseQuery = queryResult(sql, datasourceConfig.getDatasourceId());
        return ResultUtil.success(GlobalEnum.QuerySuccess, baseQuery);
    }

    /**
     * 根据数据源表查询字段信息
     *
     * @param dataSourceId 数据源ID
     * @param tableName    表名
     * @return ResultEntity
     */
    public ResultEntity getColumns(String dataSourceId, String tableName) {
        final SysDatasourceConfigEntity datasourceConfig = datasourceConfigService.convertRecordToMap(SysDatasourceConfigEntity.builder().datasourceId(dataSourceId).build()).get(dataSourceId);
        final AbstractDbService db = DbFactory.getDb(datasourceConfig.getDatabaseType());
        String sql = db.getColumnsSql(tableName);
        List<Map<String, Object>> columnList = queryResult(sql, dataSourceId);
        final String dataSql = db.getDataSql(tableName);
        final List<Map<String, Object>> dataList = queryResult(dataSql, datasourceConfig.getDatasourceId());
        if (dataList != null && dataList.size() > 0) {
            final Map<String, Object> dataMap = dataList.get(0);
            columnList.stream().forEach(x -> {
                x.put("tableData", dataMap.get(x.get(COLUMN_EN)));
            });
            columnList = columnList.stream().filter(e -> !Q_BATCH_TIME.equalsIgnoreCase(e.getOrDefault(COLUMN_EN, "").toString())).collect(Collectors.toList());

        }
        return ResultUtil.success(GlobalEnum.QuerySuccess, columnList);
    }

    /**
     * 查询结果
     *
     * @param sql          查询sql
     * @param datasourceId 查询数据源id
     * @return
     */
    private List<Map<String, Object>> queryResult(String sql, String datasourceId) {
        Map<String, Object> queryMap = new HashMap<>(1);
        queryMap.put(QUERY_SQL_KEY, sql);
        try {
            datasourceService.changeDb(datasourceId);
        } catch (Exception e) {
            log.error("切换数据源发生错误:{}", e.getMessage());
            GlobalUtils.convertMessage(GlobalEnum.DataEmpty);
        }
        return baseQueryMapper.baseQuery(queryMap);
    }

    /**
     * api接口列数据信息
     *
     * @param functionId 脚本函数id
     * @return
     */
    public ResultEntity apiData(String functionId) {
        final List list = exeScript(ApiDbEntity.builder().scriptId(functionId).build());
        return ResultUtil.success(GlobalEnum.QuerySuccess, list);
    }

    /**
     * 执行脚本 函数
     *
     * @param apiDbEntity api-db实体类
     * @return
     */
    public List exeScript(ApiDbEntity apiDbEntity) {
        List resultList = Lists.newArrayList();
        long beginTime = System.currentTimeMillis();
        log.info("表:{},执行完成,耗时:{}ms,数据条数:{}", apiDbEntity.getTableName(), (System.currentTimeMillis() - beginTime), resultList.size());
        return resultList;
    }

    /**
     * 执行脚本 函数
     *
     * @param apiDbEntity api-db实体类
     * @return ResultEntity
     */
    public ResultEntity apiToDb(ApiDbEntity apiDbEntity) {
        try {
            //1.查询数据源 根据数据源类型创建dbService
            String dbId = apiDbEntity.getDbId();
            final SysDatasourceConfigEntity datasourceConfig = datasourceConfigService.convertRecordToMap(SysDatasourceConfigEntity.builder().datasourceId(dbId).build()).get(dbId);
            AbstractDbService db = DbFactory.getDb(datasourceConfig.getDatabaseType());
            //2. 查询字段 判断是否存在批次字段
            String columnSql = db.getColumnsSql(apiDbEntity.getTableName());
            List<Map<String, Object>> columnList = queryResult(columnSql, dbId);
            AtomicBoolean isHasBatchColumn = new AtomicBoolean(false);
            columnList.stream().forEach(x -> {
                if (Q_BATCH_TIME.equalsIgnoreCase(x.getOrDefault(COLUMN_EN, EMPTY_STRING).toString())) {
                    isHasBatchColumn.set(true);
                }
            });
            //添加批次字段
            if (!isHasBatchColumn.get()) {
                final String insertSql = db.addBatchColumn(apiDbEntity.getTableName());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(QUERY_SQL_KEY, insertSql);
                baseQueryMapper.insert(jsonObject);
            }
            //3. 执行脚本函数
            final List resultList = exeScript(apiDbEntity);
            if (resultList.size() == 0) {
                return ResultUtil.error(GlobalUtils.convertMsg(GlobalEnum.DataEmpty, dbId));
            }
            //4. 根据插入模型，返回结果入库
            String sql;
            if (AbstractDbService.ModelInsertEnum.IGNORE_INSERT.getMsg().equalsIgnoreCase(apiDbEntity.getInsertModel())) {
                //忽略插入
                sql = db.batchInsertSql(apiDbEntity.getTableName(), apiDbEntity.getTableApiConfigs(), true);
            } else if (AbstractDbService.ModelInsertEnum.UPDATE.getMsg().equalsIgnoreCase(apiDbEntity.getInsertModel())) {
                //更新数据
                sql = db.batchUpdateSql(apiDbEntity.getTableName(), apiDbEntity.getTableApiConfigs());
            } else {
                //直接插入
                sql = db.batchInsertSql(apiDbEntity.getTableName(), apiDbEntity.getTableApiConfigs(), false);
            }
            Long beginTime = System.currentTimeMillis();
            //切换数据源
            boolean changeFlag;
            try {
                changeFlag = datasourceService.changeDb(dbId);
            } catch (Exception e) {
                log.error("切换数据源发生错误:{}", e.getMessage());
                return ResultUtil.error(GlobalUtils.convertMsg(GlobalEnum.DataBaseError, dbId));
            }
            log.info("数据源:{},切换数据源耗时:{}ms", dbId, (System.currentTimeMillis() - beginTime));
            if (!changeFlag) {
                return ResultUtil.error(GlobalUtils.convertMsg(GlobalEnum.DataBaseError, dbId));
            }
            JSONObject jsonObject = new JSONObject(2);
            jsonObject.put("list", resultList);
            jsonObject.put(QUERY_SQL_KEY, sql);
            Integer count = 0;
            if (AbstractDbService.ModelInsertEnum.UPDATE.getMsg().equalsIgnoreCase(apiDbEntity.getInsertModel())) {
                //更新数据
                count = this.baseQueryMapper.update(jsonObject);
            } else {
                //插入数据
                count = this.baseQueryMapper.insert(jsonObject);
            }
            return ResultUtil.msg(count);
        } catch (Exception e) {
            log.error("数据入库异常!", e);
            return ResultUtil.error(GlobalEnum.MsgOperationFailed);
        }

    }
}
