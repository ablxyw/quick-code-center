package cn.ablxyw.validator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static cn.ablxyw.constants.GlobalConstants.INTERVAL_COMMA;

/**
 * ValidationResult
 *
 * @author weiqiang
 * @Description 参数错误信息结果收集
 * @Date 2019-05-08 08:59
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ValidationResult implements Serializable {
    private static final long serialVersionUID = -8990407718746596190L;

    /**
     * 返回结果是否有错误
     */
    @Builder.Default
    private boolean hasError = false;

    /**
     * 错误信息
     */
    @Builder.Default
    private Map<String, String> errorMsgMap = new HashMap<>();

    public String getMsg() {
        return StringUtils.join(errorMsgMap.values().toArray(), INTERVAL_COMMA);
    }
}
