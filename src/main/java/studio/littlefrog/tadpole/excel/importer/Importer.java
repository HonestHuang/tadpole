package studio.littlefrog.tadpole.excel.importer;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.littlefrog.tadpole.validator.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

public class Importer {

    private static Logger logger = LoggerFactory.getLogger(Importer.class);

    private Importer.Builder builder;

    private Importer(Importer.Builder builder) {
        this.builder = builder;
    }

    private Importer() {
    }

    private Workbook workbook() throws Exception {
        switch (builder.version) {
            case 2003:
                return new HSSFWorkbook(builder.inputStream);
            case 2007:
            default:
                return new XSSFWorkbook(builder.inputStream);
        }
    }

    private void exception(Exception e) {
        logger.error("导出excel异常", e);
        Consumer<Exception> consumer = this.builder.exceptionConsumer;
        if (Objects.nonNull(consumer)) {
            consumer.accept(e);
        }
    }

    public void generate() {
        try {

            try (Workbook workbook = workbook()) {
                builder.sheets.forEach(s -> s.generate(workbook));
            } catch (Exception e) {
                exception(e);
            }

        } catch (Exception e) {
            exception(e);
        }
    }

    public static class Builder {

        private List<SheetDefinition> sheets = new ArrayList<>();

        private Short version = 2003;

        private InputStream inputStream;

        private Consumer<Exception> exceptionConsumer;

        /**
         * excel 2007 xlxs格式
         */
        public Importer.Builder v2007() {
            version = 2007;
            return this;
        }

        /**
         * excel 2003 xlxs格式
         */
        public Importer.Builder v2003() {
            version = 2003;
            return this;
        }

        public Importer.Builder input(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Importer.Builder input(File file) {
            try {
                this.inputStream = new FileInputStream(file);
            } catch (Exception e) {
                throw new IllegalStateException("文件输入流创建失败");
            }
            return this;
        }

        public Importer.Builder exception(Consumer<Exception> exceptionConsumer) {
            this.exceptionConsumer = exceptionConsumer;
            return this;
        }

        public <T> SheetDefinition<T> sheet(Class<T> cls) {
            SheetDefinition<T> h = new SheetDefinition<>(this, cls);
            sheets.add(h);
            return h;
        }

        public <T> SheetDefinition<T> sheet(Class<T> cls, Integer index) {
            SheetDefinition<T> h = new SheetDefinition<>(this, cls, index);
            sheets.add(h);
            return h;
        }

        public Importer build() {
            Assert.isTrue(Objects.nonNull(inputStream), "数据输入为空");
            sheets.forEach(SheetDefinition::validate);
            return new Importer(this);
        }
    }


}
