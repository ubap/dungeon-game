package com.mygdx.game.framework;

public abstract class ScheduledEvent extends Event implements Comparable<ScheduledEvent> {

    private int cyclesExecuted;
    private long ticks;
    private int maxCycles;

    public ScheduledEvent() {
        ticks = System.currentTimeMillis() + getDelay();
        cyclesExecuted = 0;
        maxCycles = 1;
    }

    public abstract int getDelay();
    public int getMaxCycles() {
        return maxCycles;
    }
    public ScheduledEvent setMaxCycles(int maxCycles) {
        this.maxCycles = maxCycles;
        return this;
    }

    @Override
    public void execute() {
        if (!isCanceled() && (getMaxCycles() == 0 || cyclesExecuted < getMaxCycles())) {
            callback();
            executed = true;
        }
        // callback may be used in the next cycle

        cyclesExecuted++;
    }

    public boolean nextCycle() {
        if (!canceled && (getMaxCycles() == 0 || cyclesExecuted < getMaxCycles())) {
            ticks += getDelay();
            return true;
        }

        return false;
    }

    public long getTicks() {
        return ticks;
    }

    public long getRemainingTicks() {
        return ticks - System.currentTimeMillis();
    }

    public int getCyclesExecuted() {
        return cyclesExecuted;
    }

    @Override
    public int compareTo(ScheduledEvent var1) {
        long val =  var1.getTicks() - this.getTicks();
        if (val < 0) {
            return -1;
        } else if (val > 0) {
            return 1;
        }
        return 0;
    }
}
