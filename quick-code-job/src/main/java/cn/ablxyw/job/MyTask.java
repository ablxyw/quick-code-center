package cn.ablxyw.job;

import cn.ablxyw.constants.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 定时任务
 *
 * @author weiqiang
 * @date 2021-02-01
 */
@Slf4j
@Component
public class MyTask extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobKey jobKey = context.getJobDetail().getKey();
        //任务数据
        JobDataMap map = context.getJobDetail().getJobDataMap();
        String userId = map.getString("userId");
        log.info("SimpleJob says: {}, userId:{}, executing at:{}", jobKey, userId, LocalDateTime.now().format(DateTimeFormatter.ofPattern(GlobalConstants.DATE_TIME_FORMAT)));
    }
}
