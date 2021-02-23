package cn.ablxyw.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * 系统配置信息
 *
 * @author weiqiang
 * @date 2020-03-30 2:07 下午
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Configuration
public class SysInfoConfig implements Serializable {
    private static final long serialVersionUID = 5047599969920648434L;

    /**
     * 系统名称
     */
    @Value("${qFrame.sysInfo.title:Q-Database}")
    private String title;

    /**
     * 系统版本
     */
    @Value("${qFrame.sysInfo.version:1.1.4}")
    private String version;

    /**
     * 公司链接地址
     */
    @Value("${qFrame.sysInfo.companyUrl:https://www.ablxyw.com.cn}")
    private String companyUrl;

    /**
     * 公司名称
     */
    @Value("${qFrame.sysInfo.companyName:quick-code}")
    private String companyName;

    /**
     * 主题
     */
    @ApiModelProperty(value = "主题")
    @Value("${qFrame.sysInfo.theme:default}")
    private String theme;
}
