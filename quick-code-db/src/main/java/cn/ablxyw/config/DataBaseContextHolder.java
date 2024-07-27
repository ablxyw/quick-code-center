package cn.ablxyw.config;

import lombok.extern.slf4j.Slf4j;

/**
 * 手动切换数据源
 *
 * @author weiqiang
 * @date 2020-01-14 11:28:44
 */
@Slf4j
public class DataBaseContextHolder {
    /**
     * 对当前线程的操作-线程安全的
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 获取数据源
     *
     * @return String
     */
    public static String getDataSource() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 调用此方法，切换数据源
     *
     * @param dataSourceId 数据源Id
     */
    public static void setDataSource(String dataSourceId) {
        CONTEXT_HOLDER.set(dataSourceId);
        log.info("已切换到数据源:{}", dataSourceId);
    }

    /**
     * 移除数据源,切换至主数据源
     */
    public static void clearDataSource() {
        CONTEXT_HOLDER.remove();
        log.info("已切换到主数据源");
    }

}
