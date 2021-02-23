package cn.ablxyw.aspect;


import cn.ablxyw.config.AsyncRequestLogConfig;
import cn.ablxyw.entity.SysInterfaceRequestEntity;
import cn.ablxyw.service.SysTokenInfoService;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.vo.ResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.ablxyw.constants.GlobalConstants.*;

/**
 * AOP处理
 *
 * @author weiQiang
 * @date 2020-03-09
 */
@Slf4j
@Aspect
@Component
public class CommonAspect {

    /**
     * 请求Url
     */
    private static final ThreadLocal<String> REQUEST_URL = new ThreadLocal<>();
    /**
     * 开始时间,用于记录请求耗时
     */
    public static ThreadLocal<Long> BEGIN_TIME = new ThreadLocal<>();
    /**
     * 请求IP
     */
    public static ThreadLocal<String> REQUEST_IP = new ThreadLocal<>();
    /**
     * 请求ID
     */
    public static ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();
    /**
     * 忽略日志写入
     */
    public static ThreadLocal<Boolean> IGNORE_LOG = new ThreadLocal<>();
    /**
     * 请求port
     */
    public static ThreadLocal<Integer> REQUEST_PORT = new ThreadLocal<>();
    /**
     * request
     */
    public static ThreadLocal<HttpServletRequest> REQUEST_INFO = new ThreadLocal<>();
    /**
     * 忽略日志入库URI
     */
    @Value("#{'${qFrame.log.ignoreUrl:sys/requestBrowser,sys/requestErrorLog,sys/requestLog}'.split(',')}")
    private List<String> ignoreLogList;

    /**
     * serverPath
     */
    @Value("${server.servlet.context-path}")
    private String serverPath;

    /**
     * apiPrefix
     */
    @Value("${qFrame.apiPrefix:" + API_PREFIX + "}")
    private String apiPrefix;

    /**
     * 是否开启登录
     */
    @Value("${qFrame.login:true}")
    private boolean enableLogin;

    /**
     * 异步插入或更新动态接口日志
     */
    @Autowired
    private AsyncRequestLogConfig asyncRequestLogConfig;

    /**
     * token信息
     */
    @Autowired
    private SysTokenInfoService sysTokenInfoService;

    /**
     * 请求之后拦截
     */
    @After("doController()")
    public void doAfter() {

    }

    /**
     * 返回时拦截
     *
     * @param object
     */
    @AfterReturning(returning = "object", pointcut = "doController()")
    public void doAfterReturning(Object object) {
        long requestTime = System.currentTimeMillis() - BEGIN_TIME.get();
        try {
            if (null != object) {
                if (object instanceof ResultEntity) {
                    ((ResultEntity) object).setTotalTime(requestTime);
                    if (null != ((ResultEntity) object).getData() && (((ResultEntity) object).getData() instanceof ArrayList) && !((ResultEntity) object).isPageable()) {
                        if (Objects.isNull(((ResultEntity) object).getTotal()) || Objects.equals(((ResultEntity) object).getTotal(), 0L)) {
                            ((ResultEntity) object).setTotal((long) ((ResultEntity) object).getData().size());
                        }
                    }
                    List<?> resultData = ((ResultEntity) object).getData();
                    asyncRequestLogConfig.asyncUpdate(SysInterfaceRequestEntity.builder()
                            .requestId(REQUEST_ID.get())
                            .ignoreLog(IGNORE_LOG.get())
                            .success(((ResultEntity) object).isSuccess())
                            .message(((ResultEntity) object).getMessage())
                            .dataSize(Objects.nonNull(resultData) && !resultData.isEmpty() ? resultData.size() : 0)
                            .requestTime(requestTime)
                            .build());
                }
                log.debug("response:{}", object);
            } else {
                log.debug("response:{}", "无返回值");
            }
            log.info("请求ip:{},请求:{},耗时:{}ms", REQUEST_IP.get(), REQUEST_URL.get(), requestTime);
        } catch (Exception e) {
            log.error("请求结果转换发生错误:{}", e.getMessage());
        } finally {
            BEGIN_TIME.remove();
            REQUEST_URL.remove();
            IGNORE_LOG.remove();
            REQUEST_IP.remove();
            REQUEST_ID.remove();
            REQUEST_PORT.remove();
            REQUEST_INFO.remove();
        }

    }

    /**
     * 请求之前拦截
     *
     * @param joinPoint
     */
    @Before("doController()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        StringBuffer requestUrl = request.getRequestURL();
        BEGIN_TIME.set(System.currentTimeMillis());
        REQUEST_URL.set(requestUrl.toString());
        REQUEST_IP.set(GlobalUtils.getIpAddress(request));
        REQUEST_INFO.set(request);
        String requestUri = request.getRequestURI();
        String requestId = GlobalUtils.ordinaryId();
        REQUEST_ID.set(requestId);
        REQUEST_PORT.set(request.getRemotePort());
        String requestType = request.getMethod().toUpperCase();
        String browserName = GlobalUtils.getBrowserName(request);
        String browserVersion = GlobalUtils.getBrowserVersion(request);
        String osName = GlobalUtils.getOsName(request);
        requestUri = requestUri.replaceFirst(GlobalUtils.appendString(serverPath, SLASH_CODE), "");
        if (enableLogin) {
            //以apiPrefix开头的请求忽略token验证
            boolean ignoreCheckTokenUrl = requestUrl.toString().startsWith(HTTP_CODE + request.getServerName() + INTERVAL_COLON + request.getServerPort() + GlobalUtils.appendString(serverPath, SLASH_CODE, apiPrefix));
            if (!ignoreCheckTokenUrl) {
                GlobalUtils.checkRequestInfo(request, response, sysTokenInfoService);
            }
        }
        response.setHeader("Access-Control-Expose-Headers", "Authorization,token,newToken");
        //TODO 考虑以前前缀开启的情况，例如:sys/*，表示忽略sys下所以的接口
        IGNORE_LOG.set(ignoreLogList.contains(requestUri));
        log.debug("url:{},方法:{},请求ip:{},类和方法:{}(),参数:{}", requestUrl, request.getMethod(), REQUEST_IP.get(), joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(), joinPoint.getArgs());
        log.info("请求ip:{},请求:{},浏览器名称:{},浏览器版本号:{},系统名称:{}", REQUEST_IP.get(), requestUrl, GlobalUtils.getBrowserName(request), GlobalUtils.getBrowserVersion(request), GlobalUtils.getOsName(request));
        asyncRequestLogConfig.asyncInsert(SysInterfaceRequestEntity.builder()
                .requestId(requestId).requestUri(requestUri)
                .requestType(requestType)
                .ignoreLog(IGNORE_LOG.get())
                .clientIp(REQUEST_IP.get())
                .browserName(browserName)
                .browserVersion(browserVersion)
                .osName(osName)
                .success(false)
                .requestTime(0L)
                .build());
    }

    /**
     * 在抛出异常时执行
     *
     * @param throwing 异常
     */
    @AfterThrowing(pointcut = "doController()", throwing = "throwing")
    public void afterThrowing(Throwable throwing) {
        long requestTime = System.currentTimeMillis() - BEGIN_TIME.get();
        log.error("请求ip:{},请求:{},发生异常:{},耗时:{}ms", REQUEST_IP.get(), REQUEST_URL.get(), throwing.getMessage(), requestTime);
    }

    /**
     * 切点
     */
    @Pointcut(value = "execution(* cn.ablxyw..controller.*.*(..))")
    public void doController() {
    }
}
