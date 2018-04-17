package dunegon.game;

import dunegon.io.DatAttrs;

public class Effect extends Thing {
    private int id;

    public static Effect create(int id) {
        Effect val = new Effect();
        val.setId(id);
        return val;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    protected ThingType getThingType() {
        return ThingTypeManager.getInstance().getThingType(id, DatAttrs.ThingCategory.ThingCategoryEffect);
    }

    @Override
    public boolean isEffect() {
        return true;
    }
}
