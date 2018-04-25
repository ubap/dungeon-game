package com.mygdx.game.dunegon.game;

public class Position {
    private int x;
    private int y;
    private int z;

    public Position() {
        this.x = -1;
        this.y = -1;
        this.z = -1;
    }

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
    public boolean isValid() {
        return isMapPosition();
    }

    public boolean isInRange(Position position, int xRange, int yRange) {
        return (isInRange(position, xRange, xRange, yRange, yRange));
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

    public static Consts.Direction getDirectionFromPositions(Position fromPosition, Position toPosition) {
        double angle = Math.toDegrees(getAngleFromPositions(fromPosition, toPosition));
        if (angle >= 360 - 22.5 || angle < 0 + 22.5)
            return Consts.Direction.EAST;
        else if (angle >= 45 - 22.5 && angle < 45 + 22.5)
            return Consts.Direction.NORTH_EAST;
        else if (angle >= 90 - 22.5 && angle < 90 + 22.5)
            return Consts.Direction.NORTH;
        else if (angle >= 135 - 22.5 && angle < 135 + 22.5)
            return Consts.Direction.NORTH_WEST;
        else if (angle >= 180 - 22.5 && angle < 180 + 22.5)
            return Consts.Direction.WEST;
        else if (angle >= 225 - 22.5 && angle < 225 + 22.5)
            return Consts.Direction.SOUTH_WEST;
        else if (angle >= 270 - 22.5 && angle < 270 + 22.5)
            return Consts.Direction.SOUTH;
        else if (angle >= 315 - 22.5 && angle < 315 + 22.5)
            return Consts.Direction.SOUTH_EAST;
        else
            return Consts.Direction.INVALID_DIRECTION;
    }

    public Consts.Direction getDirectionFromPosition(Position toPosition) {
        return Position.getDirectionFromPositions(this, toPosition);
    }

    public static double getAngleFromPositions(Position fromPosition, Position toPosition) {
        int dx = toPosition.getX() - fromPosition.getX();
        int dy = toPosition.getY() - fromPosition.getY();
        if (dx == 0 && dy == 0) {
            return -1;
        }

        double angle = Math.atan2(dy * -1f, dx);
        if (angle < 0) {
            angle += 2 * Math.PI;
        }

        return angle;
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
