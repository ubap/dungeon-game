package com.mygdx.game.framework;

public class Timer {
    private long startTicks;
    private boolean stopped;

    public Timer() {
        restart();
    }

    public void restart() {
        this.startTicks = System.currentTimeMillis();
        this.stopped = false;
    }

    public long getStartTicks() {
        return startTicks;
    }

    public long getElapsedTicks() {
        return System.currentTimeMillis() - startTicks;
    }

    public float getTimeElapse() {
        return getElapsedTicks() / 1000.0f;
    }
}
