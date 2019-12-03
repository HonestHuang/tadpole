package studio.littlefrog.tadpole.excel.importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.littlefrog.tadpole.excel.exporter.SheetDefinition;
import studio.littlefrog.tadpole.validator.Assert;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Importer {

//    private static List v;
    private static Logger logger = LoggerFactory.getLogger(Importer.class);

    private Importer.Builder builder;

    private Importer(Importer.Builder builder) {
        this.builder = builder;
    }

    private Importer() {
    }


    public void generate() {

    }

    public static class Builder {

        private List<SheetDefinition> sheets = new ArrayList<>();

        private Short version = 2003;

        private InputStream inputStream;

        private Consumer<byte[]> outConsumer;

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

        public Importer.Builder exception(Consumer<Exception> exceptionConsumer) {
            this.exceptionConsumer = exceptionConsumer;
            return this;
        }

//        public <T> SheetDefinition<T> sheet(List<T> list) {
//            SheetDefinition<T> h = new SheetDefinition(this, list);
//            sheets.add(h);
//            return h;
//        }
//
//        public <T> SheetDefinition<T> sheet(String name, List<T> list) {
//            SheetDefinition<T> h = new SheetDefinition<>(this, name, list);
//            sheets.add(h);
//            return h;
//        }
//
//        public <T> SheetDefinition<T> sheet(Iterator<T> iterator) {
//            SheetDefinition<T> h = new SheetDefinition<>(this, iterator);
//            sheets.add(h);
//            return h;
//        }
//
//        public <T> SheetDefinition<T> sheet(String name, Iterator<T> iterator) {
//            SheetDefinition<T> h = new SheetDefinition<>(this, name, iterator);
//            sheets.add(h);
//            return h;
//        }

        public Importer build() {
            Assert.isTrue(Objects.nonNull(inputStream), "数据输入为空");
            sheets.forEach(SheetDefinition::validate);
            return new Importer(this);
        }
    }

}
