package studio.littlefrog.tadpole.excel.exporter;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class SheetDefinition<T> {
    private String name;
    private List<ColumnDefinition<T>> columns = new ArrayList<>();
    private Exporter.Builder build;
    private Iterator<T> iterator;

    public SheetDefinition(Exporter.Builder build, Iterator<T> iterator) {
        this.build = build;
        this.iterator = iterator;
    }

    public SheetDefinition(Exporter.Builder build, List<T> data) {
        this(build, data.iterator());
    }


    public SheetDefinition(Exporter.Builder build, String name, Iterator<T> iterator) {
        this(build, iterator);
        this.name = name;
    }

    public SheetDefinition(Exporter.Builder build, String name, List<T> data) {
        this(build, data);
        this.name = name;
    }

    public SheetDefinition<T> col(String key, String title) {
        columns.add(new ColumnDefinition<>(key, title));
        return this;
    }

    public SheetDefinition<T> col(String key, String title, BiFunction<T, Object, String> formatter) {
        columns.add(new ColumnDefinition<>(key, title, formatter));
        return this;
    }

    public SheetDefinition<T> col(String key, String title, BiFunction<T, Object, String> formatter, Consumer<Exception> formatErrorHandler) {
        columns.add(new ColumnDefinition<>(key, title, formatter, formatErrorHandler));
        return this;
    }

    public Exporter.Builder end() {
        return build;
    }

    public void validate() {
//        Assert.notBlank(name,"");
    }
//    public Boolean exist() {
//        return StringUtils.isNotBlank(name) || CollectionUtils.isNotEmpty(columns);
//    }
//

    @SuppressWarnings("unchecked")
    public void generate(Workbook workbook) {


        final Sheet sheet = StringUtils.isNotBlank(name) ? workbook.createSheet(this.name) : workbook.createSheet();
        Row rowTitle = sheet.createRow(0);

        Boolean withSerialNo = this.build.getWithSerialNo();
        if (withSerialNo) {
            rowTitle.createCell(0).setCellValue(this.build.getSerialNoTitle());
        }
        //标题栏
        for (int i = 0, len = columns.size(); i < len; i++) {
            int index = i + (withSerialNo ? 1 : 0);
            rowTitle.createCell(index).setCellValue(columns.get(i).title);
        }
        //解析内容
        int rowNo = 0;
        while (iterator.hasNext()) {
            Row row = sheet.createRow(rowNo + 1);
            T data = iterator.next();
            if (withSerialNo) {
                row.createCell(0).setCellValue(rowNo + 1);
            }

            for (int i = 0, len = columns.size(); i < len; i++) {
                ColumnDefinition<T> c = columns.get(i);
                int index = i + (withSerialNo ? 1 : 0);
                Cell cell = row.createCell(index);

                Object value;
                try {
                    value = getFieldValue(data, c.key, data.getClass()).orElse("");
                } catch (Exception e) {
                    value = e.getMessage();
                    if (Objects.nonNull(c.formatErrorHandler)) {
                        c.formatErrorHandler.accept(e);
                    }
                }

                String formatValue;
                if (Objects.nonNull(c.formatter)) {
                    if (StringUtils.isNotBlank(Objects.toString(value, ""))) {
                        formatValue = c.formatter.apply(data, value);
                    } else {
                        formatValue = "";
                    }
                } else {
                    formatValue = Objects.toString(value,"");
                }
                cell.setCellValue(formatValue);
            }
            rowNo++;
        }
    }

    private Optional<Object> getFieldValue(T obj, String field, Class cls) {
        if (Objects.isNull(cls)) {
            return Optional.empty();
        } else if (cls.isAssignableFrom(Map.class)) {
            return Optional.ofNullable(((Map) obj).get(field));
        } else {
            try {
                Field f = cls.getDeclaredField(field);
                f.setAccessible(true);
                return Optional.ofNullable(f.get(obj));
            } catch (Exception e) {
                return getFieldValue(obj, field, cls.getSuperclass());
            }
        }
    }

}
