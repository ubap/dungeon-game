package dunegon.game;

import dunegon.io.DatAttrs;

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
}
