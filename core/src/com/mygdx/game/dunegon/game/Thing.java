package com.mygdx.game.dunegon.game;

import com.mygdx.game.graphics.Point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Thing {
    private static Logger sLogger = LoggerFactory.getLogger(Thing.class.getSimpleName());

    private Position position;

    public abstract void setId(long id);
    public abstract long getId();

    public int getStackPriority() {
        if (isGround())
            return 0;
        else if (isGroundBorder())
            return 1;
        else if (isOnBottom())
            return 2;
        else if (isOnTop())
            return 3;
        else if (isCreature())
            return 4;
        else // common items
            return 5;
    }
    protected abstract ThingType getThingType();

    public boolean isPlayer() {
        return false;
    }
    public boolean isLocalPlayer() {
        return false;
    }
    public boolean isCreature() {
        return false;
    }
    public boolean isMonster() {
        return false;
    }
    public boolean isNpc() {
        return false;
    }
    public boolean isEffect() {
        return false;
    }
    public boolean isItem() {
        return false;
    }


    // type shortcuts
    public boolean isGround() {
        return getThingType().isGround();
    }
    public boolean isFullGround() {
        return getThingType().isFullGround();
    }
    public boolean isGroundBorder() {
        return getThingType().isGroundBorder();
    }
    public boolean isOnBottom() {
        return getThingType().isOnBottom();
    }
    public boolean isOnTop() {
        return getThingType().isOnTop();
    }
    public boolean isStackable() {
        return getThingType().isStackable();
    }
    public boolean isFluidContainer() {
        return getThingType().isFluidContainer();
    }
    public boolean isSplash() {
        return getThingType().isSplash();
    }
    public boolean isTopEffect() {
        return getThingType().isTopEffect();
    }
    public boolean isForceUse() {
        return getThingType().isForceUse();
    }
    public boolean isIgnoreLook() {
        return getThingType().isIgnoreLook();
    }
    public boolean isHangable() {
        return getThingType().isHangable();
    }
    public boolean isHookEast() {
        return getThingType().isHookEast();
    }
    public boolean isHookSouth() {
        return getThingType().isHookSouth();
    }
    public boolean isBlockProjectile() {
        return getThingType().isBlockProjectile();
    }
    public boolean isDontHide() {
        return getThingType().isDontHide();
    }

    public int getNumPatternX() {
        return getThingType().getPatternX();
    }
    public int getNumPatternY() {
        return getThingType().getPatternY();
    }
    public int getNumPatternZ() {
        return getThingType().getPatternZ();
    }
    public int getDrawElevation() {
        return getThingType().getDrawElevation();
    }
    public int getLayers() {
        return getThingType().getLayers();
    }
    public int getHeight() {
        return getThingType().getHeight();
    }
    public int getWidth() {
        return getThingType().getWidth();
    }
    public int getAnimationPhases() {
        return getThingType().getAnimationPhases();
    }
    public boolean isAnimateAlways() {
        return getThingType().isAnimateAlways();
    }
    public Position getPosition() {
        return position;
    }
    public int getGroundSpeed() {
        return getThingType().getGroundSpeed();
    }
    public Tile getTile() {
        return Game.getInstance().getMap().getTile(position);
    }
    public int getStackPos() {
        if (position.getX() == 0xFFFF && isItem()) {
            return position.getZ();
        } else {
            Tile tile = getTile();
            if (tile == null) {
                sLogger.warn("got a thing with invalid stackpos");
                return -1;
            }
            return tile.getThingStackPos(this);
        }
    }
    public Point getDisplacement() {
        return getThingType().getDisplacement();
    }

    // set
    public void setPosition(Position position) {
        this.position = position;
    }

    public abstract void onAppear();
    public abstract void onDisappear();

    public void draw(Point dest, float scaleFactor) {
        getThingType().draw(dest, 0, 0, 0, 0, 0, 0);
    }
}
