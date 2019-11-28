package studio.littlefrog.tadpole.excel.exporter;

import studio.littlefrog.tadpole.validator.Assert;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ColumnDefinition<T> {

    String key;
    String title;
    BiFunction<T, Object, String> formatter;
    Consumer<Exception> formatErrorHandler;

    public ColumnDefinition(String key, String title) {
        Assert.notBlank(key, "key不能为空");
        Assert.notBlank(title, "title不能为空");
        this.key = key;
        this.title = title;
    }

    public ColumnDefinition(String key, String title, BiFunction<T, Object, String> formatter) {
        this(key, title);
        this.formatter = formatter;
    }

    public ColumnDefinition(String key, String title, BiFunction<T, Object, String> formatter, Consumer<Exception> formatErrorHandler) {
        this(key, title, formatter);
        this.formatErrorHandler = formatErrorHandler;
    }

}
