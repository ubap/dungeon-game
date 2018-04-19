package com.mygdx.game.graphics;

public class Size {
    private int width, height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getArea() {
        return width * height;
    }

    public Point toPoint() {
        return new Point(width, height);
    }
    public Size multiply(int factor) {
        return new Size(width * factor, height * factor);
    }
}
