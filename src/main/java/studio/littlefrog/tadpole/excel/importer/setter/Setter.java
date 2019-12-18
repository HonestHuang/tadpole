package studio.littlefrog.tadpole.excel.importer.setter;

public interface Setter {
    void set(Object obj, String key, Object val);

    Boolean isCandidate(Class klass);

    Integer sort();
}
