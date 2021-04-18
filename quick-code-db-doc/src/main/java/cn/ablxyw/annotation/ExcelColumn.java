package cn.ablxyw.annotation;

import java.lang.annotation.*;

/**
 * 自定义实体类所需要的bean(Excel属性标题、位置等)
 *
 * @author weiqiang
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelColumn {
    /**
     * Excel标题
     *
     * @return String
     */
    String value() default "";

    /**
     * Excel从左往右排列位置
     *
     * @return String
     */
    int col() default 0;

    /**
     * 单元格宽度
     *
     * @return int
     */
    int width() default 100;
}
