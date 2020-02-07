package studio.littlefrog.tadpole.excel.importer.setter;


import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.littlefrog.tadpole.excel.exporter.Exporter;

public class ClassSetter implements Setter {
    private static Logger logger = LoggerFactory.getLogger(Exporter.class);
    private Class cls;

    public void set(Object obj, String key, Object val) {
        try {
            FieldUtils.writeDeclaredField(obj, key, val, true);
        } catch (Exception e) {
            logger.error("写入失败", e);
        }
    }

    @Override
    public Boolean isCandidate(Class klass) {
        return true;
    }

    @Override
    public Integer sort() {
        return Integer.MIN_VALUE;
    }

    public ClassSetter() {
//        this.cls = cls;
    }

    public static Setter newInstance() {
        return new ClassSetter();
    }
}
