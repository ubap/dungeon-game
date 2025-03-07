package com.mygdx.game.dunegon.game;

import com.mygdx.game.dunegon.io.DatAttrs;
import com.mygdx.game.dunegon.io.ThingTypeManager;
import com.mygdx.game.framework.EventDispatcher;
import com.mygdx.game.framework.ScheduledEvent;
import com.mygdx.game.framework.Timer;
import com.mygdx.game.graphics.Point;
import com.mygdx.game.graphics.Rect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Creature extends Thing {
    private static Logger LOGGER = LoggerFactory.getLogger(Creature.class.getSimpleName());

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
    private Consts.Direction walkTurnDirection;
    private boolean walking;
    private Timer walkTimer;
    private int walkedPixels;
    private Point walkOffset;
    private int walkAnimationPhase;
    private Tile walkingTile;

    private boolean footStepDrawn;
    private Timer footTimer;
    private int footStep;

    private ScheduledEvent walkFinishAnimEvent;
    private ScheduledEvent walkUpdateEvent;

    private int baseSpeed;
    private int speed;
    private double speedA;
    private double speedB;
    private double speedC;

    public Creature() {
        super();
        this.oldPosition = new Position();
        this.walkTimer = new Timer();
        this.footTimer = new Timer();
        this.walkOffset = new Point(0, 0);
        this.speed = 200;
        this.speedA = -1;
        this.speedB = -1;
        this.speedC = -1;
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
    public boolean hasSpeedFormula() {
        return this.speedA != -1 && this.speedB != -1 && this.speedC != -1;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setBaseSpeed(int baseSpeed) {
        this.baseSpeed = baseSpeed;
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
    public boolean isWAlking() {
        return this.walking;
    }

    public int getStepDuration() {
        return getStepDuration(false, Consts.Direction.INVALID_DIRECTION);
    }
    public int getStepDuration(boolean ignoreDiagonal) {
        return getStepDuration(ignoreDiagonal, Consts.Direction.INVALID_DIRECTION);
    }
    public int getStepDuration(boolean ignoreDiagonal, Consts.Direction direction) {
        int speed = this.speed;
        if (speed < 0) {
            return 0;
        }

        speed *= 2;

        int groundSpeed = 0;
        Position tilePosition;
        if (this.direction == Consts.Direction.INVALID_DIRECTION) {
            tilePosition = this.lastStepToPosition;
        } else {
            tilePosition = getPosition().translatedToDirection(direction);
        }

        if (!tilePosition.isValid()) {
            tilePosition = getPosition();
        }
        Tile tile = Game.getInstance().getMap().getTile(tilePosition);
        if (tile != null) {
            groundSpeed = tile.getGroundSpeed();
            if (groundSpeed == 0) {
                groundSpeed = 100;
            }
        }

        int interval = 100;
        if (groundSpeed > 0 && speed > 0) {
            interval = 1000 * groundSpeed;
        }

        if (hasSpeedFormula()) {
            int formulatedSpeed = 1;
            if (speed > -speedB) {
                formulatedSpeed = Math.max(1, (int) Math.floor( (this.speedA * Math.log((speed / 2) + this.speedB) + this.speedC) + 0.5 ));
            }
            interval = (int) Math.floor(interval / (double)formulatedSpeed);
        } else {
            interval /= speed;
        }

        interval = (interval / Game.getInstance().getServerBeat()) * Game.getInstance().getServerBeat();

        float factor = 3;
        interval = Math.max(interval, Game.getInstance().getServerBeat());
        if (!ignoreDiagonal && (lastStepDirection == Consts.Direction.NORTH_WEST || lastStepDirection == Consts.Direction.NORTH_EAST
                || lastStepDirection == Consts.Direction.SOUTH_WEST || lastStepDirection == Consts.Direction.SOUTH_EAST)) {
            interval *= factor;
        }

        return interval;
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

        this.walking = true;
        this.walkTimer.restart();
        this.walkedPixels = 0;

        // no direction need to be changed when the walk ends
        this.walkTurnDirection = Consts.Direction.INVALID_DIRECTION;

        // starts updating walk
        nextWalkUpdate();
    }

    public void nextWalkUpdate() {
        if (this.walkUpdateEvent != null) {
            this.walkUpdateEvent.cancel();
        }

        // do the update
        updateWalk();

        // schedule next update
        if (this.walking) {
            final int delay = getStepDuration() / 32;
            this.walkUpdateEvent = new ScheduledEvent() {
                @Override
                public int getDelay() {
                    return delay;
                }

                @Override
                public void callback() {
                    Creature.this.walkUpdateEvent = null;
                    Creature.this.nextWalkUpdate();
                }
            };
            EventDispatcher.getInstance().scheduleEvent(this.walkUpdateEvent);
        }
    }

    public void updateWalk() {
        float walkTicksPerPixel = getStepDuration(true) / 32;
        int totalPixelsWalked = (int) Math.min(this.walkTimer.getElapsedTicks() / walkTicksPerPixel, 32.0f);

        // needed for paralyze effect
        this.walkedPixels = Math.max(walkedPixels, totalPixelsWalked);

        // update walk animation and offsets
        updateWalkAnimation(totalPixelsWalked);
        updateWalkOffset(this.walkedPixels);
        updateWalkingTile();

        // terminate walk
        if (walking && walkTimer.getElapsedTicks() >= getStepDuration()) {
            terminateWalk();
        }
    }

    public void terminateWalk() {
        if (walkUpdateEvent != null) {
            walkUpdateEvent.cancel();
            walkUpdateEvent = null;
        }

        // now the walk has ended, do any scheduled turn

        if (this.walkingTile != null) {
            this.walkingTile.remvoeWalkingCreature(this);
            this.walkingTile = null;
        }

        walking = false;
        walkedPixels = 0;

        // reset walk animation states
        this.walkOffset = new Point(0, 0);
        this.walkAnimationPhase = 0;
    }

    private void updateWalkOffset(int totalPixelsWalked) {
        // LOGGER.info("update walk offset: pixels: {}", totalPixelsWalked);
        this.walkOffset = new Point(0, 0);
        if (this.direction == Consts.Direction.NORTH || this.direction == Consts.Direction.NORTH_EAST
                || this.direction == Consts.Direction.NORTH_WEST) {

            this.walkOffset = new Point(this.walkOffset.getX(), 32 - totalPixelsWalked);
        } else if (this.direction == Consts.Direction.SOUTH || this.direction == Consts.Direction.SOUTH_EAST
                || this.direction == Consts.Direction.SOUTH_WEST) {

            this.walkOffset = new Point(this.walkOffset.getX(), totalPixelsWalked  - 32);
        }

        if (this.direction == Consts.Direction.EAST || this.direction == Consts.Direction.NORTH_EAST
                || this.direction == Consts.Direction.SOUTH_EAST) {

            this.walkOffset = new Point(totalPixelsWalked - 32, this.walkOffset.getY());
        } else if (this.direction == Consts.Direction.WEST || this.direction == Consts.Direction.NORTH_WEST
                || this.direction == Consts.Direction.SOUTH_WEST) {

            this.walkOffset = new Point(32 - totalPixelsWalked, this.walkOffset.getY());
        }
    }

    private void updateWalkingTile() {
        Tile newWalkingTile = null;
        Rect virtualCreatureRect = new Rect(ThingType.TILE_PIXELS + (walkOffset.getX() - getDisplacement().getX()),
                                            ThingType.TILE_PIXELS + (walkOffset.getY() - getDisplacement().getY()),
                                                ThingType.TILE_PIXELS , ThingType.TILE_PIXELS);
        for (int xi = -1; xi <= 1 && newWalkingTile == null ; xi++) {
            for (int yi = -1; yi <= 1 && newWalkingTile == null; yi++) {
                Rect virtualTileRect = new Rect( (xi+1)*ThingType.TILE_PIXELS, (yi+1) * ThingType.TILE_PIXELS,
                                                    ThingType.TILE_PIXELS, ThingType.TILE_PIXELS);

                // only render creatures where bottom right is inside tile rect
                if (virtualTileRect.contains(virtualCreatureRect.getBottomRight())) {
                    Position tilePosition = getPosition().translated(xi, yi);
                    // LOGGER.info("updateWalkingTile tilePosition: {} {}", tilePosition);
                    newWalkingTile = Game.getInstance().getMap().getTile(tilePosition);
                }
            }
        }

        if (newWalkingTile != this.walkingTile) {
            if (this.walkingTile != null) {
                this.walkingTile.remvoeWalkingCreature(this);
            }
            if (newWalkingTile != null) {
                newWalkingTile.addWalkingCreature(this);
            }

            // recache visible tiles in map views
            if (newWalkingTile != null && newWalkingTile.isEmpty()) {
                Game.getInstance().getMap().notificateTileUpdate(newWalkingTile.getPosition());
            }
            this.walkingTile = newWalkingTile;
        }
    }

    private void updateWalkAnimation(int totalPixelsWalked) {
        if (this.outfit.getThingCategory() != DatAttrs.ThingCategory.ThingCategoryCreature) {
            return;
        }

        int footAnimPhases = getAnimationPhases() - 1;
        int footDelay = getStepDuration(true) / 3;
        if (footAnimPhases == 0) {
            this.walkAnimationPhase = 0;
        } else if (this.footStepDrawn && this.footTimer.getElapsedTicks() >= footDelay && totalPixelsWalked < 32) {
            this.footStep++;
            this.walkAnimationPhase = 1 + (this.footStep % footAnimPhases);
            this.footStepDrawn = false;
            this.footTimer.restart();
        } else if (this.walkAnimationPhase == 0 && totalPixelsWalked < 32) {
            walkAnimationPhase = 1 + (this.footStep & footAnimPhases);
        }

        if (totalPixelsWalked == 32 && this.walkFinishAnimEvent == null) {
            final int delay = Math.min(footDelay, 200);
            this.walkFinishAnimEvent = new ScheduledEvent() {
                @Override
                public int getDelay() {
                    return delay;
                }
                @Override
                public void callback() {
                    if (!Creature.this.walking || Creature.this.walkTimer.getElapsedTicks() >= Creature.this.getStepDuration(true)) {
                        Creature.this.walkAnimationPhase = 0;
                    }
                    Creature.this.walkFinishAnimEvent = null;
                }
            };
            EventDispatcher.getInstance().scheduleEvent(this.walkFinishAnimEvent);
        }
    }

    @Override
    public void draw(Point dest, float scaleFactor) {
        boolean animate = true;
        Point animationOffset = animate ? this.walkOffset : new Point(0, 0);

        internalDrawOutfit(dest.add( animationOffset.multiply(scaleFactor) ), scaleFactor, direction);
        this.footStepDrawn = true;
    }

    private void internalDrawOutfit(Point dest, float scaleFactor, Consts.Direction direction) {
        if (outfit.getThingCategory() == DatAttrs.ThingCategory.ThingCategoryCreature) {

            boolean animateWalk = true;
            boolean animateIdle = true;
            int animationPhase = animateWalk ? this.walkAnimationPhase : 0;

            if (isAnimateAlways() && animateIdle) {
                int ticksPerFrame = 1000 / getAnimationPhases();
                animationPhase = (int) (System.currentTimeMillis() % (ticksPerFrame * getAnimationPhases())) / ticksPerFrame;
            }

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

                getThingType().draw(dest, scaleFactor, 0, patternX, patternY, patternZ, animationPhase);

                if (getLayers() > 1) {
                    // todo: outfit colors
                   // getThingType().draw(dest, 0, DatAttrs.SpriteMask.YELLOW, patternX, patternY, patternZ, 0);
                }
            }

        }
    }

}
