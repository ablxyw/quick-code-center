package cn.ablxyw.vo;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * 请求返回实体类
 *
 * @author weiQiang
 * @date 2020-01-10
 */
@ApiModel("请求返回实体类")
public class PageResultEntity extends PageInfo implements Serializable {

    private static final long serialVersionUID = -5159330866402406443L;
    /**
     * 返回信息
     */
    @ApiModelProperty(value = "返回结果提示信息")
    private String message;
    /**
     * 返回成功与否
     */
    @ApiModelProperty(value = "返回结果状态")
    private boolean success;
    /**
     * 耗时
     */
    @Builder.Default
    @ApiModelProperty(value = "请求耗时,单位ms")
    private Long totalTime = 0L;

    /**
     * 包装Page对象
     *
     * @param list
     */
    public PageResultEntity(List list, String message, boolean success) {
        super(list);
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }
}
