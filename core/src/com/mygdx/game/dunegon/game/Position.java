package com.mygdx.game.dunegon.game;

public class Position {
    private int x;
    private int y;
    private int z;

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public int getZ() {
        return this.z;
    }

    public boolean isMapPosition() {
        return (x >=0 && y >= 0 && z >= 0 && x < 65535 && y < 65535 && z <= Consts.MAX_Z);
    }

    public boolean isInRange(Position position, int minXRange, int maxXRange, int minYRange, int maxYRange) {
        return (position.getX() >= x - minXRange && position.getX() <= x + maxXRange
                && position.getY() >= y - minYRange && position.getY() <= y + maxYRange
                && position.getZ() == z);
    }

    public Position coveredUp() {
        return coveredUp(1);
    }
    public Position coveredUp(int n) {
        int nx = x + n, ny = y + n, nz = z - n;
        if (nx >= 0 && nx <= 65535 && ny >= 0 && ny <= 65535 && nz >= 0 && nz <= Consts.MAX_Z) {
            return new Position(nx, ny, nz);
        }
        return null;
    }

    public Position up() {
        return up(1);
    }
    public Position up(int n) {
        int nz = this.z - n;
        if (nz >= 0 && nz <= Consts.MAX_Z) {
            return new Position(this.x, this.y, nz);
        }
        return null;
    }

    public Position coveredDown() {
        return coveredDown(1);
    }
    public Position coveredDown(int n) {
        int nx = x - n, ny = y - n, nz = z + n;
        if (nx >= 0 && nx <= 65535 && ny >= 0 && ny <= 65535 && nz >= 0 && nz <= Consts.MAX_Z) {
            return new Position(nx, ny, nz);
        }
        return null;
    }
    public Position translated(int x, int y) {
        return new Position(this.x + x, this.y + y, this.z);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("x=")
                .append(x)
                .append(", y=")
                .append(y)
                .append(", z=")
                .append(z);
        return stringBuilder.toString();
    }
}
