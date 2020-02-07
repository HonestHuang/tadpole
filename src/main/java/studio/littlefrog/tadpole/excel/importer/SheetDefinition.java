package studio.littlefrog.tadpole.excel.importer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import studio.littlefrog.tadpole.excel.CellValueGetter;
import studio.littlefrog.tadpole.excel.importer.setter.Setter;
import studio.littlefrog.tadpole.excel.importer.setter.SetterProvider;
import studio.littlefrog.tadpole.function.TriConsumer;
import studio.littlefrog.tadpole.validator.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author HonestHuang
 */
public class SheetDefinition<T> {
    private Integer index;
    private List<ColumnDefinition> columns = new ArrayList<>();
    private Importer.Builder build;
    private List<T> list;
    private Class<T> klass;
    private Integer start;
    private Integer titleIndex;
    private Integer end;
    private Consumer<T> consumer;
    private TriConsumer<T, Row, Row> rowConsumer;
    private Setter setter;

    public SheetDefinition(Importer.Builder build, Class<T> klass) {
        this(build, klass, 0);
    }

    public SheetDefinition(Importer.Builder build, Class<T> klass, Integer index) {
        this.build = build;
        this.klass = klass;
        this.index = index;
        this.setter = SetterProvider.getInstance().findCandidate(klass);
    }

    public SheetDefinition<T> col(Integer index, String field) {
        columns.add(new ColumnDefinition(index, field));
        return this;
    }

    public SheetDefinition<T> col(Integer index, String field, Function<String, Object> formatter) {
        columns.add(new ColumnDefinition(index, field, formatter));
        return this;
    }

    public SheetDefinition<T> col(Integer index, String field, Function<String, Object> formatter, Consumer<Exception> formatErrorHandler) {
        columns.add(new ColumnDefinition(index, field, formatter, formatErrorHandler));
        return this;
    }

    public SheetDefinition<T> from(Integer start) {
        Assert.notNull(start, "start不能为空");
        Assert.gteZero(start, "start必须大于等于0");
        this.start = start;
        return this;
    }

    public SheetDefinition<T> title(Integer index) {
        Assert.notNull(index, "start不能为空");
        Assert.gteZero(index, "start必须大于等于0");
        this.titleIndex = index;
        return this;
    }

    public SheetDefinition<T> to(Integer end) {
        Assert.notNull(end, "end不能为空");
        Assert.gtZero(end, "end必须大于0");
        this.end = end;
        return this;
    }

    public SheetDefinition<T> list(List<T> list) {
        Assert.notNull(list, "list不能为空");
        this.list = list;
        return this;
    }

    public SheetDefinition<T> consumer(Consumer<T> consumer) {
        Assert.notNull(consumer, "consumer不能为空");
        this.consumer = consumer;
        return this;
    }

    public SheetDefinition<T> rowConsumer(TriConsumer<T, Row, Row> consumer) {
        Assert.notNull(consumer, "consumer不能为空");
        this.rowConsumer = consumer;
        return this;
    }

    public Importer.Builder end() {
        return build;
    }

    public void validate() {
        Assert.isTrue(Objects.nonNull(consumer) || Objects.nonNull(list), "接收不能为空");
        Assert.gtZero(start, "start必须大于0");
        Assert.gtZero(end, "end必须大于0");

    }

    public void generate(Workbook workbook) {
        final Sheet sheet = workbook.getSheetAt(index);

        int _start = getValidStart(sheet.getFirstRowNum());
        int _end = getValidEnd(sheet.getLastRowNum());

        Row titleRow = Objects.nonNull(titleIndex) ? sheet.getRow(titleIndex) : null;

        for (int i = _start; i <= _end; i++) {
            Row row = sheet.getRow(i);
            T obj;
            try {
                obj = klass.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException("实例化失败");
            }

            columns.forEach(column -> {
                Cell cell = Objects.nonNull(row) ? row.getCell(column.getIndex()) : null;
                Function<String, Object> formatter = column.getFormatter();
                Object val;
                if (Objects.nonNull(formatter)) {
                    val = formatter.apply(CellValueGetter.getStringValue(cell));
                } else {
                    val = CellValueGetter.getStringValue(cell);
                }
                setter.set(obj, column.getField(), val);
            });

            if (Objects.nonNull(list)) {
                list.add(obj);
            }
            if (Objects.nonNull(consumer)) {
                consumer.accept(obj);
            }
            if (Objects.nonNull(rowConsumer)) {
                rowConsumer.accept(obj, row, titleRow);
            }
        }


    }


    private int getValidStart(int firstRowNum) {
        if (Objects.isNull(this.start) || this.start < firstRowNum) {
            return firstRowNum;
        }
        return this.start;
    }

    private int getValidEnd(int lastRowNum) {
        if (Objects.isNull(this.end) || this.end > lastRowNum) {
            return lastRowNum;
        }
        return this.end;
    }

}
