package cn.ablxyw.config;

import cn.ablxyw.entity.SysInterfaceRequestEntity;
import cn.ablxyw.service.SysInterfaceRequestService;
import cn.ablxyw.vo.ResultEntity;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 异步插入或更新动态接口日志
 *
 * @author weiqiang
 * @date 2020-02-16 9:14 下午
 */
@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class AsyncRequestLogConfig {
    /**
     * 插入数据队列
     */
    private final Queue<SysInterfaceRequestEntity> insertEntities = new ConcurrentLinkedQueue<>();
    /**
     * 更新数据队列
     */
    private final Queue<SysInterfaceRequestEntity> updateEntities = new ConcurrentLinkedQueue<>();

    /**
     * 超时更新数据队列
     */
    private final Queue<SysInterfaceRequestEntity> reUpdateEntities = new ConcurrentLinkedQueue<>();
    /**
     * 默认不开启日志记录
     */
    @Value("${qFrame.log.enable:false}")
    private Boolean enableLog;
    /**
     * 接口请求日志Service
     */
    @Autowired
    private SysInterfaceRequestService sysInterfaceRequestService;

    /**
     * 异步插入接口请求日志
     *
     * @param sysInterfaceRequestEntity 接口请求日志
     */
    public void asyncInsert(SysInterfaceRequestEntity sysInterfaceRequestEntity) {
        if (!sysInterfaceRequestEntity.getForceConfigInsertLog()) {
            if (!enableLog) {
                return;
            }
        }
        if (sysInterfaceRequestEntity.getIgnoreLog()) {
            return;
        }
        sysInterfaceRequestEntity.setBeginTime(new Date());
        insertEntities.add(sysInterfaceRequestEntity);
    }

    /**
     * 异步更新接口请求日志
     *
     * @param sysInterfaceRequestEntity 接口请求日志
     */
    public void asyncUpdate(SysInterfaceRequestEntity sysInterfaceRequestEntity) {
        if (!sysInterfaceRequestEntity.getForceConfigInsertLog()) {
            if (!enableLog) {
                return;
            }
        }
        if (sysInterfaceRequestEntity.getIgnoreLog()) {
            return;
        }
        sysInterfaceRequestEntity.setEndTime(new Date());
        updateEntities.add(sysInterfaceRequestEntity);
    }

    /**
     * 定时录入查询数据
     */
    @Async
    @Scheduled(cron = "${qFrame.log.cron:0 */5 * * * ?}")
    public void insertOrUpdate() {
        log.info("开始消费日志数据");
        List<String> insertIds = Lists.newArrayList();
        try {
            int size = insertEntities.size();
            List<SysInterfaceRequestEntity> entities = Lists.newArrayList();
            for (int i = 0; i < size; i++) {
                SysInterfaceRequestEntity poll = insertEntities.poll();
                entities.add(poll);
            }
            log.info("开始插入日志数据:{}条", size);
            if (entities.size() > 0) {
                insertIds = entities.stream().map(SysInterfaceRequestEntity::getRequestId).sorted().collect(Collectors.toList());
                sysInterfaceRequestService.insert(entities);
            }
        } catch (Exception e) {
            log.error("批量增加数据发生错误:{}", e.getMessage());
        }
        //更新前先等待一秒
        try {
            int size = updateEntities.size();
            List<SysInterfaceRequestEntity> entities = Lists.newArrayList();
            for (int i = 0; i < size; i++) {
                SysInterfaceRequestEntity poll = updateEntities.poll();
                String requestId = poll.getRequestId();
                if (!insertIds.contains(requestId)) {
                    reUpdateEntities.add(poll);
                } else {
                    entities.add(poll);
                }
            }
            if (reUpdateEntities.size() > 0) {
                log.info("未执行完请求日志:{}条", reUpdateEntities.size());
            }
            updateEntities.addAll(reUpdateEntities);
            reUpdateEntities.clear();
            List<SysInterfaceRequestEntity> requestEntities = new ArrayList<>(entities.stream()
                    .collect(Collectors.toMap(SysInterfaceRequestEntity::getRequestId,
                            Function.identity(), (o1, o2) -> {
                                //开始合并
                                o2.setRequestParam(o1.getRequestParam());
                                o2.setQuerySql(o1.getQuerySql());
                                o2.setDatasourceId(o1.getDatasourceId());
                                return o2;
                            }))
                    .values());
            log.info("开始更新日志数据:{}条", requestEntities.size());
            if (entities.size() > 0) {
                ResultEntity resultEntity = sysInterfaceRequestService.update(requestEntities);
                log.info("更新日志返回状态:{},结果:{}", resultEntity.isSuccess(), resultEntity.getMessage());
            }
        } catch (Exception e) {
            log.error("批量增加数据发生错误:{}", e.getMessage());
        }
    }
}
