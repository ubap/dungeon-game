package com.mygdx.game.dunegon.game;

public class AwareRange {
    private int top;
    private int right;
    private int bottom;
    private int left;

    public AwareRange(int top, int right, int bottom, int left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public int getTop() {
        return top;
    }
    public int getRight() {
        return right;
    }
    public int getBottom() {
        return bottom;
    }
    public int getLeft() {
        return left;
    }

    public void setTop(int top) {
        this.top = top;
    }
    public void setRight(int right) {
        this.right = right;
    }
    public void setBottom(int bottom) {
        this.bottom = bottom;
    }
    public void setLeft(int left) {
        this.left = left;
    }

    public int horizontal() {
        return left + right + 1;
    }

    public int vertical() {
        return top + bottom + 1;
    }
}
