package com.mygdx.game.dunegon.game;

import com.mygdx.game.dunegon.io.DatAttrs;
import com.mygdx.game.dunegon.io.ThingTypeManager;
import com.mygdx.game.graphics.Point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class Item extends Thing {
    private static Logger LOGGER = LoggerFactory.getLogger(Item.class.getSimpleName());

    private long clientId;
    private int countOrSubType;

    public static Item create(int id) {
        Item val = new Item();
        val.setId(id);

        return val;
    }

    @Override
    public boolean isItem() {
        return true;
    }

    @Override
    public void setId(long id) {
        // todo: check: is valid dat
        this.clientId = id;
    }

    @Override
    public long getId() {
        return clientId;
    }

    @Override
    protected ThingType getThingType() {
        return ThingTypeManager.getInstance().getThingType((int) clientId, DatAttrs.ThingCategory.ThingCategoryItem);
    }

    public void setCountOrSubType(int countOrSubType) {
        this.countOrSubType = countOrSubType;
    }

    @Override
    public void draw(Point dest, float scaleFActor) {
        // LOGGER.info("drawing item: {}", clientId);
        AtomicInteger patternX = new AtomicInteger(0);
        AtomicInteger patternY = new AtomicInteger(0);
        AtomicInteger patternZ = new AtomicInteger(0);

        calculatePatterns(patternX, patternY, patternZ);

        getThingType().draw(dest, scaleFActor, 0, patternX.get(), patternY.get(), patternZ.get(), 0);
    }

    private void calculatePatterns(AtomicInteger patternX, AtomicInteger patternY, AtomicInteger patternZ) {
        if (isStackable() && getNumPatternX() == 4 && getNumPatternY() == 2) {
            if(countOrSubType <= 0) {
                patternX.set(0);
                patternY.set(0);
            } else if(countOrSubType < 5) {
                patternX.set(countOrSubType-1);
                patternY.set(0);
            } else if(countOrSubType < 10) {
                patternX.set(0);
                patternY.set(1);
            } else if(countOrSubType < 25) {
                patternX.set(1);
                patternY.set(1);
            } else if(countOrSubType < 50) {
                patternX.set(2);
                patternY.set(1);
            } else {
                patternX.set(3);
                patternY.set(1);
            }
        } else if (isHangable()) {
            Tile tile = getTile();
            if (tile != null) {
                if (tile.mustHookSouth()) {
                    patternX.set(getNumPatternX() >= 2 ? 1 : 0);
                } else if (tile.mustHookEast()) {
                    patternX.set(getNumPatternX() >= 3 ? 2 : 0);
                }
            }
        } else if (isSplash() || isFluidContainer()) {
            int color;
            switch (countOrSubType) {
                case Consts.Fluid.NONE:
                    color = Consts.FluidColor.TRANSPARENT;
                    break;
                case Consts.Fluid.WATER:
                case Consts.Fluid.WINE:
                    color = Consts.FluidColor.BLUE;
                    break;
                case Consts.Fluid.SLIME:
                    color = Consts.FluidColor.GREEN;
                    break;
                case Consts.Fluid.LEMONADE:
                case Consts.Fluid.URINE:
                case Consts.Fluid.FRUIT_JUICE:
                    color = Consts.FluidColor.YELLOW;
                    break;
                case Consts.Fluid.MILK:
                    color = Consts.FluidColor.WHITE;
                    break;
                case Consts.Fluid.COCONUT_MILK:
                    color = Consts.FluidColor.WHITE;
                    break;
                case Consts.Fluid.MANA:
                    color = Consts.FluidColor.PURPLE;
                    break;
                case Consts.Fluid.BEER:
                case Consts.Fluid.OIL:
                case Consts.Fluid.MUD:
                case Consts.Fluid.RUM:
                case Consts.Fluid.TEA:
                case Consts.Fluid.MEAD:
                    color = Consts.FluidColor.BROWN;
                    break;
                case Consts.Fluid.BLOOD:
                case Consts.Fluid.HEALTH:
                    color = Consts.FluidColor.RED;
                    break;
                default:
                    color = Consts.FluidColor.TRANSPARENT;
                    break;
            }

            patternX.set((color % 4) % getNumPatternX());
            patternY.set((color / 4) % getNumPatternY());
        } else {
            patternX.set(getPosition().getX() % getNumPatternX());
            patternY.set(getPosition().getY() % getNumPatternY());
            patternZ.set(getPosition().getZ() % getNumPatternZ());
        }
    }
}
