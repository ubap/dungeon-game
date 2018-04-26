package com.mygdx.game.framework;

public class FpsCounter {
    private double frame;
    private double lastTick;

    public void FpsCounter() {
        this.lastTick = System.currentTimeMillis();
    }

    public void frame() {
        this.frame++;
    }

    public double getFps() {
        long currentTick = System.currentTimeMillis();

        double milisPerFrame = (currentTick - lastTick) / frame;
        double fps = 1000 / milisPerFrame;

        if (currentTick - this.lastTick > 3000) {
            this.lastTick = currentTick;
            this.frame = 0;
        }

        return fps;
    }
}
