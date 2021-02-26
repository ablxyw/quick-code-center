package cn.ablxyw.service;

import cn.ablxyw.vo.ResultEntity;
import org.activiti.bpmn.model.Process;

/**
 * BpmService
 *
 * @author weiqiang
 * @date 2021-02-25 下午3:05
 */
public interface BpmService {

    /**
     * 获取流程
     *
     * @param process  流程定义
     * @param pageNum  起始页
     * @param pageSize 页面条数
     * @return ResultEntity
     */
    ResultEntity process(Process process, Integer pageNum, Integer pageSize);
}
