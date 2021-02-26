package cn.ablxyw.service.impl;

import cn.ablxyw.service.BpmService;
import cn.ablxyw.vo.ResultEntity;

import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author weiqiang
 * @Description
 * @date 2021-02-25 下午3:05
 */
@Service("bpmService")
public class BpmServiceImpl implements BpmService {
    /**
     * RuntimeService
     */
    @Autowired
    private RuntimeService runtimeService;

    /**
     * TaskService
     */
    @Autowired
    private TaskService taskService;

    /**
     * RepositoryService
     */
    @Autowired
    private RepositoryService repositoryService;

    /**
     * ProcessEngine
     */
    @Autowired
    private ProcessEngine processEngine;

    /**
     * HistoryService
     */
    @Autowired
    private HistoryService historyService;

    @Resource
    private ProcessRuntime processRuntime;

    /**
     * 获取流程
     *
     * @param process  流程定义
     * @param pageNum  起始页
     * @param pageSize 页面条数
     * @return ResultEntity
     */
    @Override
    public ResultEntity process(Process process, Integer pageNum, Integer pageSize) {
        //查询所有流程定义信息
        Page<ProcessDefinition> processDefinitionPage = processRuntime.processDefinitions(Pageable.of(pageNum - 1, pageSize));
        System.out.println("当前流程定义的数量：" + processDefinitionPage.getTotalItems());
        //获取流程信息
        for (ProcessDefinition processDefinition : processDefinitionPage.getContent()) {
            System.out.println("流程定义信息" + processDefinition);
        }
        return null;
    }
}
