package com.cht.easygrpc.stream;

/**
 * @author : chenhaitao934
 * @date : 1:57 下午 2020/10/9
 */
public interface EasyGrpcStreamObserver<V> {

    /**
     * Receives a value from the stream.
     *
     * <p>Can be called many times but is never called after {@link #onError(Throwable)} or {@link
     * #onCompleted()} are called.
     *
     * <p>Unary calls must invoke onNext at most once.  Clients may invoke onNext at most once for
     * server streaming calls, but may receive many onNext callbacks.  Servers may invoke onNext at
     * most once for client streaming calls, but may receive many onNext callbacks.
     *
     * <p>If an exception is thrown by an implementation the caller is expected to terminate the
     * stream by calling {@link #onError(Throwable)} with the caught exception prior to
     * propagating it.
     *
     * @param value the value passed to the stream
     */
    void onNext(V value);

    /**
     * Receives a terminating error from the stream.
     *
     * <p>May only be called once and if called it must be the last method called. In particular if an
     * exception is thrown by an implementation of {@code onError} no further calls to any method are
     * allowed.
     *
     * <p>{@code t} should be a {@link io.grpc.StatusException} or {@link
     * io.grpc.StatusRuntimeException}, but other {@code Throwable} types are possible. Callers should
     * generally convert from a {@link io.grpc.Status} via {@link io.grpc.Status#asException()} or
     * {@link io.grpc.Status#asRuntimeException()}. Implementations should generally convert to a
     * {@code Status} via {@link io.grpc.Status#fromThrowable(Throwable)}.
     *
     * @param t the error occurred on the stream
     */
    void onError(Throwable t);

    /**
     * Receives a notification of successful stream completion.
     *
     * <p>May only be called once and if called it must be the last method called. In particular if an
     * exception is thrown by an implementation of {@code onCompleted} no further calls to any method
     * are allowed.
     */
    void onCompleted();
}
