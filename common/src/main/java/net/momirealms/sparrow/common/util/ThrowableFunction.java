package net.momirealms.sparrow.common.util;

public interface ThrowableFunction<T, V, E extends Throwable> {

    V apply(T t) throws E;
}
