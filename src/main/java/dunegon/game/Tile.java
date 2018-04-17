package dunegon.game;

import java.util.ArrayList;
import java.util.List;

public class Tile {
    public static final int MAX_THINGS = 10;

    private Position position;
    private List<Thing> things;

    public Tile(Position position) {
        this.things = new ArrayList<>();
        this.position = position;
    }

    public void addThing(Thing thing, int stackPos) {
        if (thing.isEffect()) {
            if (thing.isTopEffect()) {
                things.add(0, thing);
            } else {
                things.add(thing);
            }
        } else {
            // priority                                    854
            // 0 - ground,                        -->      -->
            // 1 - ground borders                 -->      -->
            // 2 - bottom (walls),                -->      -->
            // 3 - on top (doors)                 -->      -->
            // 4 - creatures, from top to bottom  <--      -->
            // 5 - items, from top to bottom      <--      <--

            if (stackPos < 0 || stackPos == 255) {
                int priority = thing.getStackPriority();
                // -1 or 255 => auto detect position
                // -2        => append
                boolean append;
                if (stackPos == -2) {
                    append = true;
                } else {
                    append = (priority <= 3);
                    // todo: simplify
                    if (priority == 4) {
                        append = !append;
                    }
                }

                for (stackPos = 0; stackPos < things.size(); stackPos++) {
                    int otherPriority = things.get(stackPos).getStackPriority();
                    if ((append && otherPriority > priority) || ( !append && otherPriority >= priority)) {
                        break;
                    }
                }
            } else if (stackPos > things.size()) {
                stackPos = things.size();
            }

            things.add(stackPos, thing);
            if (things.size() > MAX_THINGS) {
                // todo: remove last thing
            }
        }

        thing.setPosition(position);
    }

    public void clean() {
        this.things.clear();
    }
}
