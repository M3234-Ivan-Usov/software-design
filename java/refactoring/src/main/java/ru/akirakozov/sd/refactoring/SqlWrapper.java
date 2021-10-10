package ru.akirakozov.sd.refactoring;

/**
 * @author iusov
 * Wrapper for Consumer to execute function which can throw exception
 * @param <T> Function argument type
 * @param <E> Exception, that function can throw
 */
@FunctionalInterface
public interface SqlWrapper<T, E extends Exception> {
    void action(T arg) throws E;

    default void call(T arg) {
        try {
            action(arg);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
