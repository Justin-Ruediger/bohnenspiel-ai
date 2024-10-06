package utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A Thread that when interrupted can finish gracefully and completes the Future:
  */
public abstract class CompletableThread<T> extends Thread{
    public final CompletableFuture<T> completableFuture = new CompletableFuture<>();
    @Override
    public void run() {
        final T result = doWork();

        completableFuture.complete(result);
    }

    /**
     * This will Block the current Thread until a result is Present;
     * @return Result
     */
    public T getResult() {
        try {
            return completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This will Block the current Thread until a result is Present;
     * @return Result
     */
    public T interuptAndGetResult() {
        interrupt();
        return getResult();
    }

    /**
     * remember to end the work when the Thread is interrupted!!!
     */
    abstract protected T doWork();
}
