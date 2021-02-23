package cn.ablxyw.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * 系统运行信息
 *
 * @author weiqiang
 * @date 2021-01-19
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SystemRunInfoVo implements Serializable {

    /**
     * 当前时间
     */
    private String nowTime;

    /**
     * 当前服务器Ip
     */
    private String hostIp;

    /**
     * cpu利用率
     */
    private Integer ratioCpu;

    /**
     * 当前总内存(G)
     */
    private double totalMemory;
    /**
     * 已使用内存(G)
     */
    private double useMemory;

    /**
     * 剩余内存(G)
     */
    private double freeMemory;

    /**
     * 内存使用率
     */
    private Integer ratioMemory;
}
