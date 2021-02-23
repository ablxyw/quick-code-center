package cn.ablxyw.service.impl;

import cn.ablxyw.config.SysInfoConfig;
import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.service.SysInfoService;
import cn.ablxyw.utils.GlobalUtils;
import cn.ablxyw.utils.ResultUtil;
import cn.ablxyw.vo.ResultEntity;
import cn.ablxyw.vo.SysInfoConfigVo;
import cn.ablxyw.vo.SystemRunInfoVo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static cn.ablxyw.constants.GlobalConstants.DATE_TIME_FORMAT;

/**
 * 系统ServiceImpl
 *
 * @author weiqiang
 * @date 2021-01-19 下午4:15
 */
@Slf4j
@Service("sysInfoService")
public class SysInfoServiceImpl implements SysInfoService {

    /**
     * 系统配置信息
     */
    @Autowired
    private SysInfoConfig sysInfoConfig;

    /**
     * 获取系统配置信息
     *
     * @return ResultEntity
     */
    @Override
    public ResultEntity sysInfo() {
        SysInfoConfigVo sysInfoConfigVo = GlobalUtils.copyForBean(SysInfoConfigVo::new, sysInfoConfig);
        return ResultUtil.success(GlobalEnum.QuerySuccess, Lists.newArrayList(sysInfoConfigVo));
    }

    /**
     * 服务器运行信息
     *
     * @return ResultEntity
     */
    @Override
    public ResultEntity systemRunInfoVo() {
        SystemRunInfoVo vo = new SystemRunInfoVo();
        try {
            Sigar sigar = new Sigar();

            int ratioCpu = BigDecimal.valueOf(sigar.getCpuPerc().getCombined()).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).intValue();
            vo.setRatioCpu(ratioCpu);

            Mem mem = sigar.getMem();
            double totalMemory = BigDecimal.valueOf(mem.getTotal() / 1024.00 / 1024.00 / 1024.00).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            vo.setTotalMemory(totalMemory);

            double useMemory = BigDecimal.valueOf(mem.getUsed() / 1024.00 / 1024.00 / 1024.00).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            vo.setUseMemory(useMemory);

            double freeMemory = BigDecimal.valueOf(mem.getFree() / 1024.00 / 1024.00 / 1024.00).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            vo.setFreeMemory(freeMemory);

            int ratioMemory = BigDecimal.valueOf(useMemory / totalMemory).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).intValue();
            vo.setRatioMemory(ratioMemory);

        } catch (Exception ex) {
            log.error("获取服务器运行信息发生错误:{}", ex.getMessage());
        }
        vo.setNowTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        vo.setHostIp(GlobalUtils.getHostIp());
        return ResultUtil.success(GlobalEnum.QuerySuccess, Lists.newArrayList(vo));
    }
}
