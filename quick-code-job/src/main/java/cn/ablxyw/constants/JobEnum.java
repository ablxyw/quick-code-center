package cn.ablxyw.constants;

/**
 * 任务枚举
 *
 * @author weiqiang
 * @date 2021-01-30 下午5:27
 */
public enum JobEnum {

    /**
     * 任务类不存在
     */
    JobClassNotFound("任务类不存在,请确认!"),
    /**
     * 时间范围
     */
    EndTimeBeforeStartTime("结束时间不能小于开始时间,请确认!"),
    /**
     * 表达式
     */
    CornParamError("定时任务格式错误"),
    ;
    /**
     * 信息
     */
    private final String message;

    JobEnum(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }
}
