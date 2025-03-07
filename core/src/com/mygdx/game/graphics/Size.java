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
    public Size multiply(float factor) {
        return new Size(Math.round(width * factor), Math.round(height * factor));
    }
    public Size add(Size size) {
        return new Size(this.width + size.width, this.height + size.height);
    }
}
