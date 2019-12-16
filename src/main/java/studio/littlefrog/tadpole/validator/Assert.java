package studio.littlefrog.tadpole.validator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class Assert {

    private final static Logger logger = LoggerFactory.getLogger(Assert.class);

    public Assert() {
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            exception(message);
        }
    }
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            exception(message);
        }
    }

    public static void notTrue(boolean expression, String message) {
        if (expression) {
            exception(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (Objects.nonNull(object)) {
            exception(message);
        }
    }

    public static void notNull(Object object, String message) {
        if (Objects.isNull(object)) {
            exception(message);
        }
    }

    public static void gtZero(Number number, String message) {
        if (Objects.nonNull(number) && (number.doubleValue() <= 0)) {
            exception(message);
        }
    }

    public static void gteZero(Number number, String message) {
        if (Objects.nonNull(number) && (number.doubleValue() < 0)) {
            exception(message);
        }
    }

    public static void notBlank(String text, String message) {
        if (StringUtils.isBlank(text)) {
            exception(message);
        }
    }

    public static void notEmpty(Object[] array, String message) {
        if (ObjectUtils.isEmpty(array)) {
            exception(message);
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            exception(message);
        }
    }

    public static void notEmpty(Map<?, ?> map, String message) {
        if (MapUtils.isEmpty(map)) {
            exception(message);
        }
    }

    public static void exception(String message) {
        logger.error(message);
        throw new ValidateException(message);
    }
}
