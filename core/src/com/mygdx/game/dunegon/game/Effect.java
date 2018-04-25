package com.mygdx.game.dunegon.game;

import com.mygdx.game.dunegon.io.DatAttrs;
import com.mygdx.game.dunegon.io.ThingTypeManager;

public class Effect extends Thing {
    private long id;

    public static Effect create(int id) {
        Effect val = new Effect();
        val.setId(id);
        return val;
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
        return ThingTypeManager.getInstance().getThingType((int) id, DatAttrs.ThingCategory.ThingCategoryEffect);
    }

    @Override
    public boolean isEffect() {
        return true;
    }

    @Override
    public void onAppear() {

    }

    @Override
    public void onDisappear() {

    }
}
