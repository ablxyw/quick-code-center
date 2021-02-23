package cn.ablxyw.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * ValidatorImpl
 *
 * @author weiqiang
 * @Description 校验参数
 * @Date 2019-05-08 09:04
 */
@Component(value = "validatorImpl")
public class ValidatorImpl implements InitializingBean {

    private Validator validator;

    /**
     * 校验参数是否不符合定义
     *
     * @param bean 参数bean
     * @return ValidationResult
     */
    public ValidationResult validate(Object bean) {
        ValidationResult validationResult = ValidationResult.builder().build();
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(bean);
        if (null != constraintViolations && constraintViolations.size() > 0) {
            validationResult.setHasError(true);
            constraintViolations.stream().forEach(constraintViolation -> {
                String message = constraintViolation.getMessage();
                String field = constraintViolation.getPropertyPath().toString();
                validationResult.getErrorMsgMap().put(field, message);
            });
        }
        return validationResult;
    }

    /**
     * 初始化Validator
     */
    @Override
    public void afterPropertiesSet() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
