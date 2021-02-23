package cn.ablxyw.utils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.hyperic.sigar.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sigar工具类
 *
 * @author weiqiang
 * @date 2021-01-19 下午3:22
 */
@Slf4j
public class SystemRunInfoUtil {
    /**
     * 获取系统信息
     *
     * @return List
     */
    public static List sysInfo() throws Exception {
        List sysInfoList = Lists.newLinkedList();
        cpu(sysInfoList);
        memory(sysInfoList);
        os(sysInfoList);
        who(sysInfoList);
        file(sysInfoList);
        net(sysInfoList);
        ethernet(sysInfoList);
        return sysInfoList;
    }

    /**
     * 获取内存信息
     *
     * @param sysInfoList 系统信息
     * @throws SigarException
     */
    public static void memory(List sysInfoList) throws SigarException {
        Sigar sigar = new Sigar();
        Mem mem = sigar.getMem();
        // 内存总量
        log.info("内存总量:{}", mem.getTotal() / 1024L + "K av");
        // 当前内存使用量
        log.info("当前内存使用量:{}", mem.getUsed() / 1024L + "K used");
        // 当前内存剩余量
        log.info("当前内存剩余量:{}", mem.getFree() / 1024L + "K free");
        Swap swap = sigar.getSwap();
        // 交换区总量
        log.info("交换区总量:{}", swap.getTotal() / 1024L + "K av");
        // 当前交换区使用量
        log.info("当前交换区使用量:{}", swap.getUsed() / 1024L + "K used");
        // 当前交换区剩余量
        log.info("当前交换区剩余量:{}", swap.getFree() / 1024L + "K free");
        Map<String, Object> memoryMap = new HashMap<>(6);
        // 当前内存使用量
        memoryMap.put("当前内存使用量", mem.getUsed() / 1024L + "K used");
        // 当前内存剩余量
        memoryMap.put("当前内存剩余量", mem.getFree() / 1024L + "K free");
        // 交换区总量
        memoryMap.put("交换区总量", swap.getTotal() / 1024L + "K av");
        // 当前交换区使用量
        memoryMap.put("当前交换区使用量", swap.getUsed() / 1024L + "K used");
        // 当前交换区剩余量
        memoryMap.put("当前交换区剩余量", swap.getFree() / 1024L + "K free");

        sysInfoList.add(new HashMap<String, Object>(1) {{
            put("memory", memoryMap);
        }});
    }

    /**
     * cpu信息
     *
     * @param sysInfoList 系统信息
     * @throws SigarException
     */
    public static void cpu(List<Map<String, Object>> sysInfoList) throws SigarException {
        Sigar sigar = new Sigar();
        CpuInfo[] infos = sigar.getCpuInfoList();
        CpuPerc[] cpuList;
        cpuList = sigar.getCpuPercList();
        // 不管是单块CPU还是多CPU都适用
        Map<String, Object> cpuMap = new HashMap<>(infos.length);
        for (int i = 0; i < infos.length; i++) {
            CpuInfo info = infos[i];
            log.info("第" + (i + 1) + "块CPU信息");
            // CPU的总量MHz
            log.info("CPU的总量MHz:{}", info.getMhz());
            // 获得CPU的卖主，如：Intel
            log.info("CPU生产商:{}", info.getVendor());
            // 获得CPU的类别，如：Celeron
            log.info("CPU类别:{}", info.getModel());
            // 缓冲存储器数量
            log.info("CPU缓存数量:{}", info.getCacheSize());
            printCpuPerc(cpuList[i]);

            cpuMap.put("第" + (i + 1), "块CPU信息");
            // CPU的总量MHz
            cpuMap.put("CPU的总量MHz:{}", info.getMhz());
            // 获得CPU的卖主，如：Intel
            cpuMap.put("CPU生产商:{}", info.getVendor());
            // 获得CPU的类别，如：Celeron
            log.info("CPU类别:{}", info.getModel());
            // 缓冲存储器数量
            cpuMap.put("CPU缓存数量:{}", info.getCacheSize());
        }
        sysInfoList.add(new HashMap<String, Object>(1) {{
            put("cpu", cpuMap);
        }});
    }

    /**
     * 打印Cpu使用率
     *
     * @param cpu cpu使用率
     */
    private static void printCpuPerc(CpuPerc cpu) {
        // 用户使用率
        log.info("CPU用户使用率:{}", CpuPerc.format(cpu.getUser()));
        // 系统使用率
        log.info("CPU系统使用率:{}", CpuPerc.format(cpu.getSys()));
        // 当前等待率
        log.info("CPU当前等待率:{}", CpuPerc.format(cpu.getWait()));
        //
        log.info("CPU当前错误率:{}", CpuPerc.format(cpu.getNice()));
        // 当前空闲率
        log.info("CPU当前空闲率:{}", CpuPerc.format(cpu.getIdle()));
        // 总的使用率
        log.info("CPU总的使用率:{}", CpuPerc.format(cpu.getCombined()));
    }

    /**
     * 操作系统信息
     *
     * @param sysInfoList 系统信息
     */
    public static void os(List<Map<String, Object>> sysInfoList) {
        OperatingSystem os = OperatingSystem.getInstance();
        // 操作系统内核类型如： 386、486、586等x86
        log.info("操作系统:{}", os.getArch());
        log.info("操作系统CpuEndian():{}", os.getCpuEndian());
        log.info("操作系统DataModel():{}", os.getDataModel());
        // 系统描述
        log.info("操作系统的描述:{}", os.getDescription());
        // 操作系统类型
        log.info("os.getName(): " + os.getName());
        log.info("os.getPatchLevel(): " + os.getPatchLevel());
        // 操作系统的卖主
        log.info("操作系统的卖主:{}", os.getVendor());
        // 卖主名称
        log.info("操作系统的卖主名:{}", os.getVendorCodeName());
        // 操作系统名称
        log.info("操作系统名称:{}", os.getVendorName());
        // 操作系统卖主类型
        log.info("操作系统卖主类型:{}", os.getVendorVersion());
        // 操作系统的版本号
        log.info("操作系统的版本号:{}", os.getVersion());
    }

    /**
     * 用户信息
     *
     * @param sysInfoList 系统信息
     * @throws SigarException
     */
    public static void who(List<Map<String, Object>> sysInfoList) throws SigarException {
        Sigar sigar = new Sigar();
        Who[] who = sigar.getWhoList();
        if (who != null && who.length > 0) {
            for (int i = 0; i < who.length; i++) {
                log.info("当前系统进程表中的用户名{}", String.valueOf(i));
                Who whoInfo = who[i];
                log.info("用户控制台:{}", whoInfo.getDevice());
                log.info("用户host:{}", whoInfo.getHost());
                log.info("计算机时间:{} ", whoInfo.getTime());
                log.info("当前系统进程表中的用户名:{}", whoInfo.getUser());
            }
        }
    }

    /**
     * 文件系统信息
     *
     * @param sysInfoList 系统信息
     * @throws Exception
     */
    public static void file(List<Map<String, Object>> sysInfoList) throws Exception {
        Sigar sigar = new Sigar();
        FileSystem[] fslist = sigar.getFileSystemList();
        for (int i = 0; i < fslist.length; i++) {
            log.info("分区的盘符名称" + i);
            FileSystem fs = fslist[i];
            // 分区的盘符名称
            log.info("盘符名称:{}", fs.getDevName());
            // 分区的盘符名称
            log.info("盘符路径:{}", fs.getDirName());
            log.info("盘符标志:{}", fs.getFlags());
            // 文件系统类型，比如 FAT32、NTFS
            log.info("盘符类型:{}", fs.getSysTypeName());
            // 文件系统类型名，比如本地硬盘、光驱、网络文件系统等
            log.info("盘符类型名:{}", fs.getTypeName());
            // 文件系统类型
            log.info("盘符文件系统类型:{}", fs.getType());
            FileSystemUsage usage = null;
            usage = sigar.getFileSystemUsage(fs.getDirName());
            switch (fs.getType()) {
                // TYPE_UNKNOWN ：未知
                case 0:
                    break;
                // TYPE_NONE
                case 1:
                    break;
                // TYPE_LOCAL_DISK : 本地硬盘
                case 2:
                    // 文件系统总大小
                    log.info(fs.getDevName() + "总大小:{}", usage.getTotal() + "KB");
                    // 文件系统剩余大小
                    log.info(fs.getDevName() + "剩余大小:{}", usage.getFree() + "KB");
                    // 文件系统可用大小
                    log.info(fs.getDevName() + "可用大小:{}", usage.getAvail() + "KB");
                    // 文件系统已经使用量
                    log.info(fs.getDevName() + "已经使用量:{}", usage.getUsed() + "KB");
                    double usePercent = usage.getUsePercent() * 100D;
                    // 文件系统资源的利用率
                    log.info(fs.getDevName() + "资源的利用率:{}", usePercent + "%");
                    break;
                // TYPE_NETWORK ：网络
                case 3:
                    break;
                // TYPE_RAM_DISK ：闪存
                case 4:
                    break;
                // TYPE_CDROM ：光驱
                case 5:
                    break;
                // TYPE_SWAP ：页面交换
                case 6:
                    break;
                default:
                    break;
            }
            log.info(fs.getDevName() + "读出:{}", usage.getDiskReads());
            log.info(fs.getDevName() + "写入:{}", usage.getDiskWrites());
        }
        return;
    }

    /**
     * 网络信息
     *
     * @param sysInfoList 系统信息
     * @throws Exception
     */
    public static void net(List<Map<String, Object>> sysInfoList) throws Exception {
        Sigar sigar = new Sigar();
        String[] ifNames = sigar.getNetInterfaceList();
        for (int i = 0; i < ifNames.length; i++) {
            String name = ifNames[i];
            NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
            // 网络设备名
            log.info("网络设备名:{}", name);
            // IP地址
            log.info("IP地址:{}", ifconfig.getAddress());
            // 子网掩码
            log.info("子网掩码:{}", ifconfig.getNetmask());
            if ((ifconfig.getFlags() & 1L) <= 0L) {
                log.info("!IFF_UP...skipping getNetInterfaceStat");
                continue;
            }
            NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
            // 接收的总包裹数
            log.info(name + "接收的总包裹数:{}", ifstat.getRxPackets());
            // 发送的总包裹数
            log.info(name + "发送的总包裹数:{}", ifstat.getTxPackets());
            // 接收到的总字节数
            log.info(name + "接收到的总字节数:{}", ifstat.getRxBytes());
            // 发送的总字节数
            log.info(name + "发送的总字节数:{}", ifstat.getTxBytes());
            // 接收到的错误包数
            log.info(name + "接收到的错误包数:{}", ifstat.getRxErrors());
            // 发送数据包时的错误数
            log.info(name + "发送数据包时的错误数:{}", ifstat.getTxErrors());
            // 接收时丢弃的包数
            log.info(name + "接收时丢弃的包数:{}", ifstat.getRxDropped());
            // 发送时丢弃的包数
            log.info(name + "发送时丢弃的包数:{}", ifstat.getTxDropped());
        }
    }

    /**
     * 网络信息
     *
     * @param sysInfoList 系统信息
     * @throws SigarException
     */
    public static void ethernet(List<Map<String, Object>> sysInfoList) throws SigarException {
        Sigar sigar;
        sigar = new Sigar();
        String[] ipInfos = sigar.getNetInterfaceList();
        for (int i = 0; i < ipInfos.length; i++) {
            NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(ipInfos[i]);
            if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress())
                    || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0
                    || NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
                continue;
            }
            // IP地址
            log.info(cfg.getName() + "IP地址:{}", cfg.getAddress());
            // 网关广播地址
            log.info(cfg.getName() + "网关广播地址:{}", cfg.getBroadcast());
            // 网卡MAC地址
            log.info(cfg.getName() + "网卡MAC地址:{}", cfg.getHwaddr());
            // 子网掩码
            log.info(cfg.getName() + "子网掩码:{}", cfg.getNetmask());
            // 网卡描述信息
            log.info(cfg.getName() + "网卡描述信息:{}", cfg.getDescription());
            // 网卡类型
            log.info(cfg.getName() + "网卡类型:{}", cfg.getType());
        }
    }
}
