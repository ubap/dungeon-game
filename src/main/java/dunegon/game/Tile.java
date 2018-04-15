package dunegon.game;

import java.util.ArrayList;
import java.util.List;

public class Tile {
    public static final int MAX_THINGS = 10;

    private List<Thing> things;

    public Tile() {
        this.things = new ArrayList<>();
    }

    public void addThing(Thing thing, int stackPos) {

        if (thing.isEffect()) {

        }
    }

    public void clean() {
        this.things.clear();
    }
}
