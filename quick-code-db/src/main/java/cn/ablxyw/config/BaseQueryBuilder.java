package cn.ablxyw.config;

import cn.ablxyw.utils.GlobalUtils;

import java.util.Map;

import static cn.ablxyw.constants.GlobalConstants.*;

/**
 * 动态SQL构造器
 *
 * @author weiqiang
 * @date 2020-10-13
 */
public class BaseQueryBuilder {

    /**
     * 基础查询
     *
     * @param jsonObject 参数
     * @return String
     */
    public String baseQueryBuilder(Map<String, Object> jsonObject) {
        return GlobalUtils.appendString(SCRIPT_START, jsonObject.getOrDefault(QUERY_SQL_KEY, EMPTY_STRING).toString(), SCRIPT_END);
    }

    /**
     * 基础插入
     *
     * @param jsonObject 参数
     * @return String
     */
    public String baseInsertBuilder(Map<String, Object> jsonObject) {
        return GlobalUtils.appendString(SCRIPT_START, jsonObject.getOrDefault(QUERY_SQL_KEY, EMPTY_STRING).toString(), SCRIPT_END);
    }

    /**
     * 基础更新
     *
     * @param jsonObject 参数
     * @return String
     */
    public String baseUpdateBuilder(Map<String, Object> jsonObject) {
        return GlobalUtils.appendString(SCRIPT_START, jsonObject.getOrDefault(QUERY_SQL_KEY, EMPTY_STRING).toString(), SCRIPT_END);
    }

    /**
     * 基础删除
     *
     * @param jsonObject 参数
     * @return String
     */
    public String baseDeleteBuilder(Map<String, Object> jsonObject) {
        return GlobalUtils.appendString(SCRIPT_START, jsonObject.getOrDefault(QUERY_SQL_KEY, EMPTY_STRING).toString(), SCRIPT_END);
    }
}
