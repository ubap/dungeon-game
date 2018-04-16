package dunegon.game;

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
