package cn.ablxyw.ascept;

import cn.ablxyw.config.DataBaseContextHolder;
import cn.ablxyw.entity.SysInterfaceRequestEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.ablxyw.constants.GlobalConstants.UPDATE_CACHE_LIST;

/**
 * mapper切面
 *
 * @author weiqiang
 * @date 2020-01-14 11:28:44
 */
@Slf4j
@Aspect
@Component
public class MapperAspect {
    /**
     * 根据请求是否更新缓存
     */
    private static final ThreadLocal<Boolean> UPDATE_CACHE = new ThreadLocal<Boolean>();

    /**
     * 请求之前拦截
     *
     * @param joinPoint 参数
     */
    @Before("doMapper()")
    public void doBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        log.debug("参数:{}", args);
    }

    /**
     * 基础查询切换到主数据源中
     *
     * @param joinPoint 参数
     */
    @Before("baseMapper()")
    public void baseBefore(JoinPoint joinPoint) {
        DataBaseContextHolder.clearDataSource();
        String methodName = joinPoint.getSignature().getName();
        UPDATE_CACHE.set(false);
        Object[] joinPointArgs = joinPoint.getArgs();
        boolean flag = true;
        try {
            if (Objects.nonNull(joinPointArgs) && joinPointArgs.length > 0) {
                if (joinPointArgs[0] instanceof ArrayList) {
                    List arrayList = (ArrayList) (joinPointArgs[0]);
                    if (arrayList.size() > 0 && arrayList.get(0) instanceof SysInterfaceRequestEntity) {
                        flag = false;
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取参数类型发生错误:{}", e.getMessage());
        }
        if (UPDATE_CACHE_LIST.stream().anyMatch(sqlMethod -> Objects.equals(methodName, sqlMethod)) && flag) {
            UPDATE_CACHE.set(true);
        }
    }

    @AfterReturning("baseMapper()")
    public void baseAfter() {
        try {
            if (Objects.nonNull(UPDATE_CACHE.get()) && UPDATE_CACHE.get()) {
                log.info("基础配置已变更,开始更新字典,{}", UPDATE_CACHE.get());
            }
        } catch (Exception e) {
            log.error("刷新配置发生错误:{}", e.getMessage());
        } finally {
            UPDATE_CACHE.remove();
        }
    }

    @Pointcut(value = "execution(* cn.ablxyw.mapper.Sys*.*(..))")
    public void baseMapper() {
    }

    @Pointcut(value = "execution(* cn.ablxyw.mapper.BaseQueryMapper.*(..))")
    public void doMapper() {
    }
}
