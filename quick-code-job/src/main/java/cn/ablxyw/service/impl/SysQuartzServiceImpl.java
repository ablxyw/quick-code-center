package cn.ablxyw.service.impl;

import cn.ablxyw.constants.JobEnum;
import cn.ablxyw.entity.SysJobConfigEntity;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.mapper.SysJobConfigMapper;
import cn.ablxyw.service.SysQuartzService;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.ablxyw.constants.JobConstants.CRON_INTERVAL_EMPTY;


/**
 * 分布式定时任务ServiceImpl
 *
 * @author weiqiang
 * @date 2021-01-29 下午3:29
 */
@Slf4j
@Service("sysQuartzService")
public class SysQuartzServiceImpl implements SysQuartzService {
    /**
     * 分布式定时任务配置
     */
    @Resource
    private SysJobConfigMapper sysJobConfigMapper;
    /**
     * Scheduler
     */
    @Autowired
    private Scheduler scheduler;
    /**
     * 任务表前缀
     */
    @Value("${spring.quartz.tablePrefix:quick_}")
    private String talePrefix;

    /**
     * 创建定时任务Simple
     * sysQuartzEntity.getInterval()==null表示单次提醒，
     * 否则循环提醒（sysQuartzEntity.getEndTime()!=null）
     *
     * @param sysJobConfigEntity 分布式定时任务配置类
     */
    @Override
    public void createScheduleJobSimple(SysJobConfigEntity sysJobConfigEntity) throws Exception {
        //获取到定时任务的执行类  必须是类的绝对路径名称
        //定时任务类需要是job类的具体实现 QuartzJobBean是job的抽象类。
        Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(sysJobConfigEntity.getJobClass());
        // 构建定时任务信息
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(sysJobConfigEntity.getJobName(), StringUtils.isNotEmpty(sysJobConfigEntity.getJobGroup()) ? sysJobConfigEntity.getJobGroup() : null)
                .setJobData(sysJobConfigEntity.getDataMap())
                .build();
        // 设置定时任务执行方式
        SimpleScheduleBuilder simpleScheduleBuilder = null;
        //单次
        if (sysJobConfigEntity.getInterval() == null) {
            simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
            //循环
        } else {
            simpleScheduleBuilder = SimpleScheduleBuilder.repeatMinutelyForever(sysJobConfigEntity.getInterval());
        }
        // 构建触发器trigger
        Trigger trigger = null;
        //单次
        if (sysJobConfigEntity.getInterval() == null) {
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(sysJobConfigEntity.getJobName(), StringUtils.isNotEmpty(sysJobConfigEntity.getJobGroup()) ? sysJobConfigEntity.getJobGroup() : null)
                    .withSchedule(simpleScheduleBuilder)
                    .startAt(sysJobConfigEntity.getStartTime())
                    .build();
        } else { //循环
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(sysJobConfigEntity.getJobName(), StringUtils.isNotEmpty(sysJobConfigEntity.getJobGroup()) ? sysJobConfigEntity.getJobGroup() : null)
                    .withSchedule(simpleScheduleBuilder)
                    .startAt(sysJobConfigEntity.getStartTime())
                    .endAt(sysJobConfigEntity.getEndTime())
                    .build();
        }
        scheduler.scheduleJob(jobDetail, trigger);
        ResultUtil.success(GlobalEnum.InsertSuccess);
    }

    /**
     * 创建定时任务Cron
     * 定时任务创建之后默认启动状态
     *
     * @param sysJobConfigEntity 定时任务信息类
     * @return ResultEntity
     * @throws Exception
     */
    @Override
    public ResultEntity createScheduleJobCron(SysJobConfigEntity sysJobConfigEntity) throws Exception {
        //获取到定时任务的执行类  必须是类的绝对路径名称
        //定时任务类需要是job类的具体实现 QuartzJobBean是job的抽象类。
        Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(sysJobConfigEntity.getJobClass());
        // 构建定时任务信息
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(sysJobConfigEntity.getJobName(), sysJobConfigEntity.getJobGroup())
                .setJobData(sysJobConfigEntity.getDataMap()).build();
        // 设置定时任务执行方式
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(sysJobConfigEntity.getCronExpression());
        // 构建触发器trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(sysJobConfigEntity.getJobName(), sysJobConfigEntity.getJobGroup()).withSchedule(scheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, trigger);
        return ResultUtil.success(GlobalEnum.InsertSuccess);
    }

    /**
     * 根据任务名称暂停定时任务
     *
     * @param jobName  定时任务名称
     * @param jobGroup 任务组（没有分组传值null）
     * @return ResultEntity
     * @throws Exception
     */
    @Override
    public ResultEntity pauseScheduleJob(String jobName, String jobGroup) throws Exception {
        JobKey jobKey = JobKey.jobKey(jobName, StringUtils.isNotEmpty(jobGroup) ? jobGroup : null);
        scheduler.pauseJob(jobKey);
        return ResultUtil.success(GlobalEnum.MsgOperationSuccess);
    }

    /**
     * 根据任务名称恢复定时任务
     *
     * @param jobName  定时任务名
     * @param jobGroup 任务组（没有分组传值null）
     * @return ResultEntity
     * @throws SchedulerException
     */
    @Override
    public ResultEntity resumeScheduleJob(String jobName, String jobGroup) throws Exception {
        JobKey jobKey = JobKey.jobKey(jobName, StringUtils.isNotEmpty(jobGroup) ? jobGroup : null);
        scheduler.resumeJob(jobKey);
        return ResultUtil.success(GlobalEnum.MsgOperationSuccess);
    }

    /**
     * 根据任务名称立即运行一次定时任务
     *
     * @param jobName  定时任务名称
     * @param jobGroup 任务组（没有分组传值null）
     * @return ResultEntity
     * @throws SchedulerException
     */
    @Override
    public ResultEntity runOnce(String jobName, String jobGroup) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
            scheduler.triggerJob(jobKey);
        } catch (Exception e) {
            log.error(JobEnum.JobClassNotFound.getMessage());
        }
        return ResultUtil.success(GlobalEnum.MsgOperationSuccess);
    }

    /**
     * 更新定时任务Simple
     *
     * @param sysJobConfigEntity 定时任务信息类
     * @return ResultEntity
     * @throws SchedulerException
     */
    public ResultEntity updateScheduleJobSimple(SysJobConfigEntity sysJobConfigEntity) throws Exception {
        //获取到对应任务的触发器
        TriggerKey triggerKey = TriggerKey.triggerKey(sysJobConfigEntity.getJobName(), sysJobConfigEntity.getJobGroup());
        // 设置定时任务执行方式
        SimpleScheduleBuilder simpleScheduleBuilder;
        //单次
        if (sysJobConfigEntity.getInterval() == null) {
            simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
            //循环
        } else {
            simpleScheduleBuilder = SimpleScheduleBuilder.repeatMinutelyForever(sysJobConfigEntity.getInterval());
        }
        // 构建触发器trigger
        Trigger trigger;
        //单次
        if (sysJobConfigEntity.getInterval() == null) {
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(sysJobConfigEntity.getJobName(), sysJobConfigEntity.getJobGroup())
                    .usingJobData(sysJobConfigEntity.getDataMap())
                    .withSchedule(simpleScheduleBuilder)
                    .startAt(sysJobConfigEntity.getStartTime())
                    .build();
            //循环
        } else {
            trigger = TriggerBuilder.newTrigger()
                    .withIdentity(sysJobConfigEntity.getJobName(), sysJobConfigEntity.getJobGroup())
                    .withSchedule(simpleScheduleBuilder)
                    .usingJobData(sysJobConfigEntity.getDataMap())
                    .startAt(sysJobConfigEntity.getStartTime())
                    .endAt(sysJobConfigEntity.getEndTime())
                    .build();
        }
        //重置对应的job
        scheduler.rescheduleJob(triggerKey, trigger);
        return ResultUtil.success(GlobalEnum.UpdateSuccess);
    }

    /**
     * 更新定时任务Cron
     *
     * @param sysJobConfigEntity 定时任务信息类
     * @return ResultEntity
     * @throws SchedulerException
     */
    public ResultEntity updateScheduleJobCron(SysJobConfigEntity sysJobConfigEntity) throws Exception {
        //获取到对应任务的触发器
        TriggerKey triggerKey = TriggerKey.triggerKey(sysJobConfigEntity.getJobName(), sysJobConfigEntity.getJobGroup());
        //设置定时任务执行方式
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(sysJobConfigEntity.getCronExpression());
        //重新构建任务的触发器trigger
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder)
                .usingJobData(sysJobConfigEntity.getDataMap())
                .build();
        //重置对应的job
        scheduler.rescheduleJob(triggerKey, trigger);
        return ResultUtil.success(GlobalEnum.UpdateSuccess);
    }

    /**
     * 根据定时任务名称从调度器当中删除定时任务
     *
     * @param jobName  定时任务名称
     * @param jobGroup 任务组（没有分组传值null）
     * @return ResultEntity
     * @throws SchedulerException
     */
    @Override
    public ResultEntity deleteScheduleJob(String jobName, String jobGroup) throws Exception {
        JobKey jobKey = JobKey.jobKey(jobName, StringUtils.isNotEmpty(jobGroup) ? jobGroup : null);
        pauseScheduleJob(jobName, jobGroup);
        scheduler.deleteJob(jobKey);
        return ResultUtil.success(GlobalEnum.DeleteSuccess);
    }

    /**
     * 查询所有的任务
     *
     * @param sysJobConfig 分布式定时任务配置类
     * @return ResultEntity
     */
    @Override
    public ResultEntity list(SysJobConfigEntity sysJobConfig) {
        List<SysJobConfigEntity> jobConfigEntities = sysJobConfigMapper.list(sysJobConfig);
        return ResultUtil.success(GlobalEnum.QuerySuccess, convertResult(jobConfigEntities));
    }

    /**
     * 分页查询分布式定时任务配置
     *
     * @param sysJobConfig 分布式定时任务配置
     * @param pageNum      初始页
     * @param pageSize     每页条数
     * @param sortName     排序信息
     * @param sortOrder    排序顺序
     * @return ResultEntity
     */
    @Override
    public ResultEntity list(SysJobConfigEntity sysJobConfig, Integer pageNum, Integer pageSize, String sortName, String sortOrder) {
        PageHelper.startPage(pageNum, pageSize);
        String sort = GlobalUtils.changeColumn(sortName, sortOrder);
        sysJobConfig.setSort(sort);
        List<SysJobConfigEntity> sysJobConfigList = sysJobConfigMapper.list(sysJobConfig);
        sysJobConfigList = convertResult(sysJobConfigList);
        PageInfo pageInfo = new PageInfo(sysJobConfigList);
        return ResultUtil.success(GlobalEnum.QuerySuccess, pageInfo);
    }

    /**
     * 转换分布式定时任务配置查询结果
     *
     * @param sysJobConfigList 分布式定时任务配置集合
     * @return List
     */
    private List<SysJobConfigEntity> convertResult(List<SysJobConfigEntity> sysJobConfigList) {
        if (Objects.isNull(sysJobConfigList) || sysJobConfigList.isEmpty()) {
            return Lists.newArrayList();
        }
        sysJobConfigList.forEach(sysJobConfig -> {
            String jobName = sysJobConfig.getJobName();
            String jobGroup = sysJobConfig.getJobGroup();
            try {
                Trigger.TriggerState state = scheduler.getTriggerState(TriggerKey.triggerKey(jobName, jobGroup));
                sysJobConfig.setRunStatus(state.name());
            } catch (Exception e) {
                log.error("获取状态失败:{}", e.getMessage());
            }
        });
        return sysJobConfigList;
    }

    /**
     * 获取任务状态
     *
     * @param jobName  任务名称
     * @param jobGroup 任务组（没有分组传值null）
     * @return (" BLOCKED ", " 阻塞 ");
     * ("COMPLETE", "完成");
     * ("ERROR", "出错");
     * ("NONE", "不存在");
     * ("NORMAL", "正常");
     * ("PAUSED", "暂停");
     */
    @Override
    public ResultEntity getScheduleJobStatus(String jobName, String jobGroup) throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, StringUtils.isNotEmpty(jobGroup) ? jobGroup : null);
        Trigger.TriggerState state = scheduler.getTriggerState(triggerKey);
        Map<String, Object> jobMap = new HashMap<String, Object>(3) {{
            put("jobName", jobName);
            put("jobGroup", jobGroup);
            put("jobState", state.name());
        }};
        return ResultUtil.success(GlobalEnum.QuerySuccess, Lists.newArrayList(jobMap));
    }

    /**
     * 创建定时任务并启动
     *
     * @param sysJobConfigEntity 定时任务
     * @return ResultEntity
     */
    @Override
    public ResultEntity insert(SysJobConfigEntity sysJobConfigEntity) {
        String cronExpression = sysJobConfigEntity.getCronExpression();
        ResultEntity resultEntity = verifyParam(sysJobConfigEntity);
        if (!resultEntity.isSuccess()) {
            return resultEntity;
        }
        try {
            if (StringUtils.isNotBlank(cronExpression)) {
                createScheduleJobCron(sysJobConfigEntity);
            } else {
                if (Objects.isNull(sysJobConfigEntity.getStartTime())) {
                    sysJobConfigEntity.setStartTime(new Date());
                }
                createScheduleJobSimple(sysJobConfigEntity);
            }
        } catch (Exception e) {
            log.error("创建定时任务发生错误:{}", e.getMessage());
            return ResultUtil.error(GlobalUtils.convertMsg(GlobalEnum.ExceptionMessage, e.getMessage()));
        }
        sysJobConfigEntity.setId(GlobalUtils.ordinaryId());
        Integer insertCount = sysJobConfigMapper.batchInsert(Lists.newArrayList(sysJobConfigEntity));
        return ResultUtil.msg(insertCount);
    }


    /**
     * 修改分布式定时任务配置
     *
     * @param sysJobConfig 分布式定时任务配置
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity update(SysJobConfigEntity sysJobConfig) {
        String cronExpression = sysJobConfig.getCronExpression();
        ResultEntity resultEntity = verifyParam(sysJobConfig);
        if (!resultEntity.isSuccess()) {
            return resultEntity;
        }
        if (Objects.isNull(sysJobConfig.getStartTime())) {
            sysJobConfig.setStartTime(new Date());
        }
        //经过测试发现如果修改了JobData数据不会重新加载,所以采用删除重新创建
        try {
            String id = sysJobConfig.getId();
            SysJobConfigEntity oldConfig = sysJobConfigMapper.findById(id);
            String jobName = oldConfig.getJobName();
            String jobGroup = oldConfig.getJobGroup();
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, StringUtils.isNotEmpty(jobGroup) ? jobGroup : null);
            Trigger.TriggerState state = scheduler.getTriggerState(triggerKey);
            deleteScheduleJob(jobName, jobGroup);
            if (StringUtils.isNotBlank(cronExpression)) {
                createScheduleJobCron(sysJobConfig);
            } else {
                createScheduleJobSimple(sysJobConfig);
            }
            if (Objects.equals(state, Trigger.TriggerState.PAUSED)) {
                pauseScheduleJob(jobName, jobGroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建定时任务发生错误:{}", e.getMessage());
            return ResultUtil.error(GlobalUtils.convertMsg(GlobalEnum.ExceptionMessage, e.getMessage()));
        }
        List<SysJobConfigEntity> sysJobConfigList = Lists.newArrayList(sysJobConfig);
        Integer updateCount = sysJobConfigMapper.batchUpdate(sysJobConfigList);
        return ResultUtil.msg(updateCount);
    }

    /**
     * 删除分布式定时任务配置
     *
     * @param ids 分布式定时任务配置主键集合
     * @return ResultEntity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public ResultEntity delete(List<String> ids) {
        Map<String, SysJobConfigEntity> configEntityMap = sysJobConfigMapper.list(null).stream().collect(Collectors.toMap(SysJobConfigEntity::getId, Function.identity(), (o1, o2) -> o2));
        ids.stream().filter(id -> configEntityMap.containsKey(id)).forEach(id -> {
            SysJobConfigEntity configEntity = configEntityMap.get(id);
            String jobName = configEntity.getJobName();
            String jobGroup = configEntity.getJobGroup();
            try {
                deleteScheduleJob(jobName, jobGroup);
            } catch (Exception e) {
                log.error("删除任务,任务名称:{},任务组:{},失败:{}", jobName, jobGroup, e.getMessage());
            }
        });
        Integer deleteCount = sysJobConfigMapper.batchDelete(ids);
        return ResultUtil.msg(deleteCount);
    }

    /**
     * 根据定时任务名称来判断任务是否存在
     *
     * @param jobName  定时任务名称
     * @param jobGroup 任务组（没有分组传值null）
     * @return Boolean
     * @throws Exception
     */
    @Override
    public Boolean checkExistsScheduleJob(String jobName, String jobGroup) throws Exception {
        JobKey jobKey = JobKey.jobKey(jobName, StringUtils.isNotEmpty(jobGroup) ? jobGroup : null);
        return scheduler.checkExists(jobKey);
    }

    /**
     * 根据任务组刪除定时任务
     *
     * @param jobGroup 任务组
     * @return Boolean
     * @throws SchedulerException
     */
    public Boolean deleteGroupJob(String jobGroup) throws Exception {
        GroupMatcher<JobKey> matcher = GroupMatcher.groupEquals(jobGroup);
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        List<JobKey> jobKeyList = new ArrayList<JobKey>();
        jobKeyList.addAll(jobKeys);
        return scheduler.deleteJobs(jobKeyList);
    }

    /**
     * 根据任务组批量刪除定时任务
     *
     * @param jobKeys 定时任务
     * @return Boolean
     * @throws SchedulerException
     */
    public Boolean batchDeleteGroupJob(List<JobKey> jobKeys) throws Exception {
        return scheduler.deleteJobs(jobKeys);
    }

    /**
     * 根据任务组批量查询出jobKey
     *
     * @param jobGroup 任务组
     * @return List
     * @throws SchedulerException
     */
    public List<JobDetail> batchQueryGroupJob(String jobGroup) throws Exception {
        List<JobDetail> jobDetails = Lists.newArrayList();
        GroupMatcher matcher = GroupMatcher.groupEquals(jobGroup);
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        for (JobKey jobKey : jobKeys) {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            jobDetails.add(jobDetail);
        }
        return jobDetails;
    }

    /**
     * 检验请求参数
     *
     * @param sysJobConfigEntity 分布式定时任务配置类
     * @return ResultEntity
     */
    private ResultEntity verifyParam(SysJobConfigEntity sysJobConfigEntity) {
        String cronExpression = sysJobConfigEntity.getCronExpression();
        Integer interval = sysJobConfigEntity.getInterval();
        boolean cornErrorFlag = StringUtils.isBlank(cronExpression) && (Objects.isNull(interval) || interval <= 0);
        if (cornErrorFlag) {
            return ResultUtil.error(CRON_INTERVAL_EMPTY);
        }
        try {
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(sysJobConfigEntity.getJobClass());
        } catch (Exception e) {
            log.error("任务类不存在:{}", e.getMessage());
            return ResultUtil.error(JobEnum.JobClassNotFound.getMessage());
        }
        if (StringUtils.isNotBlank(cronExpression)) {
            //校验corn表达式
            boolean isCorn = CronExpression.isValidExpression(cronExpression);
            if (!isCorn) {
                return ResultUtil.error(JobEnum.CornParamError.getMessage());
            }
        }
        Date startTime = sysJobConfigEntity.getStartTime();
        Date endTime = sysJobConfigEntity.getEndTime();
        if (Objects.nonNull(startTime) && Objects.nonNull(endTime) && endTime.getTime() <= startTime.getTime()) {
            return ResultUtil.error(JobEnum.EndTimeBeforeStartTime.getMessage());
        }
        return ResultUtil.success(GlobalEnum.MsgOperationSuccess);
    }
}
