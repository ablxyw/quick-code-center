package cn.ablxyw.controller;

import cn.ablxyw.service.BpmService;
import cn.ablxyw.vo.ResultEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "工作流")
@RestController
@RequestMapping("/activiti")
public class ActivitiController {

    /**
     * BpmService
     */
    @Autowired
    private BpmService bpmService;

    @Resource
    private ProcessRuntime processRuntime;
    @Resource
    private TaskRuntime taskRuntime;

    /**
     * 查询流程定义
     */
    @ApiOperation("查询流程定义")
    @GetMapping("/list")
    public ResultEntity getProcess(Process process, @RequestParam(required = false, defaultValue = "1", name = "pageNum") Integer pageNum, @RequestParam(required = false, defaultValue = "30", name = "pageSize") Integer pageSize) {
        return bpmService.process(process, pageNum, pageSize);
    }

    /**
     * 启动流程示例
     */
    @ApiOperation("启动流程示例")
    @GetMapping("/startInstance")
    public void startInstance() {
        ProcessInstance instance = processRuntime.start(ProcessPayloadBuilder.start().withProcessDefinitionKey("demo").build());
        System.out.println(instance.getId());
    }

    /**
     * 获取任务，拾取任务，并且执行
     */
    @ApiOperation("获取任务，拾取任务，并且执行")
    @GetMapping("/getTask")
    public void getTask() {
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));
        if (tasks.getTotalItems() > 0) {
            for (Task task : tasks.getContent()) {
                System.out.println("任务名称：" + task.getName());
                //拾取任务
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
                //执行任务
                taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId()).build());
            }
        }
    }
}
