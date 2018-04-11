package dunegon.game;

public class ThingType {
    private Thing[] mTings;

    public ThingType() {

    }

    public void initThingsArray(int size) {
        mTings = new Thing[size];
    }

    public Thing[] getThings() {
        return mTings;
    }

    public int getThingCount() {
        return mTings.length;
    }
}
