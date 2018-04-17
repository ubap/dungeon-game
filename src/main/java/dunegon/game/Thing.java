package dunegon.game;

public abstract class Thing {
    private Position position;

    public abstract void setId(long id);
    public abstract long getId();

    public int getStackPriority() {
        if(isGround())
            return 0;
        else if(isGroundBorder())
            return 1;
        else if(isOnBottom())
            return 2;
        else if(isOnTop())
            return 3;
        else if(isCreature())
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

    public int getAnimationPhases() {
        return getThingType().getAnimationPhases();
    }

    // set
    public void setPosition(Position position) {
        this.position = position;
    }
}
