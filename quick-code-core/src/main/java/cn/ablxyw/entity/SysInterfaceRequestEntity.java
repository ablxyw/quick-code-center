package cn.ablxyw.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

import static cn.ablxyw.constants.GlobalConstants.DATE_TIME_FORMAT;


/**
 * 接口请求日志
 *
 * @author weiqiang
 * @email weiq0525@gmail.com
 * @date 2020-02-16 18:11:59
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"requestId"})
@ApiModel("接口请求日志")
public class SysInterfaceRequestEntity implements Serializable {

    private static final long serialVersionUID = 233152653402628723L;
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private String requestId;

    /**
     * 数据源Id
     */
    @Length(max = 255, message = "数据源Id不能多于255个字符")
    @ApiModelProperty(value = "数据源Id")
    private String datasourceId;

    /**
     * 配置Id
     */
    @Length(max = 255, message = "配置Id不能多于255个字符")
    @ApiModelProperty(value = "配置Id")
    private String configId;

    /**
     * 请求uri
     */
    @NotBlank(message = "请求uri不能为空")
    @Length(max = 255, message = "请求uri不能多于255个字符")
    @ApiModelProperty(value = "请求uri")
    private String requestUri;

    /**
     * 请求类型
     */
    @ApiModelProperty(value = "请求类型")
    @Pattern(regexp = "(GET)|(POST)", message = "请求类型只能是GET或POST")
    private String requestType;

    /**
     * 请求参数
     */
    @Length(max = 2255, message = "请求参数不能多于2255个字符")
    @ApiModelProperty(value = "请求参数")
    private String requestParam;

    /**
     * 查询SQL
     */
    @Length(max = 3000, message = "查询SQL不能多于3000个字符")
    @ApiModelProperty(value = "查询SQL")
    private String querySql;

    /**
     * 客户端ip
     */
    @Length(max = 255, message = "客户端ip不能多于255个字符")
    @ApiModelProperty(value = "客户端ip")
    private String clientIp;


    /**
     * 服务器Ip
     */
    @Length(max = 255, message = "服务器ip不能多于255个字符")
    @ApiModelProperty(value = "服务器ip")
    private String serverIp;

    /**
     * 是否成功
     */
    @ApiModelProperty(value = "是否成功")
    private Boolean success;
    /**
     * 返回信息
     */
    @Length(max = 2255, message = "返回信息不能多于2255个字符")
    @ApiModelProperty(value = "返回信息")
    private String message;
    /**
     * 数据条数
     */
    @ApiModelProperty(value = "数据条数")
    private Integer dataSize;

    /**
     * 浏览器
     */
    @Length(max = 255, message = "浏览器信息不能多于2255个字符")
    @ApiModelProperty(value = "浏览器")
    private String browserName;

    /**
     * 浏览器版本
     */
    @Length(max = 255, message = "浏览器版本信息不能多于2255个字符")
    @ApiModelProperty(value = "浏览器版本")
    private String browserVersion;

    /**
     * 操作系统
     */
    @Length(max = 255, message = "操作系统信息不能多于2255个字符")
    @ApiModelProperty(value = "操作系统")
    private String osName;

    /**
     * 请求时间
     */
    @ApiModelProperty(value = "请求时间")
    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = "GMT+8")
    private Date beginTime;

    /**
     * 返回时间
     */
    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = "GMT+8")
    @ApiModelProperty(value = "返回时间")
    private Date endTime;

    /**
     * 请求耗时
     */
    @ApiModelProperty(value = "请求耗时")
    private Long requestTime;

    /**
     * 忽略日志写入
     */
    @JsonIgnore
    @ApiModelProperty(value = "忽略日志写入", hidden = true)
    private Boolean ignoreLog;

    /**
     * 忽略配置强制写入日志,这种情况值的是针对专门的接口,例如Q框架,则开启该配置,除非Q框架自己过滤掉某些uri
     */
    @JsonIgnore
    @Builder.Default
    @ApiModelProperty(value = "忽略配置强制写入日志", hidden = true)
    private Boolean forceConfigInsertLog = false;

    /**
     * 排序信息:字段 顺序
     */
    @JsonIgnore
    @ApiModelProperty(value = "排序信息:字段 顺序", hidden = true)
    private String sort;
}
