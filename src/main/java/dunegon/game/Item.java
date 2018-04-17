package dunegon.game;

import dunegon.io.DatAttrs;

public class Item extends Thing {

    private int clientId;

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
    public void setId(int id) {
        // todo: check: is valid dat
        this.clientId = id;
    }

    @Override
    protected ThingType getThingType() {
        return ThingTypeManager.getInstance().getThingType(clientId, DatAttrs.ThingCategory.ThingCategoryItem);
    }
}
