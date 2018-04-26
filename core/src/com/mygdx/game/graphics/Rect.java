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
    public Point getBottomRight() {
        return new Point(x2, y2);
    }
    public Size getSize() {
        return new Size(getWidth(), getHeight());
    }

    public boolean contains(Point point) {
        return contains(point, false);
    }
    public boolean contains(Point point, boolean insideOnly) {
        int l, r;
        if (x2 < x1 - 1) {
            l = x2;
            r = x1;
        } else {
            l = x1;
            r = x2;
        }
        if (insideOnly) {
            if (point.getX() <= l || point.getX() >= r) {
                return false;
            }
        } else {
            if (point.getX() < l || point.getX() > r) {
                return false;
            }
        }

        int t, b;
        if (y2 < y1 - 1) {
            t = y2;
            b = y1;
        } else {
            t = y1;
            b = y2;
        }
        if (insideOnly) {
            if (point.getY() <= t || point.getY() >= b) {
                return false;
            }
        } else {
            if (point.getY() < t || point.getY() > b) {
                return false;
            }
        }
        return true;
    }


    public Rect setLeft(int x1) {
        Rect rect = new Rect();
        rect.x1 = x1;
        rect.y1 = this.y1;
        rect.x2 = this.x2;
        rect.y2 = this.y2;
        return rect;
    }

    public Rect setTop(int y1) {
        Rect rect = new Rect();
        rect.x1 = this.x1;
        rect.y1 = y1;
        rect.x2 = this.x2;
        rect.y2 = this.y2;
        return rect;
    }

    public Rect setBottom(int y2) {
        Rect rect = new Rect();
        rect.x1 = this.x1;
        rect.y1 = this.y1;
        rect.x2 = this.x2;
        rect.y2 = y2;
        return rect;
    }

    public Rect setRight(int x2) {
        Rect rect = new Rect();
        rect.x1 = this.x1;
        rect.y1 = this.y1;
        rect.x2 = x2;
        rect.y2 = this.y2;
        return rect;
    }

}
