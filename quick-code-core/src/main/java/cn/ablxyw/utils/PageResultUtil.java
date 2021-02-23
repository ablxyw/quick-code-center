package cn.ablxyw.utils;

import cn.ablxyw.enums.GlobalEnum;
import cn.ablxyw.vo.ResultEntity;
import com.github.pagehelper.PageInfo;

/**
 * 分页返回结果封装工具类
 *
 * @author weiqiang
 * @date 2020-03-09 3:43 下午
 */
public class PageResultUtil {

    /**
     * 分页返回
     *
     * @param globalEnum 信息
     * @param pageInfo   分页信息
     * @return ResultEntity
     */
    public static ResultEntity success(GlobalEnum globalEnum, PageInfo pageInfo) {
        return ResultEntity.builder().message(globalEnum.getMessage()).success(true).data(pageInfo.getList()).pageable(true).total(pageInfo.getTotal()).build();
    }

}
