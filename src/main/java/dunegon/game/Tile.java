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

    public Thing getThing(int stackPos) {
        if (stackPos >= 0 && stackPos < things.size()) {
            return things.get(stackPos);
        }
        return null;
    }

    public int getThingStackPos(Thing thing) {
        for (int stackPos = 0; stackPos < things.size(); stackPos++) {
            if (thing == things.get(stackPos)) {
                return stackPos;
            }
        }
        return -1;
    }

    public boolean removeThing(Thing thing) {
        if (thing == null) {
            return false;
        }

        boolean removed = false;

        if (thing.isEffect()) {
            // todo
        } else {
            removed = things.remove(thing);
        }

        return removed;
    }

    public Thing getTopThing() {
        if (isEmpty()) {
            return null;
        }
        for (Thing thing : things) {
            if (!thing.isGround() && thing.isGroundBorder() && !thing.isOnBottom() && !thing.isOnTop() && !thing.isCreature()) {
                return thing;
            }
        }
        return things.get(things.size() - 1);
    }

    public Thing getTopLookThing() {
        if (isEmpty()) {
            return null;
        }
        for (Thing thing : things) {
            if (!thing.isIgnoreLook() && !thing.isGround() && !thing.isGroundBorder() && !thing.isOnBottom() && !thing.isOnTop()) {
                return thing;
            }
        }
        return things.get(0);
    }

    public Thing getTopUseThing() {
        if (isEmpty()) {
            return null;
        }
        for (Thing thing : things) {
            if (thing.isForceUse()
                    || (!thing.isGround() && !thing.isGroundBorder() && !thing.isOnBottom()
                    && !thing.isOnBottom() && !thing.isCreature() && !thing.isSplash())) {
                return thing;
            }
        }
        for (Thing thing : things) {
            if (!thing.isGround() && !thing.isGroundBorder() && !thing.isCreature() && !thing.isSplash()) {
                return thing;
            }
        }

        return things.get(0);
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        for (Thing thing : things) {
            if (thing.isItem()) {
                items.add((Item) thing);
            }
        }
        return items;
    }

    public List<Creature> getCreatures() {
        List<Creature> creatures = new ArrayList<>();
        for (Thing thing : things) {
            if (thing.isCreature()) {
                creatures.add((Creature) thing);
            }
        }
        return creatures;
    }

    public boolean isEmpty() {
        return things.size() == 0;
    }

    public void clean() {
        this.things.clear();
    }
}
