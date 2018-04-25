package com.mygdx.game.framework;

public abstract class Event {
    protected boolean canceled;
    protected boolean executed;

    public Event() {
        canceled = false;
        executed = false;
    }

    public abstract void callback();

    public void execute() {
        if (!canceled && !executed) {
            callback();
            executed = true;
        }
    }

    public void cancel() {
        canceled = true;
    }

    boolean isCanceled() {
        return this.canceled;
    }

    boolean isExecuted() {
        return this.executed;
    }
}
