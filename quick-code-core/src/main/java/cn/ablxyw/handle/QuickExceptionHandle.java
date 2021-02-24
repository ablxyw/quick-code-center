package cn.ablxyw.handle;

import cn.ablxyw.config.AsyncRequestLogConfig;
import cn.ablxyw.entity.SysInterfaceRequestEntity;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Objects;

import static cn.ablxyw.aspect.CommonAspect.*;

/**
 * 异常统一处理
 *
 * @author weiQiang
 * @date 2020-01-10
 */
@Slf4j
@RestControllerAdvice
public class QuickExceptionHandle {

    /**
     * 异步插入或更新动态接口日志
     */
    @Autowired
    private AsyncRequestLogConfig asyncRequestLogConfig;

    /**
     * 转换异常返回信息
     *
     * @param request   请求
     * @param exception 异常
     * @return Object
     */
    public Object convertReturn(HttpServletRequest request, Exception exception) {
        if (GlobalUtils.isAjaxRequest(request)) {
            return ResultUtil.error(exception.getMessage());
        } else {
            String path = request.getRequestURL().toString();
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("message", exception.getMessage());
            modelAndView.addObject("status", 500);
            modelAndView.addObject("timestamp", new Date());
            modelAndView.addObject("path", path);
            modelAndView.setViewName("error");
            return modelAndView;
        }
    }

    /**
     * 统一异常处理
     *
     * @param e        异常
     * @param request  请求
     * @param response 响应
     * @return ResultEntity
     */
    @ExceptionHandler(value = Exception.class)
    public ResultEntity handle(Exception e, HttpServletRequest request, HttpServletResponse response) {
        log.error("错误信息:", e);
        String path = request.getRequestURL().toString();
        String message = e.getMessage();
        if (e instanceof BindException) {
            message = GlobalEnum.TokenParamError.getMessage();
        }
        if (e.getStackTrace().length > 0) {
            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            int lineNumber = stackTraceElement.getLineNumber();
            log.error("请求路径:{},在:{},方法:{},行:{},发生了错误:{}", path, className, methodName, lineNumber, message);
        }
        if (Objects.nonNull(REQUEST_ID) && Objects.nonNull(REQUEST_ID.get())) {
            Integer maxLength = 1000;
            if (StringUtils.isNotBlank(message) && message.length() > maxLength) {
                message = message.substring(0, maxLength - 1);
            }
            Long requestTime = 0L;
            if (Objects.nonNull(BEGIN_TIME) && Objects.nonNull(BEGIN_TIME.get())) {
                requestTime = System.currentTimeMillis() - BEGIN_TIME.get();
            }
            asyncRequestLogConfig.asyncUpdate(SysInterfaceRequestEntity.builder().requestId(REQUEST_ID.get()).forceConfigInsertLog(true).ignoreLog(IGNORE_LOG.get()).success(false).requestTime(requestTime).message(message).build());
        }
        return ResultUtil.error(message);
    }


}
