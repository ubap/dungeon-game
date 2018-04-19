package com.mygdx.game.graphics;

public class Rect {
    private int x1, y1, x2, y2;

    public Rect() {
        x1 = -1;
        y1 = -1;
        x2 = -1;
        y2 = -1;
    }

    public Rect(int x, int y, int width, int height) {
        x1 = x;
        y1 = y;
        x2 = x + width - 1;
        y2 = y + width - 1;
    }

    public int getLeft() {
        return x1;
    }
    public int getTop() {
        return y1;
    }
    public int getRight() {
        return x2;
    }
    public int getBottom() {
        return y2;
    }
    public int getWidth() {
        return x2 - x1 + 1;
    }
    public int getHeight() {
        return y2 - y1 + 1;
    }
}
