package com.vesas.spacefly.util;

/**
 * Can be used to time code blocks.
 */
public class Timing {
    
    private long startTime;
    private long endTime;

    public void start() {
        startTime = System.nanoTime();
    }

    public void stop() {
        endTime = System.nanoTime();
    }

    public long getElapsedNanos() {
        return endTime - startTime;
    }

    public double getElapsedMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    public double getElapsedSeconds() {
        return (endTime - startTime) / 1_000_000_000.0;
    }

    public static Timing startNew() {
        Timing t = new Timing();
        t.start();
        return t;
    }

}
