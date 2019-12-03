package studio.littlefrog.tadpole.excel.importer.setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class SetterProvider {

    private final static AtomicReference<SetterProvider> holder = new AtomicReference<>();
    private List<Setter> setters = new ArrayList<>();


    public static SetterProvider getInstance() {
        SetterProvider instance = holder.get();
        if (Objects.isNull(instance)) {
            SetterProvider _instance = new SetterProvider();
            return holder.compareAndSet(null, _instance) ? _instance : holder.get();
        }
        return instance;
    }

    private SetterProvider() {
        setters.add(ClassSetter.newInstance());
        setters.add(MapSetter.newInstance());
    }

    public void addSetter(Setter setter) {
        setters.add(setter);
    }

    public Setter findCandidate(Class klass) {
        return setters
                .stream()
                .sorted(Comparator.comparing(Setter::sort))
                .filter(s -> s.isCandidate(klass))
                .findFirst()
                .orElse(ClassSetter.newInstance())
                ;
    }


}
