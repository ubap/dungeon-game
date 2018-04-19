package com.mygdx.game.dunegon.game;

import com.mygdx.game.dunegon.io.DatAttrs;
import com.mygdx.game.dunegon.io.ThingTypeManager;
import com.mygdx.game.graphics.Point;

import java.util.concurrent.atomic.AtomicInteger;

public class Item extends Thing {

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
    public void draw(Point dest) {
        AtomicInteger patternX = new AtomicInteger(0);
        AtomicInteger patternY = new AtomicInteger(0);
        AtomicInteger patternZ = new AtomicInteger(0);

        calculatePatterns(patternX, patternY, patternZ);

        getThingType().draw(dest, 1, 0, patternX.get(), patternY.get(), patternZ.get(), 0);
    }

    private void calculatePatterns(AtomicInteger patternX, AtomicInteger patternY, AtomicInteger patternZ) {
        patternX.set(getPosition().getX() % getNumPatternX());
        patternY.set(getPosition().getY() % getNumPatternY());
        patternZ.set(getPosition().getZ() % getNumPatternZ());
    }
}
