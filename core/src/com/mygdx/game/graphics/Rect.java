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
        y2 = y + height - 1;
    }
    public Rect(Point topLeft, Point p2) {
        x1 = topLeft.getX();
        y1 = topLeft.getY();
        x2 = p2.getX();
        y2 = p2.getY();
    }
    public Rect(int x, int y, Size size) {
        x1 = x;
        y1 = y;
        x2 = x + size.getWidth()-1;
        y2 = y + size.getHeight()-1;
    }
    public Rect(Point topLeft, Size size) {
        x1 = topLeft.getX();
        y1 = topLeft.getY();
        x2 = x1 + size.getWidth()-1;
        y2 = y1 + size.getHeight()-1;
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
    public Point getTopLeft() { return new Point(x1, y1); }
    public Size getSize() {
        return new Size(getWidth(), getHeight());
    }
}
