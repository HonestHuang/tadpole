package studio.littlefrog.tadpole.function;

/**
 * @author HuangShuCheng
 */
@FunctionalInterface
public interface TriConsumer<T, E, U> {

    void accept(T t, E e, U u);
}
