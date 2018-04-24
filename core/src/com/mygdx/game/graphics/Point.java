package com.mygdx.game.graphics;

public class Point {
    private int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point multiply(int factor) {
        return new Point(x * factor, y * factor);
    }

    public Point add(Point other) {
        return new Point(x + other.getX(), y + other.getY());
    }

    public Point sub(Point other) {
        return new Point(x - other.getX(), y - other.getY());
    }
}
