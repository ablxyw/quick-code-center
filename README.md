# 代码集成中心

1. quick-code-core

   公共代码中心

2. quick-code-job

   分布式定时任务模块，可以单独使用，配置文件如下

   ```yaml
   spring:
     quartz:
       # 参见 org.springframework.boot.autoconfigure.quartz.QuartzProperties
       job-store-type: jdbc
       wait-for-jobs-to-complete-on-shutdown: true
       scheduler-name: q_frame_job
       auto-startup: true
       startupDelay: 1
       tablePrefix: quick_
       properties:
         #设置为TRUE不会出现序列化非字符串类到 BLOB 时产生的类版本问题
         org.quartz.jobStore.useProperties: true
         #开启分布式部署
         org.quartz.jobStore.isClustered: true
         #quartz相关数据表前缀名
         org.quartz.jobStore.tablePrefix:  ${spring.quartz.tablePrefix}
         #事务隔离级别为“读已提交”
         org.quartz.jobStore.txIsolationLevelReadCommitted: true
         #分布式节点有效性检查时间间隔，单位：毫秒
         org.quartz.jobStore.clusterCheckinInterval: 20000
         #配置线程池实现类
         org.quartz.jobStore.class: org.quartz.impl.jdbcjobstore.JobStoreTX
         org.quartz.jobStore.driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
   
         org.quartz.scheduler.instanceName: q_frame_job
         org.quartz.scheduler.instanceId: AUTO
         #如果你想quartz-scheduler出口本身通过RMI作为服务器，然后设置“出口”标志true(默认值为false)。
         org.quartz.scheduler.rmi.export: false
         #true:链接远程服务调度(客户端),这个也要指定registryhost和registryport，默认为false
         # 如果export和proxy同时指定为true，则export的设置将被忽略
         org.quartz.scheduler.rmi.proxy: false
         org.quartz.scheduler.wrapJobExecutionInUserTransaction: false
   
         org.quartz.jobStore.misfireThreshold: 5000
         # 在调度流程的第一步，也就是拉取待即将触发的triggers时，是上锁的状态，即不会同时存在多个线程拉取到相同的trigger的情况，也就避免的重复调度的危险。参考：https://segmentfault.com/a/1190000015492260
         org.quartz.jobStore.acquireTriggersWithinLock: true
   
   
   ```

3. 使用

   ```xml
   <!--quick-code-core-->
   <dependency>
     <groupId>cn.ablxyw</groupId>
     <artifactId>quick-code-core</artifactId>
     <version>${last.version}</version>
   </dependency>
   ```

   



