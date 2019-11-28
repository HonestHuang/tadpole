package studio.littlefrog.tadpole.excel.exporter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.littlefrog.tadpole.validator.Assert;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Exporter {

    private static Logger logger = LoggerFactory.getLogger(Exporter.class);

    private Builder builder;

    private Exporter(Builder builder) {
        this.builder = builder;
    }

    private Exporter() {
    }

    private Workbook workbook() {
        switch (builder.version) {
            case 2003:
                return new HSSFWorkbook();
            case 12007:
                return new SXSSFWorkbook(1000);
            case 2007:
            default:
                return new XSSFWorkbook();
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
        OutputStream out = null;
        try {
            out = Objects.isNull(builder.outputStream) ? new ByteArrayOutputStream() : builder.outputStream;

            try (Workbook workbook = workbook()) {
                builder.sheets.forEach(s -> s.generate(workbook));
                workbook.write(out);

                if (Objects.nonNull(builder.outConsumer)) {
                    byte[] bytes = ((ByteArrayOutputStream) out).toByteArray();
                    builder.outConsumer.accept(bytes);
                }
            } catch (Exception e) {
                exception(e);
            }

        } catch (Exception e) {
            exception(e);
        } finally {
            if (Objects.isNull(builder.outputStream) && Objects.nonNull(out)) {
                try {
                    out.close();
                } catch (Exception ignored) {
                }
            }
        }

    }


    public static class Builder {

        private List<SheetDefinition> sheets = new ArrayList<>();

        private Short version = 2003;

        private Boolean withSerialNo = true;

        private String serialNoTitle = "序号";

        private OutputStream outputStream;

        private Consumer<byte[]> outConsumer;

        private Consumer<Exception> exceptionConsumer;

        public Builder serialNo(Boolean withSerialNo) {
            this.withSerialNo = withSerialNo;
            return this;
        }

        public Builder serialNoTitle(String title) {
            Assert.notNull(title, "请勿设置空的序号标题");
            this.serialNoTitle = title;
            return this;
        }

        /**
         * excel 2007 xlxs格式
         */
        public Builder v2007() {
            version = 2007;
            return this;
        }

        public Builder v2007BigData() {
            version = 12007;
            return this;
        }

        /**
         * excel 2003 xlxs格式
         */
        public Builder v2003() {
            version = 2003;
            return this;
        }

        public Builder output(OutputStream outputStream) {
            if (Objects.nonNull(outConsumer)) {
                throw new IllegalArgumentException("已设置输出方式");
            }
            this.outputStream = outputStream;
            return this;
        }

        public Builder output(Consumer<byte[]> outConsumer) {
            if (Objects.nonNull(outputStream)) {
                throw new IllegalArgumentException("已设置输出方式");
            }
            this.outConsumer = outConsumer;
            return this;
        }

        public Builder exception(Consumer<Exception> exceptionConsumer) {
            this.exceptionConsumer = exceptionConsumer;
            return this;
        }

        public <T> SheetDefinition<T> sheet(List<T> list) {
            SheetDefinition<T> h = new SheetDefinition<>(this, list);
            sheets.add(h);
            return h;
        }

        public <T> SheetDefinition<T> sheet(String name, List<T> list) {
            SheetDefinition<T> h = new SheetDefinition<>(this, name, list);
            sheets.add(h);
            return h;
        }

        public <T> SheetDefinition<T> sheet(Iterator<T> iterator) {
            SheetDefinition<T> h = new SheetDefinition<>(this, iterator);
            sheets.add(h);
            return h;
        }

        public <T> SheetDefinition<T> sheet(String name, Iterator<T> iterator) {
            SheetDefinition<T> h = new SheetDefinition<>(this, name, iterator);
            sheets.add(h);
            return h;
        }

        public Exporter build() {
            Assert.isTrue(Objects.nonNull(outputStream) || Objects.nonNull(outConsumer), "数据接收为空");
            sheets.forEach(SheetDefinition::validate);
            return new Exporter(this);
        }

        public Boolean getWithSerialNo() {
            return withSerialNo;
        }

        public String getSerialNoTitle() {
            return serialNoTitle;
        }
    }
}
