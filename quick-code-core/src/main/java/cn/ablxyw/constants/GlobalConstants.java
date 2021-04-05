package cn.ablxyw.constants;

import com.google.common.collect.Lists;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 常量
 *
 * @author weiQiang
 * @date 2020-01-10
 */
public class GlobalConstants {

    /**
     * linePattern
     */
    public static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");
    /**
     * 格式化时间格式
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 格式化时间格式
     */
    public static final String DATE_TIME_FORMATTER = "yyyyMMddHHmmss";
    /**
     * 格式化时间格式
     */
    public static final String DATE_FORMATTER = "yyyyMMdd";
    /**
     * 前缀
     */
    public static final String DEFAULT_PREFIX = "(";
    /**
     * 默认排名
     */
    public static final Integer DEFAULT_RANKING = 1;
    /**
     * 后缀
     */
    public static final String DEFAULT_SUFFIX = ")";
    /**
     * 导入文件sheet切分长度
     */
    public static final Integer EXCEL_SHEET_LENGTH = 3;
    /**
     * EXCEL类型
     */
    public static final String EXCEL_TYPE_XLS = "xls";
    /**
     * EXCEL类型
     */
    public static final String EXCEL_TYPE_XLSX = "xlsx";
    /**
     * 上传文件配置key
     */
    public static final String FORM_DATA_KEY = "formData";
    /**
     * 最大尝试登陆失败次数
     */
    public static final int FREQUENTLY_MAX_LOGIN_ERROR_COUNT = 15;
    /**
     * 逗号
     */
    public static final String INTERVAL_COMMA = ",";
    /**
     * 间隔号
     */
    public static final String INTERVAL_NUMBER = "`";
    /**
     * 点
     */
    public static final String INTERVAL_POINT = "\\.";
    /**
     * 点
     */
    public static final String POINT = ".";
    /**
     * linux
     */
    public static final String LINUX_NAME = "linux";
    /**
     * 请求类型options
     */
    public static final String OPERATE_METHOD = "OPTIONS";
    /**
     * 升序
     */
    public static final String ORDER_ASC = "ASC";
    /**
     * 降序
     */
    public static final String ORDER_DESC = "DESC";
    /**
     * 问号
     */
    public static final String QUERY_MARK = "?";
    /**
     * 与号
     */
    public static final String AND_MARK = "&";
    /**
     * 空格
     */
    public static final String SPACE = " ";
    /**
     * 成功
     */
    public static final String SUCCESS = "success";
    /**
     * 下划线
     */
    public static final String UNDER_LINE = "_";
    /**
     * 常量:未知
     */
    public static final String UNKNOWN = "unknown";
    /**
     * 更新
     */
    public static final String UPDATE = "UPDATE";

    /**
     * 增加
     */
    public static final String INSERT = "INSERT";

    /**
     * 查询
     */
    public static final String SELECT = "SELECT";

    /**
     * 删除
     */
    public static final String DELETE = "DELETE";
    /**
     * 批量更新
     */
    public static final String BATCH_UPDATE = "batchUpdate";

    /**
     * 批量增加
     */
    public static final String BATCH_INSERT = "batchInsert";

    /**
     * 批量删除
     */
    public static final String BATCH_DELETE = "batchDelete";

    /**
     * 批量更新
     */
    public static final String BATCH_UPDATE_QUOTA_NAME = "batchUpdateQuotaName";

    /**
     * 批量更新
     */
    public static final String BATCH_UPDATE_CACHE = "batchUpdateCache";

    /**
     * 更新数据接口信息
     */
    public static final String UPDATE_API_INFO = "updateApiInfo";

    /**
     * 执行变更SQL方法
     */
    public static final List<String> UPDATE_CACHE_LIST = Lists.newArrayList(BATCH_UPDATE, BATCH_INSERT, BATCH_DELETE, BATCH_UPDATE_QUOTA_NAME, BATCH_UPDATE_CACHE, UPDATE_API_INFO);


    /**
     * xml请求
     */
    public static final String XML_HTTP_REQUEST = "XMLHttpRequest";
    /**
     * 请求类型
     */
    public static final String X_REQUESTED_WIDTH = "X-Requested-With";

    /**
     * 多线程名称
     */
    public static final String THREAD_NAME = "common_thread_";

    /**
     * User-Agent
     */
    public static final String USER_AGENT = "User-Agent";

    /**
     * message
     */
    public static final String CODE_MESSAGE = "message";

    /**
     * body
     */
    public static final String CODE_BODY = "body";

    /**
     * Cookie
     */
    public static final String COOKIE_VALUE = "Set-Cookie";

    /**
     * 核心线程数
     */
    public static final Integer CORE_POOL_SIZE = 5;

    /**
     * 最大线程数
     */
    public static final Integer MAX_POOL_SIZE = 100;
    /**
     * 任务队列为 `ArrayBlockingQueue`，并且容量为 100
     */
    public static final Integer QUEUE_CAPACITY = 100;
    /**
     * 等待时间
     */
    public static final Long KEEP_ALIVE_TIME = 1L;
    /**
     * 默认精度
     */
    public static final int DEFAULT_PRECISION = 2;

    /**
     * success code
     */
    public static final String CODE_SUCCESS = "success";

    /**
     * null
     */
    public static final String NULL_CODE = "null";

    /**
     * 斜杠
     */
    public static final String SLASH_CODE = "/";

    /**
     * 单引号
     */
    public static final String INTERVAL_APOSTROPHE = "'";

    /**
     * 查询SQL前缀
     */
    public static final String SQL_PREFIX = "#{";

    /**
     * 查询SQL后缀
     */
    public static final String SQL_SUFFIX = "}";

    /**
     * HTML后缀
     */
    public static final String HTML_SUFFIX = ".html";

    /**
     * 字符型
     */
    public static final String TYPE_STRING_CODE = "string";

    /**
     * 数字型
     */
    public static final String TYPE_NUMBER_CODE = "number";

    /**
     * 日期型
     */
    public static final String TYPE_DATE_CODE = "date";

    /**
     * 动态SQL key
     */
    public static final String QUERY_SQL_KEY = "`DYNAMIC_QUERY_SQL`";

    /**
     * script 头
     */
    public static final String SCRIPT_START = "<script>";

    /**
     * script 尾
     */
    public static final String SCRIPT_END = "</script>";

    /**
     * 大于
     */
    public static final String INTERVAL_MORE_THAN = ">";

    /**
     * 大于等于
     */
    public static final String INTERVAL_MORE_EQUAL_THAN = ">=";

    /**
     * 小于
     */
    public static final String INTERVAL_LESS_THAN = "<";

    /**
     * 小于等于
     */
    public static final String INTERVAL_LESS_EQUAL_THAN = "<=";

    /**
     * 等于
     */
    public static final String INTERVAL_EQUAL = "=";
    /**
     * 不等于
     */
    public static final String INTERVAL_NOT_EQUAL = "≠";

    /**
     * 冒号
     */
    public static final String INTERVAL_COLON = ":";

    /**
     * db
     */
    public static final String INTERVAL_DB = "db";

    /**
     * $
     */
    public static final String INTERVAL_DOLLAR = "$";

    /**
     * SQL关键词
     * and、or、like、delete、truncate、select、update、drop
     */
    public static final Pattern SQL_PATTERN = Pattern.compile("('{1}+\\s{0,}or\\s{0,})|(\\d+\\s{0,}or\\s{0,})|" +
            "(\\s+((drop)|(select)|(truncate)|(delete)|(update)|(insert)|(alter)(create))+\\s+)|" +
            "(\\s+and\\s+)|(;+)|(\\s+like+\\s)");
    /**
     * 数据库操作
     */
    public static final Pattern SQL_DATA_PATTERN = Pattern.compile("((drop)|(truncate)|(delete)|(update)|(insert)|(alter)(create))+\\s");

    public static final String CACHE_NAME = "baseCache";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String LOG_FILE_NAME = "qFrameAspect";

    public static final String CROSS_BAR = "-";

    public static final String LEFT_BRACES = "{";

    public static final String RIGHT_BRACES = SQL_SUFFIX;

    /**
     * 空字符串
     */
    public static final String EMPTY_STRING = "";

    /**
     * 数字正在表达式
     */
    public static final Pattern NUM_PATTERN = Pattern.compile("^[0-9.]*$");

    /**
     * 字符换正在表达式（中文、英文、数字包括下划线、中横线）
     */
    public static final Pattern STR_PATTERN = Pattern.compile("[\\s\\S]*+$");

    /**
     * 日期正在表达式（yyyy-MM-dd）|（yyyyMMdd）
     */
    public static final Pattern DATE_PATTERN = Pattern.compile("(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-9]))))" +
            "|(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-9]))))");

    /**
     * api前缀
     */
    public static final String API_PREFIX = "api/";

    /**
     * 高性能api前缀
     */
    public static final String API_PREFIX_Q = "qApi/";

    /**
     * 天
     */
    public static final String TABLE_DAY = "_d";

    /**
     * 周
     */
    public static final String TABLE_WEEK = "_w";

    /**
     * 月
     */
    public static final String TABLE_MONTH = "_m";

    /**
     * 年
     */
    public static final String TABLE_YEAR = "_y";

    /**
     * 表后缀
     */
    public static final List<String> TABLE_NAME_END_LIST = Lists.newArrayList(TABLE_DAY, TABLE_MONTH, TABLE_WEEK, TABLE_YEAR);

    /**
     * httpCode
     */
    public static final String HTTP_CODE = "http://";
    /**
     * httpCode
     */
    public static final String HTTPS_CODE = "https://";

    /**
     * 系统数据库
     */
    public static final String SYS_NAME = "q_frame_api";

    /**
     * 系统日志数据库
     */
    public static final String SYS_LOG_NAME = "q_frame_log";

    /**
     * 一天(毫秒)
     */
    public static final Integer SECONDS_ONE_DAY = 1000 * 60 * 60 * 24;
    /**
     * 一小时(毫秒)
     */
    public static final Integer SECONDS_ONE_HOUR = 1000 * 60 * 60;
    /**
     * 一分钟(毫秒)
     */
    public static final Integer SECONDS_ONE_MINUTE = 60000;
    /**
     * 两天(毫秒)
     */
    public static final Integer SECONDS_TWO_DAY = 1000 * 60 * 60 * 48;
    /**
     * 两分钟(毫秒)
     */
    public static final Integer SECONDS_TWO_MINUTE = 2 * 60 * 1000;

    /**
     * token再请求头中的Key
     */
    public static final String TOKEN_HEADER = "token";
    /**
     * token拥有者
     */
    public static final String TOKEN_ISSUER = SYS_NAME;

    /**
     * 忽略校验token URI
     */
    public static final String[] IGNORE_URI = {"user/login", "sysQueryConfig/apiPrefix"};
    /**
     * 请求头中不设置tokenUri
     */
    public static final String[] NO_TOKEN_URI = {"user/logout"};

    /**
     * token再请求头中的Key
     */
    public static final String TOKEN_NEW_HEADER = "newToken";
    /**
     * token超时时间
     */
    public static final Integer TOKEN_TIME_OUT = SECONDS_TWO_DAY + SECONDS_ONE_MINUTE;
    /**
     * token密钥
     */
    public static final String LEXICAL_XSD_BASE64_BINARY = "quickFrame@123";

    /**
     * 当前登陆客户端用户信息
     */
    public static final String CLIENT_INFO = "clientInfo";
    /**
     * 客户端Ip
     */
    public static final String CLIENT_IP = "clientIp";

    /**
     * 增加方法
     */
    public static final String OPERATE_TYPE_INSERT = "insert";

    /**
     * 更新方法
     */
    public static final String OPERATE_TYPE_UPDATE = "update";

    /**
     * false
     */
    public static final Integer BOOLEAN_FALSE = 0;
    /**
     * true
     */
    public static final Integer BOOLEAN_TRUE = 1;

    /**
     * 5次登陆失败
     */
    public static final int FREQUENTLY_LOGIN_COUNT = 5;


    /**
     * 开始分页参数
     */
    public static final String STR_ENABLE_PAGE = "enablePage";

    /**
     * 初始页
     */
    public static final String STR_PAGE_NUM = "pageNum";

    /**
     * 每页显示数据条数
     */
    public static final String STR_PAGE_SIZE = "pageSize";

    /**
     * 分页总数
     */
    public static final String STR_TOTAL = "total";

    /**
     * insert
     */
    public static final String SQL_INSERT = "insert ";

    /**
     * into
     */
    public static final String SQL_INTO = " into ";

    /**
     * update
     */
    public static final String SQL_UPDATE = "update ";

    /**
     * batch update
     */
    public static final String SQL_BATCH_UPDATE = "<foreach ";

    /**
     * delete
     */
    public static final String SQL_DELETE = "delete ";

    /**
     * from
     */
    public static final String SQL_FROM = " from ";

    /**
     * 请求值获取方式:静态值
     */
    public static final String OBTAINING_STATIC = "static";

    /**
     * 请求值获取方式:接口获取
     */
    public static final String OBTAINING_API = "api";

    /**
     * and
     */
    public static final String QUERY_AND = "&";

    /**
     * append
     */
    public static final String API_DATA_MODEL_APPEND = "append";

    /**
     * override
     */
    public static final String API_DATA_MODEL_OVERRIDE = "override";

    /**
     * truncate
     */
    public static final String API_DATA_MODEL_TRUNCATE = "truncate";

    /**
     * 数据请求模式 auto
     */
    public static final String API_REQUEST_MODEL_AUTO = "auto";

    /**
     * 数据请求模式 restTemplate
     */
    public static final String API_REQUEST_MODEL_REST_TEMPLATE = "restTemplate";

    /**
     * 数据请求模式 httpClient
     */
    public static final String API_REQUEST_MODEL_HTTP_CLIENT = "httpClient";

    /**
     * 数据请求模式 curl
     */
    public static final String API_REQUEST_MODEL_CURL = "curl";
    /**
     * 管理员
     */
    public static final Integer USER_TYPE_ADMIN = 0;
    /**
     * 普通用户
     */
    public static final Integer USER_TYPE_GENERAL = 1;
    /**
     * 其他
     */
    public static final Integer USER_TYPE_OTHER = 2;

    /**
     * 默认时间
     */
    public static final Integer DEFAULT_TIME_OUT = 30 * 1000;

    /**
     * 下一行
     */
    public static final String NEXT_LINE = "\r\n";
    /**
     * javascript
     */
    public static final String JAVA_SCRIPT = "javascript";

    /**
     * 默认原始ID
     */
    public static final String DEFAULT_PARENT_ID = "-1";

    /**
     * 覆盖java默认的证书验证
     */
    public static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
    }};

    /**
     * 设置不验证主机
     */
    public static final HostnameVerifier DO_NOT_VERIFY = (hostname, session) -> true;

    /**
     * debug flag
     */
    public static final String Q_DEBUG_FLAG_KEY = "q_debug";

    /**
     * 接口缓存key
     */
    public static final String API_CACHE_PREFIX_KEY = "q_api_key_";

    /**
     * windows
     */
    public static final String WINDOWS_CODE = "windows";

}
