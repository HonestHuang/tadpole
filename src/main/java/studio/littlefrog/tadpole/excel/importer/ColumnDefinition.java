package studio.littlefrog.tadpole.excel.importer;

import studio.littlefrog.tadpole.validator.Assert;

import java.util.function.Consumer;
import java.util.function.Function;

public class ColumnDefinition {

    private Integer index;
    private String field;
    private Function<String, Object> formatter;
    private Consumer<Exception> formatErrorHandler;

    public ColumnDefinition(Integer index, String field) {
        Assert.notNull(index, "index不能为空");
        Assert.notBlank(field, "field不能为空");
        this.index = index;
        this.field = field;
    }

    public ColumnDefinition(Integer index, String field, Function<String, Object> formatter) {
        this(index, field);
        this.formatter = formatter;
    }

    public ColumnDefinition(Integer index, String field, Function<String, Object> formatter, Consumer<Exception> formatErrorHandler) {
        this(index, field, formatter);
        this.formatErrorHandler = formatErrorHandler;
    }

    public Integer getIndex() {
        return index;
    }

    public String getField() {
        return field;
    }

    public Function<String, Object> getFormatter() {
        return formatter;
    }

    public Consumer<Exception> getFormatErrorHandler() {
        return formatErrorHandler;
    }
}
