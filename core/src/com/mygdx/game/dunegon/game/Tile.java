package com.mygdx.game.dunegon.game;

import com.mygdx.game.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class Tile {
    public static final int MAX_THINGS = 10;

    private Position position;
    private List<Creature> walkingCreatures;
    private List<Thing> things;

    public Tile(Position position) {
        this.walkingCreatures = new ArrayList<Creature>();
        this.things = new ArrayList<Thing>();
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
        thing.onAppear();
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

        thing.onDisappear();
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

    public void addWalkingCreature(Creature creature) {
        walkingCreatures.add(creature);
    }
    public void remvoeWalkingCreature(Creature creature) {
        walkingCreatures.remove(creature);
    }

    public boolean limitsFloorView(boolean isFreeView) {
        // ground and walls limits the view
        Thing firstThing = getThing(0);

        if (firstThing == null) {
            return false;
        }

        if (isFreeView) {
            if (!firstThing.isDontHide() && (firstThing.isGround() || firstThing.isOnBottom())) {
                return true;
            }
        } else if (!firstThing.isDontHide() && (firstThing.isGround() || (firstThing.isOnBottom() && firstThing.isBlockProjectile()))) {
            return true;
        }
        return false;
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<Item>();
        for (Thing thing : things) {
            if (thing.isItem()) {
                items.add((Item) thing);
            }
        }
        return items;
    }

    public List<Creature> getCreatures() {
        List<Creature> creatures = new ArrayList<Creature>();
        for (Thing thing : things) {
            if (thing.isCreature()) {
                creatures.add((Creature) thing);
            }
        }
        return creatures;
    }

    public boolean mustHookEast() {
        for (Thing thing : things) {
            if (thing.isHookEast()) {
                return true;
            }
        }
        return false;
    }

    public boolean mustHookSouth() {
        for (Thing thing : things) {
            if (thing.isHookSouth()) {
                return true;
            }
        }
        return false;
    }

    public boolean isLookPossible() {
        for (Thing thing : things) {
            if (thing.isBlockProjectile()) {
                return false;
            }
        }
        return true;
    }

    public boolean isDrawable() {
        return !things.isEmpty();
    }

    public boolean isFullyOpaque() {
        Thing thing = getThing(0);
        return thing != null && thing.isFullGround();
    }

    public boolean isSingleDimension() {
        for (Thing thing : things) {
            if (thing.getHeight() != 1 || thing.getWidth() != 1) {
                return false;
            }
        }
        return true;
    }

    public Position getPosition() {
        return position;
    }

    public Item getGround() {
        Thing firstObject = getThing(0);
        if (firstObject == null) {
            return null;
        }
        if (firstObject.isGround() && firstObject.isItem()) {
            return (Item) firstObject;
        }
        return null;
    }

    public int getGroundSpeed() {
        int groundSpeed = 100;
        Item ground = getGround();
        if (ground != null) {
            groundSpeed = ground.getGroundSpeed();
        }
        return groundSpeed;
    }

    public boolean isEmpty() {
        return things.size() == 0;
    }

    public boolean canErase() {
        return false; // todo: implement
    }

    public void clean() {
        this.things.clear();
    }

    // todo: lightView
    public void draw(Point dest, float scaleFactor, int drawFlags) {

        int drawElevation = 0;

        for (Thing thing : things) {
            if (!thing.isGround() && !thing.isGroundBorder() && !thing.isOnBottom()) {
                break;
            }

            thing.draw(dest.sub(new Point(Math.round(drawElevation * scaleFactor), Math.round(drawElevation * scaleFactor))), scaleFactor);

            drawElevation += thing.getDrawElevation();
            if (drawElevation > Consts.MAX_ELEVATION) {
                drawElevation = Consts.MAX_ELEVATION;
            }
        }

        // common items in reverse order
        for (int i = things.size() - 1; i >= 0; i--) {
            Thing thing = things.get(i);
            if (thing.isOnTop() || thing.isOnBottom() || thing.isGroundBorder() || thing.isGround() || thing.isCreature()) {
                break;
            }
            thing.draw(dest.sub(new Point(Math.round(drawElevation * scaleFactor), Math.round(drawElevation * scaleFactor))), scaleFactor);

            drawElevation += thing.getDrawElevation();
            if (drawElevation > Consts.MAX_ELEVATION) {
                drawElevation = Consts.MAX_ELEVATION;
            }
        }

        // creatures
        for (Creature creature : this.walkingCreatures) {
            creature.draw(new Point((int) (dest.getX() + ((creature.getPosition().getX() - getPosition().getX())*ThingType.TILE_PIXELS - drawElevation) * scaleFactor),
                    (int) (dest.getY() + ((creature.getPosition().getY() - getPosition().getY())*ThingType.TILE_PIXELS - drawElevation) * scaleFactor)), scaleFactor);
        }

        for (int i = things.size() - 1; i >= 0; i--) {
            Thing thing = things.get(i);
            if (!thing.isCreature()) {
                continue;
            }
            Creature creature = (Creature) thing;
            if (!creature.isWAlking()) {
                thing.draw(dest.sub(new Point(Math.round(drawElevation * scaleFactor), Math.round(drawElevation * scaleFactor))), scaleFactor);
            }
        }

        // effects


        // topitems
        for (Thing thing : things) {
            if (thing.isOnTop()) {
                thing.draw(dest, scaleFactor);
            }
        }

    }
}
