package cn.ablxyw.config;//package cn.ablxyw.config;
//
//import cn.ablxyw.factory.QuickAdaptableJobFactory;
//import org.quartz.Scheduler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.PropertiesFactoryBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.util.Properties;
//
//@Configuration
//public class SchedulerConfig {
//
//    @Autowired
//    private DataSource dataSource;
//
//    /**
//     * 线程池
//     */
//    @Autowired
//    private ThreadPoolTaskExecutor defaultThreadPool;
//
//    /**
//     * Job Factory
//     */
//    @Autowired
//    private QuickAdaptableJobFactory quickAdaptableJobFactory;
//
//
//    /**
//     * 实例化Scheduler
//     *
//     * @return Scheduler
//     * @throws Exception
//     */
//    @Bean
//    public Scheduler scheduler() throws Exception {
//        Scheduler scheduler = schedulerFactoryBean().getScheduler();
//        scheduler.start();
//        return scheduler;
//    }
//
//    /**
//     * 设置定时任务
//     *
//     * @return SchedulerFactoryBean
//     * @throws IOException
//     */
//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
//        SchedulerFactoryBean factory = new SchedulerFactoryBean();
//        //开启更新job
//        factory.setOverwriteExistingJobs(true);
//        //如果不配置就会使用quartz.properties中的instanceName
////        factory.setSchedulerName("Cluster_Scheduler");
//        //配置数据源,这是quartz使用的表的数据库存放位置
//        factory.setDataSource(dataSource);
//        //设置实例在spring容器中的key
//        factory.setApplicationContextSchedulerContextKey("applicationContext");
//        //配置线程池
//        factory.setTaskExecutor(defaultThreadPool);
//        //配置配置文件
//        factory.setQuartzProperties(quartzProperties());
//        //设置调度器自动运行
//        factory.setAutoStartup(true);
//        //配置任务执行规则,参数是一个可变数组
//        //factory.setTriggers(trigger1().getObject());
//        // 解决mapper无法注入问题，此处配合第四步的配置。
//        factory.setJobFactory(quickAdaptableJobFactory);
//        return factory;
//    }
//
//    /**
//     * 加载 quartz配置文件
//     *
//     * @return Properties
//     * @throws IOException
//     */
//    @Bean
//    public Properties quartzProperties() throws IOException {
//        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.setLocation(new ClassPathResource("/config/quick-quartz.properties"));
//        // 在quartz.properties中的属性被读取并注入后再初始化对象
//        propertiesFactoryBean.afterPropertiesSet();
//        return propertiesFactoryBean.getObject();
//    }
//
//}
