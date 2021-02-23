package cn.ablxyw.service;

import cn.ablxyw.entity.SysJobConfigEntity;
import cn.ablxyw.vo.ResultEntity;

import java.util.List;

/**
 * 分布式定时任务Service
 *
 * @author weiqiang
 * @date 2021-01-29 下午5:39
 */
public interface SysQuartzService {

    /**
     * 创建定时任务Simple
     * sysQuartzEntity.getInterval()==null表示单次提醒，
     * 否则循环提醒（sysQuartzEntity.getEndTime()!=null）
     *
     * @param sysJobConfigEntity 分布式定时任务配置类
     * @throws Exception
     */
    void createScheduleJobSimple(SysJobConfigEntity sysJobConfigEntity) throws Exception;


    /**
     * 创建定时任务Cron
     * 定时任务创建之后默认启动状态
     *
     * @param sysJobConfigEntity 定时任务信息类
     * @return ResultEntity
     * @throws Exception
     */
    ResultEntity createScheduleJobCron(SysJobConfigEntity sysJobConfigEntity) throws Exception;

    /**
     * 根据任务名称暂停定时任务
     *
     * @param jobName  定时任务名称
     * @param jobGroup 任务组（没有分组传值null）
     * @return ResultEntity
     * @throws Exception
     */
    ResultEntity pauseScheduleJob(String jobName, String jobGroup) throws Exception;

    /**
     * 根据任务名称恢复定时任务
     *
     * @param jobName  定时任务名
     * @param jobGroup 任务组（没有分组传值null）
     * @return ResultEntity
     * @throws Exception
     */
    ResultEntity resumeScheduleJob(String jobName, String jobGroup) throws Exception;

    /**
     * 根据任务名称立即运行一次定时任务
     *
     * @param jobName  定时任务名称
     * @param jobGroup 任务组（没有分组传值null）
     * @return ResultEntity
     */
    ResultEntity runOnce(String jobName, String jobGroup);

    /**
     * 根据定时任务名称从调度器当中删除定时任务
     *
     * @param jobName  定时任务名称
     * @param jobGroup 任务组（没有分组传值null）
     * @return ResultEntity
     * @throws Exception
     */
    ResultEntity deleteScheduleJob(String jobName, String jobGroup) throws Exception;

    /**
     * 根据定时任务名称来判断任务是否存在
     *
     * @param jobName  定时任务名称
     * @param jobGroup 任务组（没有分组传值null）
     * @return Boolean
     * @throws Exception
     */
    Boolean checkExistsScheduleJob(String jobName, String jobGroup) throws Exception;

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
     * @throws Exception
     */
    ResultEntity getScheduleJobStatus(String jobName, String jobGroup) throws Exception;

    /**
     * 创建定时任务并启动
     *
     * @param sysJobConfigEntity 定时任务
     * @return ResultEntity
     */
    ResultEntity insert(SysJobConfigEntity sysJobConfigEntity);

    /**
     * 查询所有的任务
     *
     * @param quartzEntity 分布式定时任务配置类
     * @return ResultEntity
     * @throws Exception
     */
    ResultEntity list(SysJobConfigEntity quartzEntity) throws Exception;

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
    ResultEntity list(SysJobConfigEntity sysJobConfig, Integer pageNum, Integer pageSize, String sortName, String sortOrder);

    /**
     * 修改分布式定时任务配置
     *
     * @param sysJobConfig 分布式定时任务配置
     * @return ResultEntity
     */
    ResultEntity update(SysJobConfigEntity sysJobConfig);

    /**
     * 删除分布式定时任务配置
     *
     * @param ids 分布式定时任务配置主键集合
     * @return ResultEntity
     */
    ResultEntity delete(List<String> ids);
}
