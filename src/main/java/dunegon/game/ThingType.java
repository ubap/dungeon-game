package dunegon.game;

public class ThingType {
    private Thing[] mTings;

    public ThingType() {

    }

    public void initThingsArray(int size) {
        mTings = new Thing[size];
    }

    public void setThing(Thing thing) {
        int id = thing.getId();
        mTings[id] = thing;
    }

    public Thing getThing(int id) {
        return mTings[id];
    }

    public int getThingCount() {
        return mTings.length;
    }
}
