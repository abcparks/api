package cn.alex.util.validate;

import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 手动验证工具 基于hibernate-validator
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class ObjectValidatorUtil {

    private ObjectValidatorUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 验证某一个对象
     * @param obj 验证对象
     */
    public static void validate(Object obj) {
        Iterator<ConstraintViolation<Object>> iter = getConstraintViolationIterator(obj);
        if (iter.hasNext()) {
            String message = iter.next().getMessage();
            throw new ValidateException(message);
        }
    }

    /**
     * 验证某一个对象, 可以接受多个message
     * @param obj      验证对象
     * @param messages 验证提示
     * @return 是否验证通过
     */
    public static boolean validate(Object obj, List<String> messages) {
        Iterator<ConstraintViolation<Object>> iter = getConstraintViolationIterator(obj);
        if (iter.hasNext()) {
            String message = iter.next().getMessage();
            messages.add(message);
        }
        return CollectionUtils.isEmpty(messages);
    }

    private static Iterator<ConstraintViolation<Object>> getConstraintViolationIterator(Object obj) {
        if (obj == null) {
            throw new ValidateException("验证对象不能为空");
        }
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        // 验证某个对象, 其实也可以只验证其中的某一个属性的
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
        return constraintViolations.iterator();
    }
}
