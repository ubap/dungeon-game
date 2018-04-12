package dunegon.game;

public class Position {
    private int mPosX;
    private int mPosY;
    private int mPosZ;

    public Position(int x, int y, int z) {
        mPosX = x;
        mPosY = y;
        mPosZ = z;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("x=")
                .append(mPosX)
                .append(", y=")
                .append(mPosY)
                .append(", z=")
                .append(mPosZ);
        return stringBuilder.toString();
    }
}
