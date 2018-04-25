package com.mygdx.game.dunegon.game;

import com.mygdx.game.dunegon.io.DatAttrs;
import com.mygdx.game.dunegon.io.ThingTypeManager;
import com.mygdx.game.graphics.Point;

import org.omg.CORBA.DATA_CONVERSION;

public class Creature extends Thing {
    private long id;
    private String name;
    private int healthPercent;
    private Consts.Direction direction;
    private Outfit outfit;
    private boolean allowAppearWalk;
    private boolean removed;
    private Position oldPosition;
    private Position lastStepFromPosition;
    private Position lastStepToPosition;
    private Consts.Direction lastStepDirection;


    private double speedA;
    private double speedB;
    private double speedC;

    public Creature() {
        super();
        this.oldPosition = new Position();
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    protected ThingType getThingType() {
        return ThingTypeManager.getInstance().getThingType(outfit.getId(), DatAttrs.ThingCategory.ThingCategoryCreature);
    }

    @Override
    public boolean isCreature() {
        return true;
    }

    public void setSpeedFormula(double speedA, double speedB, double speedC) {
        this.speedA = speedA;
        this.speedB = speedB;
        this.speedC = speedC;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setHealthPercent(int healthPercent) {
        this.healthPercent = healthPercent;
    }
    public void setDirection(Consts.Direction direction) {
        this.direction = direction;
    }

    public void setOutfit(Outfit outfit) {
        this.outfit = outfit;
    }

    public void allowaAppearWalk() {
        this.allowAppearWalk = true;
    }

    public String getName() {
        return name;
    }

    @Override
    public void onAppear() {
        // creature appeared the first time or wasn't seen for a long time
        if (removed) {
            // stopWalk
            removed = false;
        } // walk
        else if (this.oldPosition != getPosition() && this.oldPosition.isInRange(getPosition(), 1,1) && this.allowAppearWalk) {
            allowAppearWalk = false;
            walk(this.oldPosition, getPosition());

        } // teleport
        else {

        }
    }

    @Override
    public void onDisappear() {
        this.oldPosition = getPosition();

        // todo: dispatcher
//
//        removed = true;
//        // stopWalk
//        if (!isLocalPlayer()) {
//            setPosition(new Position());
//        }
    }

    public void walk(Position oldPosition, Position newPosition) {
        if (oldPosition == newPosition) {
            return;
        }

        this.lastStepDirection = oldPosition.getDirectionFromPosition(newPosition);
        this.lastStepFromPosition = oldPosition;
        this.lastStepToPosition = newPosition;

        setDirection(this.lastStepDirection);

    }

    @Override
    public void draw(Point dest, float scaleFactor) {
        internalDrawOutfit(dest, scaleFactor, direction);
    }

    private void internalDrawOutfit(Point dest, float scaleFactor, Consts.Direction direction) {
        if (outfit.getThingCategory() == DatAttrs.ThingCategory.ThingCategoryCreature) {

            // patternX -> creature direction
            int patternX;
            if (direction == Consts.Direction.NORTH_EAST || direction == Consts.Direction.SOUTH_EAST) {
                patternX = Consts.Direction.EAST.ordinal();
            } else if (direction == Consts.Direction.NORTH_WEST || direction == Consts.Direction.SOUTH_WEST) {
                patternX = Consts.Direction.WEST.ordinal();
            } else {
                patternX = direction.ordinal();
            }

            // mounts
            int patternZ = 0;

            for (int patternY = 0; patternY < getNumPatternY(); patternY++) {
                // addons
                if (patternY > 0) {
                    continue;
                }

                getThingType().draw(dest, scaleFactor, 0, patternX, patternY, patternZ, 0);

                if (getLayers() > 1) {
                    // todo: outfit colors
                   // getThingType().draw(dest, 0, DatAttrs.SpriteMask.YELLOW, patternX, patternY, patternZ, 0);
                }
            }

        }
    }

}
