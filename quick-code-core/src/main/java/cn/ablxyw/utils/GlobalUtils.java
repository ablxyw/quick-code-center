package cn.ablxyw.utils;

import cn.ablxyw.entity.SysTokenInfo;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.service.SysTokenInfoService;
import cn.ablxyw.vo.ResultEntity;
import cn.hutool.core.codec.Base64;
import cn.hutool.script.ScriptUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import io.jsonwebtoken.*;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SignatureException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.ablxyw.constants.GlobalConstants.*;

/**
 * 工具类
 *
 * @author weiQiang
 * @date 2020-01-10
 */
@Slf4j
public class GlobalUtils {
    /**
     * 函数名正则
     */
    public static final Pattern FUNCTION_NAME_PATTERN = Pattern.compile("function(\\s\\S+\\w)");
    /**
     * linePattern
     */
    private static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");

    /**
     * java字段转数据库字段
     *
     * @param column    Java字段
     * @param direction 排序字段
     * @return String
     */
    public static String changeColumn(String column, String direction) {
        if (StringUtils.isNotBlank(column)) {
            if (StringUtils.isBlank(direction)) {
                direction = ORDER_ASC;
            }
            return appendString(tableColumn(column), SPACE, direction);
        }
        return "";
    }

    /**
     * java字段转数据库字段
     *
     * @param column Java字段
     * @return String
     */
    public static String tableColumn(String column) {
        StringBuilder columnBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(column)) {
            char[] chars = column.toCharArray();
            for (char aChar : chars) {
                if (Character.isUpperCase(aChar)) {
                    columnBuilder.append(UNDER_LINE);
                    columnBuilder.append(String.valueOf(aChar).toLowerCase());
                } else {
                    columnBuilder.append(aChar);
                }
            }
        }
        return columnBuilder.toString();
    }

    /**
     * 创建目录
     *
     * @param path
     * @return
     */
    public static String createDir(String path) {
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        path = winOrLinuxPath(path);
        File unZipFileDir = new File(path);
        if (!unZipFileDir.exists()) {
            unZipFileDir.mkdirs();
        }
        return path;
    }


    /**
     * 根据系统转换为windows格式或者linux格式
     *
     * @param path 路径
     * @return
     */
    public static String winOrLinuxPath(String path) {
        if (!isOsLinux()) {
            path = path.replace("/", "\\");
        }
        return path;
    }


    /**
     * 判断是否是linux系统
     *
     * @return
     */
    public static boolean isOsLinux() {
        Properties properties = System.getProperties();
        String os = properties.getProperty("os.name");
        return os != null && os.toLowerCase().indexOf(LINUX_NAME) > -1;
    }


    /**
     * 获取IP地址
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(INTERVAL_COMMA) && ip.split(INTERVAL_COMMA).length > 1) {
            ip = ip.split(INTERVAL_COMMA)[0];
        }
        return ip;
    }


    /**
     * 判断是否ajax请求.
     * 可以看到Ajax 请求多了个 x-requested-with ，可以利用它，
     * request.getHeader("x-requested-with"); 为 null，则为传统同步请求，为 XMLHttpRequest，则为Ajax 异步请求。
     *
     * @param request HttpServletRequest
     * @return 是否ajax请求.
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String xr = request.getHeader(X_REQUESTED_WIDTH);
        return (xr != null && XML_HTTP_REQUEST.equalsIgnoreCase(xr));
    }


    /**
     * 通用主键
     *
     * @return
     */
    public synchronized static String ordinaryId() {
        Integer count = 6;
        return ordinaryId(count);
    }


    /**
     * 通用主键
     *
     * @param count 随机数个数
     * @return String
     */
    public synchronized static String ordinaryId(Integer count) {
        return DateFormatUtils.format(new Date(), DATE_TIME_FORMATTER) + RandomStringUtils.randomNumeric(count);
    }


    /**
     * 发送响应流方法
     *
     * @param response 响应
     * @param fileName 文件名称
     */
    public static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            fileName = new String(fileName.getBytes(), "ISO8859-1");
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            GlobalUtils.convertMessage(GlobalEnum.ExceptionMessage, ex.getMessage());
        }
    }


    /**
     * 转换异常信息
     *
     * @param globalEnum 异常提示
     * @param args       参数
     */
    public static void convertMessage(GlobalEnum globalEnum, String... args) {
        String message = globalEnum.getMessage();
        convertMessage(message, args);
    }


    /**
     * 转换异常信息
     *
     * @param message 异常提示
     * @param args    参数
     */
    public static void convertMessage(String message, String... args) {
        message = convertMsg(message, args);
        throw new RuntimeException(message);
    }

    /**
     * 转换异常信息
     *
     * @param globalEnum 异常提示
     * @param args       参数
     * @return String
     */
    public static String convertMsg(GlobalEnum globalEnum, String... args) {
        return convertMsg(globalEnum.getMessage(), args);
    }

    /**
     * 转换异常信息
     *
     * @param message 异常提示
     * @param args    参数
     * @return String
     */
    public static String convertMsg(String message, String... args) {
        message = String.format(message, args);
        return message;
    }

    /**
     * 组合字符串,并去空格拼接
     *
     * @param args 参数
     * @return String
     */
    public static String appendString(String... args) {
        if (Objects.isNull(args) || args.length < 1) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        for (String arg : args) {
            if (Objects.equals(arg, SPACE)) {
                buffer.append(arg);
            } else {
                buffer.append(StringUtils.stripToEmpty(arg));
            }
        }
        return buffer.toString();
    }

    /**
     * 获取发起请求的浏览器名称
     */
    public static String getBrowserName(HttpServletRequest request) {
        String header = request.getHeader(USER_AGENT);
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        Browser browser = userAgent.getBrowser();
        return (Objects.isNull(browser) || Objects.isNull(browser.getName())) ? "未识别出浏览器" : browser.getName();
    }

    /**
     * 获取发起请求的浏览器版本号
     */
    public static String getBrowserVersion(HttpServletRequest request) {
        String header = request.getHeader(USER_AGENT);
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        //获取浏览器信息
        Browser browser = userAgent.getBrowser();
        //获取浏览器版本号
        Version version = browser.getVersion(header);
        return Objects.isNull(version) || Objects.isNull(version.getVersion()) ? "未识别出操作系统" : version.getVersion();
    }

    /**
     * 获取发起请求的操作系统名称
     */
    public static String getOsName(HttpServletRequest request) {
        String header = request.getHeader(USER_AGENT);
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        return (Objects.isNull(operatingSystem) || Objects.isNull(operatingSystem.getName())) ? "未识别出操作系统" : operatingSystem.getName();
    }

    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String str) {
        if (Objects.isNull(str)) {
            return EMPTY_STRING;
        }
        char firstChar = str.charAt(0);
        if (!str.contains(UNDER_LINE) && Character.isLowerCase(firstChar)) {
            return str;
        }
        str = str.toLowerCase();
        Matcher matcher = LINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 关闭多线程
     *
     * @param service 多线程
     */
    public static void shutdown(ThreadPoolExecutor service) {
        service.shutdown();
        while (!service.isTerminated()) {
        }
    }

    /**
     * 拷贝指定源列表 到 指定目标bean类型，并返回目标bean列表
     *
     * @param targetSupplier 目标bean对象提供者
     * @param sourceList     源bean 列表
     * @param <T>            指目标bean类型
     * @param <D>            指代源bean类型
     * @return 返回指定目标bean类型的列表
     */
    public static <T, D> List<T> copyForList(Supplier<T> targetSupplier, List<D> sourceList) {
        if (Objects.isNull(sourceList) || Objects.isNull(targetSupplier)) {
            return null;
        }
        return sourceList.stream().filter(Objects::nonNull).map(d -> {
            T t = targetSupplier.get();
            if (Objects.nonNull(t)) {
                BeanUtils.copyProperties(d, t);
            }
            return t;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 拷贝指定bean 到目标bean
     * 用法：
     * UserDto userDto=BeanHelper.copyForBean(UserDto::new, useDo);
     *
     * @param targetSupplier
     * @param d
     * @param <T>
     * @param <D>
     * @return
     */
    public static <T, D> T copyForBean(Supplier<T> targetSupplier, D d) {
        if (Objects.isNull(targetSupplier) || Objects.isNull(d)) {
            return null;
        }
        T t = targetSupplier.get();
        if (Objects.nonNull(t)) {
            BeanUtils.copyProperties(d, t);
        }
        return t;
    }

    /**
     * 将Get请求的请求参数转为JSONObject
     *
     * @param parameterMap 请求参数
     * @return JSONObject
     */
    public static JSONObject convertGetRequestParam(Map<String, String[]> parameterMap) {
        JSONObject queryObject = new JSONObject();
        if (Objects.nonNull(parameterMap) && parameterMap.size() > 0) {
            parameterMap.forEach((key, value) -> {
                String realValue = "";
                if (Objects.nonNull(value) && value.length > 0) {
                    realValue = Arrays.asList(value).stream().collect(Collectors.joining());
                }
                queryObject.put(key, realValue);
            });
        }
        //删除Key为null
        if (Objects.nonNull(queryObject) && queryObject.size() > 0) {
            queryObject.remove(NULL_CODE);
        }
        return queryObject;
    }

    /**
     * 获取本地ipv4地址
     *
     * @return String
     */
    public static String getHostIp() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                    if (Objects.nonNull(ip)
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(INTERVAL_COLON) == -1) {
                        log.debug("本机的IP:{}", ip.getHostAddress());
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取公网ip发生错误:{}", e.getMessage());
        }
        return EMPTY_STRING;
    }

    /**
     * 测试数据源连接是否有效
     *
     * @param key        数据源Id
     * @param driveClass 驱动类
     * @param url        url
     * @param username   用户名
     * @param password   密码
     * @param timeout    超时时间
     * @return boolean
     */
    public static boolean testConnection(String key, String driveClass, String url, String username, String password, Integer timeout) {
        log.info("测试:{},数据源类:{}", key, driveClass);
        try {
            timeout = Objects.isNull(timeout) ? 60 : timeout;
            Class.forName(driveClass);
            int defaultTimeOut = 1000;
            if (timeout > defaultTimeOut) {
                timeout /= defaultTimeOut;
            }
            int minTimeOut = 60;
            if (timeout > minTimeOut) {
                timeout /= 10;
            }
            DriverManager.setLoginTimeout(timeout);
            @Cleanup
            Connection connection = DriverManager.getConnection(url, username, AesUtil.aesDecrypt(password));
            return true;
        } catch (Exception e) {
            log.error("数据源:{},测试链接失败:{}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 测试应用是否有效
     *
     * @param key 应用Id
     * @param url 应用url
     * @return boolean
     */
    public static boolean testProjectConnection(String key, String url) {
        HttpURLConnection httpUrlConnection = null;
        try {
            URL projectUrl = new URL(url);
            URLConnection rulConnection = projectUrl.openConnection();
            httpUrlConnection = (HttpURLConnection) rulConnection;
            //设置超时时间
            httpUrlConnection.setConnectTimeout(30000);
            // Post 请求不能使用缓存
            httpUrlConnection.setUseCaches(false);
            // 设置是否输出
            httpUrlConnection.setDoOutput(true);
            // 设置是否读入
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.connect();
            int code = httpUrlConnection.getResponseCode();
            return Objects.equals(code, 200);
        } catch (Exception e) {
            if (Objects.nonNull(httpUrlConnection)) {
                httpUrlConnection.disconnect();
            }
            log.error("应用程序:{},连接失败！", key);
            return false;
        } finally {
            if (Objects.nonNull(httpUrlConnection)) {
                httpUrlConnection.disconnect();
            }
        }
    }

    /**
     * 检测请求信息是否合法
     *
     * @param request             请求
     * @param response            响应
     * @param sysTokenInfoService 校验TokenService
     */
    public static void checkRequestInfo(HttpServletRequest request, HttpServletResponse response, SysTokenInfoService sysTokenInfoService) {
        String token = request.getHeader(TOKEN_HEADER);
        String requestUri = request.getRequestURI();
        for (String ignoreUri : IGNORE_URI) {
            if (requestUri.endsWith(ignoreUri)) {
                return;
            }
        }
        if (Objects.isNull(token) || Objects.equals(token, NULL_CODE)) {
            redirectLogin(GlobalEnum.TokenEmpty, response);
            return;
        }
        SysTokenInfo sysTokenInfo = parseJwt(token);
        if (StringUtils.isNotBlank(sysTokenInfo.getTokenType())) {
            boolean success = sysTokenInfo.isSuccess();
            String tokenType = sysTokenInfo.getTokenType();
            if (success) {
                ResultEntity resultEntity = sysTokenInfoService.tokenValid(token, request);
                if (!resultEntity.isSuccess()) {
                    response.reset();
                    redirectLogin(resultEntity.getMessage(), response);
                }
                if (Objects.equals(UPDATE, tokenType)) {
                    String newToken = sysTokenInfoService.updateToken(tokenType);
                    response.setHeader(TOKEN_HEADER, token);
                    response.setHeader(TOKEN_NEW_HEADER, newToken);
                } else {
                    for (String ignoreUri : NO_TOKEN_URI) {
                        if (requestUri.endsWith(ignoreUri)) {
                            response.setHeader(TOKEN_HEADER, "");
                        } else {
                            response.setHeader(TOKEN_HEADER, token);
                        }
                    }
                    log.debug("用户授权认证通过!");
                }
            } else {
                response.reset();
                if (Objects.equals(ExpiredJwtException.class.getName(), tokenType)) {
                    log.info("token已过期!");
                    redirectLogin(GlobalEnum.TokenOvertime, response);
                } else if (Objects.equals(SignatureException.class.getName(), tokenType)) {
                    log.error("token sign解析失败!");
                    redirectLogin(GlobalEnum.TokenSignError, response);
                } else if (Objects.equals(MalformedJwtException.class.getName(), tokenType)) {
                    log.error("token的head解析失败!");
                    redirectLogin(GlobalEnum.TokenSignError, response);
                } else {
                    log.error("程序未捕获到的异常:{}", tokenType);
                    redirectLogin(GlobalEnum.TokenSignError, response);
                }
            }
        }
    }

    /**
     * 跳转到登录页面
     *
     * @param globalEnum 信息
     * @param response   响应
     */
    public static void redirectLogin(GlobalEnum globalEnum, HttpServletResponse response) {
        redirectLogin(globalEnum.getMessage(), response);
    }

    /**
     * 跳转到登录页面
     *
     * @param message  信息
     * @param response 响应
     */
    public static void redirectLogin(String message, HttpServletResponse response) {
        response.setStatus(403);
        GlobalUtils.convertMessage(message);
    }

    /**
     * 检验token是或否即将过期
     * 如快要过期,就提前更新token。
     * 如果已经过期的,会抛出ExpiredJwtException 的异常
     *
     * @param jwt token
     * @return Object
     */
    public static SysTokenInfo parseJwt(String jwt) {
        SysTokenInfo sysTokenInfo = SysTokenInfo.builder().build();
        try {
            Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(LEXICAL_XSD_BASE64_BINARY)).parseClaimsJws(jwt).getBody();
            String id = claims.getId();
            String subject = claims.getSubject();
            String issuer = claims.getIssuer();
            Date issuedAt = claims.getIssuedAt();
            Date expiration = claims.getExpiration();
            sysTokenInfo.setId(id);
            sysTokenInfo.setSubject(subject);
            sysTokenInfo.setIssuer(issuer);
            sysTokenInfo.setIssuedAt(issuedAt);
            sysTokenInfo.setExpiration(expiration);
            sysTokenInfo.setToken(jwt);
            //过期的时间
            Long exp = expiration.getTime();
            //现在的时间
            long nowMillis = System.currentTimeMillis();
            //剩余的时间 ，若剩余的时间小于1小时，就返回update,产生一个新的token给APP
            long seconds = exp - nowMillis;
            if (seconds <= SECONDS_ONE_HOUR) {
                log.info("token的有效期小于1小时，请更新token!");
                sysTokenInfo.setTokenType(UPDATE);
            } else {
                sysTokenInfo.setTokenType(SUCCESS);
            }
            sysTokenInfo.setSuccess(true);
            return sysTokenInfo;
        } catch (ExpiredJwtException e) {
            sysTokenInfo.setTokenType(ExpiredJwtException.class.getName());
            return sysTokenInfo;
        } catch (MalformedJwtException e) {
            sysTokenInfo.setTokenType(MalformedJwtException.class.getName());
            return sysTokenInfo;
        }
    }

    /**
     * 创建token
     * id，issuer，subject，ttlMillis都是放在payload中的，可根据自己的需要修改
     *
     * @param id        登陆产生主键
     * @param issuer    拥有者
     * @param subject   内容
     * @param ttlMillis 过期时间
     * @return String
     */
    public static String createJwt(String id, String issuer, String subject, long ttlMillis) {
        //签名的算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //当前的时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //签名算法的秘钥，解析token时的秘钥需要和此时的一样
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(LEXICAL_XSD_BASE64_BINARY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //构造
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);
        log.info("---token生成---");
        //给token设置过期时间
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            log.info("过期时间：{}", exp);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param key  密钥
     * @param md5  密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String key, String md5) throws Exception {
        //根据传入的密钥进行验证
        String md5Text = md5(text, key);
        return md5Text.equalsIgnoreCase(md5);
    }

    /**
     * MD5方法
     *
     * @param text 明文
     * @param key  密钥
     * @return 密文
     * @throws Exception
     */
    public static String md5(String text, String key) {
        //加密后的字符串
        return DigestUtils.md5Hex(text + key);
    }

    /**
     * 获取数据条数
     *
     * @param mapList 数据数据
     * @return Long
     */
    public static Long dataTotal(List<Map<String, Object>> mapList) {
        Long total = 0L;
        if (Objects.nonNull(mapList) && !mapList.isEmpty() && mapList.size() > 0) {
            total = Long.valueOf(mapList.size());
        }
        return total;
    }

    /**
     * 是否能转jsonObject
     *
     * @param content 字符串
     * @return boolean
     */
    public static boolean isJsonObject(String content) {
        try {
            JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 是否能转jsonArray
     *
     * @param content 内容
     * @return boolean
     */
    public static boolean isJsonArray(String content) {
        try {
            JSONArray.parseArray(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 运行Javascript 方法
     *
     * @param script 脚本
     * @param args   参数
     * @return List
     * @throws Exception
     */
    public static List runScript(String script, List args) throws Exception {
        String name = getFunctionName(script);
        if (StringUtils.isBlank(name)) {
            return args;
        }
        Object executeData;
        try {
            executeData = runScript(script, name, args);
        } catch (Exception e) {
            executeData = ScriptUtil.invoke(script, name, args);
        }
        return convertRunScriptResult(executeData, args);
    }

    /**
     * 获取Javascript 方法名
     *
     * @param script 脚本
     * @return name 函数名
     */
    public static String getFunctionName(String script) {
        String name = EMPTY_STRING;
        Matcher m = FUNCTION_NAME_PATTERN.matcher(script);
        while (m.find()) {
            name = m.group(1).trim();
            if (StringUtils.isNotBlank(name)) {
                break;
            }
        }
        log.info("js原方法名:{}", name);
        name = name.split("\\(", -1)[0];
        log.info("js修正后方法名:{}", name);
        return name;
    }

    /**
     * 运行Javascript 方法
     *
     * @param script 脚本
     * @param args   参数
     * @return List
     * @throws Exception
     */
    public static Object runScript(String script, String name, List args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");
        String scriptContent = script;
        //注册脚本
        engine.eval(scriptContent);
        Invocable invocable = (Invocable) engine;
        //调用注册函数
        Object function = invocable.invokeFunction(name, args);
        return function;
    }

    /**
     * 转换JS请求结果
     *
     * @param function 函数执行结果
     * @param args     参数
     * @return List
     */
    public static List convertRunScriptResult(Object function, List args) {
        List result = Lists.newArrayList();
        if (function instanceof ScriptObjectMirror) {
            ScriptObjectMirror objectMirror = (ScriptObjectMirror) function;
            if (Objects.isNull(objectMirror) || objectMirror.isEmpty()) {
                return args;
            } else {
                if (objectMirror.isArray()) {
                    result = new ArrayList<>(objectMirror.values());
                } else {
                    return Lists.newArrayList(objectMirror);
                }
            }
        } else if (function instanceof ArrayList) {
            result.addAll((ArrayList) function);
        } else if (function instanceof JSONArray) {
            result.addAll((JSONArray) function);
        } else if (function instanceof List) {
            result.addAll((List) function);
        } else {
            result.add(function);
        }
        return result;
    }

    /**
     * 比较大小
     *
     * @param one     比较数据
     * @param two     比较数据
     * @param operate 运算符
     * @return
     */
    public static boolean compareNumber(String one, String two, String operate) {
        Double numOne = Double.parseDouble(one);
        Double numTwo = Double.parseDouble(two);
        boolean bl = false;
        switch (operate) {
            case INTERVAL_MORE_THAN:
                bl = numOne > numTwo;
                break;
            case INTERVAL_MORE_EQUAL_THAN:
                bl = numOne >= numTwo;
                break;
            case INTERVAL_LESS_THAN:
                bl = numOne < numTwo;
                break;
            case INTERVAL_LESS_EQUAL_THAN:
                bl = numOne <= numTwo;
                break;
            case INTERVAL_EQUAL:
                bl = numOne.equals(numTwo);
                break;
            case INTERVAL_NOT_EQUAL:
                bl = Objects.equals(one, two);
                break;
            default:
                break;
        }
        return bl;
    }

    /**
     * 获取默认时间
     *
     * @param timeOut 超时时间
     * @return Integer
     */
    public static Integer convertTimeOut(Integer timeOut) {
        if (Objects.isNull(timeOut)) {
            return DEFAULT_TIME_OUT;
        }
        timeOut *= 1000;
        return timeOut;
    }

    /**
     * 通过key获取List中的数据值为List
     *
     * @param key  数据主键
     * @param data 数据
     * @return List
     */
    public static List getListByMoreKey(String key, List data) {
        List resultList = Lists.newArrayList();
        data.forEach(dataEntity -> {
            if (dataEntity instanceof JSONObject && ((JSONObject) dataEntity).containsKey(key)) {
                resultList.add(((JSONObject) dataEntity).get(key));
                return;
            } else if (dataEntity instanceof HashMap && ((HashMap) dataEntity).containsKey(key)) {
                resultList.add(((HashMap) dataEntity).get(key));
                return;
            }
        });
        return resultList;
    }

    /**
     * encode出错返回源内容
     *
     * @param str 字符串
     * @return String
     */
    public static String encode(String str) {
        if (StringUtils.isBlank(str)) {
            return EMPTY_STRING;
        }
        try {
            return Base64.encode(str, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("数据encode发生错误:{}", e.getMessage());
            return str;
        }
    }

    /**
     * decode出错返回源内容
     *
     * @param str 字符串
     * @return String
     */
    public static String decode(String str) {
        if (StringUtils.isBlank(str)) {
            return EMPTY_STRING;
        }
        try {
            return new String(Base64.decode(str.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("数据decode发生错误:{}", e.getMessage());
            return str;
        }
    }
}
