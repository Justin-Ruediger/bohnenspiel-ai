package utils;

import java.util.concurrent.TimeUnit;

public class Benchmark {

    private long startTime;
    private long endTime;
    private String name;

    /**
     * Starts the benchmark timer.
     */
    public void start() {
        startTime = System.nanoTime(); // Record the current time in nanoseconds
        endTime = 0; // Reset the endTime

        StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        String callerClass = ste.getClassName();
        String callerMethod = ste.getMethodName();
        name = callerClass + ":" + callerMethod;
    }

    /**
     * Stops the benchmark timer.
     */
    public void stop() {
        if (startTime == 0) {
            throw new IllegalStateException("Benchmark timer was not started.");
        }
        endTime = System.nanoTime(); // Record the current time in nanoseconds
    }

    /**
     * Resets the timer.
     */
    public void reset() {
        startTime = 0;
        endTime = 0;
    }

    /**
     * Gets the elapsed time in the desired time unit.
     *
     * @param timeUnit The desired time unit (e.g., TimeUnit.MILLISECONDS, TimeUnit.SECONDS)
     * @return The elapsed time in the specified time unit.
     */
    public long getElapsedTime(TimeUnit timeUnit) {
        if (startTime == 0 || endTime == 0) {
            throw new IllegalStateException("Benchmark timer has not been started and stopped properly.");
        }
        long elapsedTime = endTime - startTime;
        return timeUnit.convert(elapsedTime, TimeUnit.NANOSECONDS);
    }

    /**
     * Gets the elapsed time in nanoseconds.
     *
     * @return The elapsed time in nanoseconds.
     */
    public long getElapsedTimeInNano() {
        return getElapsedTime(TimeUnit.NANOSECONDS);
    }

    /**
     * Gets the elapsed time in milliseconds.
     *
     * @return The elapsed time in milliseconds.
     */
    public long getElapsedTimeInMillis() {
        return getElapsedTime(TimeUnit.MILLISECONDS);
    }

    public void print() {
        System.out.println("[BENCHMARK] " + name + " : " + getElapsedTimeInNano() + "ns - " + getElapsedTimeInMillis() + " ms");
    }
}
