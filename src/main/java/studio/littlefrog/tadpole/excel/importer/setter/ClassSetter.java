package studio.littlefrog.tadpole.excel.importer.setter;

public class ClassSetter implements Setter {


    public void set(Object obj, String key, Object val) {
        //TODO 利用反射设置值
    }

    public Boolean isCandidate(Class klass) {
        return true;
    }

    public Integer sort() {
        return Integer.MIN_VALUE;
    }


    public static Setter newInstance() {
        return new ClassSetter();
    }
}
