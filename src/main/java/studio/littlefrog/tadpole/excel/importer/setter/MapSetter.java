package studio.littlefrog.tadpole.excel.importer.setter;

import java.util.Map;

public class MapSetter implements Setter {

    @Override
    @SuppressWarnings("unchecked")
    public void set(Object obj, String key, Object val) {
        Map<String, Object> map = (Map<String, Object>) obj;
        map.put(key, val);
    }

    @Override
    public Boolean isCandidate(Class klass) {
        return Map.class.isAssignableFrom(klass);
    }

    @Override
    public Integer sort() {
        return 1;
    }

    public static Setter newInstance() {
        return new MapSetter();
    }
}
