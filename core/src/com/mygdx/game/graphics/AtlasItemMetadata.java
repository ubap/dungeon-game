package com.mygdx.game.graphics;

public class AtlasItemMetadata {
    private String id;
    private boolean rotate;
    private int positionX;
    private int positionY;
    private int sizeX;
    private int sizeY;
    private int origX;
    private int origY;
    private int offsetX;
    private int offsetY;
    private int offset;

    public AtlasItemMetadata(String id, boolean rotate, int positionX, int positionY, int sizeX, int sizeY, int origX, int origY, int offsetX, int offsetY, int index) {
        this.id = id;
        this.rotate = rotate;
        this.positionX = positionX;
        this.positionY = positionY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.origX = origX;
        this.origY = origY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offset = index;
    }

    public String getId() {
        return id;
    }

    public boolean isRotate() {
        return rotate;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getOrigX() {
        return origX;
    }

    public int getOrigY() {
        return origY;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getOffset() {
        return offset;
    }
}
